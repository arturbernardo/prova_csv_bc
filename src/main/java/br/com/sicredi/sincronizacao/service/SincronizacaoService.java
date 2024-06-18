package br.com.sicredi.sincronizacao.service;

import br.com.sicredi.sincronizacao.dto.ContaDTO;
import br.com.sicredi.sincronizacao.timer.MeasuredExecutionTime;
import br.com.sicredi.sincronizacao.utils.OutputColor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SincronizacaoService {

  @Value("${synchronization.output_file}")
  private String FILE_OUTPUT;
  @Value("${synchronization.csv.column_separator}")
  private String COLUMN_SEPARATOR;
  @Value("${synchronization.csv.header_agencia}")
  private String HEADER_AGENCIA;
  @Value("${synchronization.csv.header_conta}")
  private String HEADER_CONTA;
  @Value("${synchronization.csv.header_saldo}")
  private String HEADER_SALDO;
  @Value("${synchronization.csv.header_status}")
  private String HEADER_STATUS;
  @Value("${synchronization.csv.header_reason}")
  private String HEADER_REASON;
  @Value("${synchronization.csv.line.success}")
  private String SUCCESS;
  @Value("${synchronization.csv.line.error}")
  private String ERROR;
  @Value("${synchronization.csv.line.error.reason.sync}")
  private String SYNC_REASON ;
  @Value("${synchronization.csv.line.error.reason.prefix}")
  private String INPUT_REASON;
  @Value("${synchronization.invalidInputMessage}")
  private String invalidInputMessage;
  @Value("${synchronization.readWrite}")
  private String readWrite;
  private final String NEW_LINE = System.lineSeparator();
  @Autowired
  BancoCentralService bancoCentralService;

  @MeasuredExecutionTime
  public void syncAccounts(String[] args) throws IOException {
    if (args.length == 0) {
      log.warn(errorMessage(invalidInputMessage));
    } else {
      File output = getFile(FILE_OUTPUT);
      try (BufferedReader reader = new BufferedReader(new FileReader(args[0]));
           BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_OUTPUT))) {

        //executor deve existir em um contexto inferior ao BufferedReader e BufferedWriter
        //garantindo que as thread serão encerradas ainda com o acesso ao disco disponível.
        ThreadPoolExecutor executor = getThreadPoolExecutor();
        writer.write(getHeaders());

        //jump headers
        reader.readLine();

        String line;
        while ((line = reader.readLine()) != null) {
          String[] lineArray = line.split(COLUMN_SEPARATOR);
          ValidateLine validateLine = validateInput(lineArray);

          if (validateLine.success) {
            ContaDTO contaDTO = new ContaDTO(lineArray[0], lineArray[1], Double.parseDouble(lineArray[2]));
            CompletableFuture.runAsync(() -> {
              boolean success = bancoCentralService.atualizaConta(contaDTO);
              try {
                writer.append(buildLine(lineArray, success));
              } catch (IOException e) {
                log.error(readWrite, e);
              }
            }, executor);
          } else {
            writer.append(buildLineValidationError(lineArray, validateLine));
          }
        }
        executor.shutdown();
      } catch (IOException e) {
        log.error(readWrite, e);
      }
      log.info(successMessage(output.getAbsolutePath()));
    }
  }

  private String buildLineValidationError(String[] lineArray, ValidateLine validateLine) {
    return Stream.of(lineArray[0],
                    lineArray[1],
                    lineArray[2],
                    ERROR,
                    INPUT_REASON + validateLine.errors.stream().collect(Collectors.joining("|")))
            .collect(Collectors.joining(COLUMN_SEPARATOR)) + NEW_LINE;
  }

  private String buildLine(String[] lineArray, boolean success) {
    return Stream.of(lineArray[0],
            lineArray[1],
            lineArray[2],
            (success ? SUCCESS : ERROR),
            (success ? "" : SYNC_REASON))
            .collect(Collectors.joining(COLUMN_SEPARATOR)) + NEW_LINE;
  }

  private String getHeaders() {
    return Stream.of(HEADER_AGENCIA, HEADER_CONTA, HEADER_SALDO, HEADER_STATUS, HEADER_REASON)
            .collect(Collectors.joining(COLUMN_SEPARATOR)) + NEW_LINE;
  }

  private File getFile(String file) throws IOException {
    File output = new File(file);
    if (output.exists()) {
      output.delete();
    }
    output.createNewFile();
    return output;
  }

  private ThreadPoolExecutor getThreadPoolExecutor() {
    int cores = Runtime.getRuntime().availableProcessors();

    ThreadPoolExecutor executor = new ThreadPoolExecutor(
            cores,
            cores,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(cores),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );
    return executor;
  }

  record ValidateLine(Boolean success, List<String> errors){}

  private ValidateLine validateInput(String[] line) {
    List<String> errors = new ArrayList<>();
    if (StringUtils.isEmpty(line[0])) errors.add(HEADER_AGENCIA);
    if (StringUtils.isEmpty(line[1])) errors.add(HEADER_CONTA);
    if (StringUtils.isEmpty(line[2]) || !line[2].matches("[0-9.]+")) errors.add(HEADER_SALDO);

    return new ValidateLine(errors.size() == 0, errors);
  }

  private String errorMessage(String message) {
    return OutputColor.WHITE_BACKGROUND_BRIGHT+OutputColor.ANSI_RED +message+OutputColor.ANSI_RESET;
  }

  private String successMessage(String path) {
    return OutputColor.WHITE_BACKGROUND_BRIGHT+OutputColor.GREEN_BOLD_BRIGHT+path+OutputColor.ANSI_RESET;
  }
}
