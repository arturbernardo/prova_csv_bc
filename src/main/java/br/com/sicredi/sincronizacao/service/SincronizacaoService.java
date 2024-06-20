package br.com.sicredi.sincronizacao.service;

import br.com.sicredi.sincronizacao.dto.ContaDTO;
import br.com.sicredi.sincronizacao.handler.CSVHandler;
import br.com.sicredi.sincronizacao.handler.ValidateLine;
import br.com.sicredi.sincronizacao.timer.MeasuredExecutionTime;
import br.com.sicredi.sincronizacao.utils.OutputColor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

@Service
@RequiredArgsConstructor
@Slf4j
public class SincronizacaoService {

  @Value("${synchronization.output_file}")
  private String FILE_OUTPUT;
  @Value("${synchronization.invalidInputMessage}")
  private String invalidInputMessage;
  @Value("${synchronization.readWriteError}")
  private String readWriteError;
  @Autowired
  private BancoCentralService bancoCentralService;
  @Autowired
  private CSVHandler csvHandler;

  @Autowired
  private AsyncBancoCentralCall asyncBancoCentralCall;

  @MeasuredExecutionTime
  public void syncAccounts(String[] args) throws IOException, InterruptedException {
    if (args.length == 0) {
      log.warn(errorMessage(invalidInputMessage));
    } else {
      File output = getFile(FILE_OUTPUT);

      try {
        BufferedReader reader = new BufferedReader(new FileReader(args[0]));
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_OUTPUT));
        writer.write(csvHandler.getHeaders());
        //jump headers
        reader.readLine();

        String line;
        while ((line = reader.readLine()) != null) {
          String[] lineArray = line.split(csvHandler.COLUMN_SEPARATOR);
          ValidateLine validateLine = csvHandler.validateInput(lineArray);

          if (validateLine.success()) {
            ContaDTO contaDTO = new ContaDTO(lineArray[0], lineArray[1], Double.parseDouble(lineArray[2]));
            asyncBancoCentralCall.executeAndWrite(writer, lineArray, contaDTO, bancoCentralService, csvHandler);
          } else {
            writer.append(csvHandler.buildLineValidationError(lineArray, validateLine));
          }
        }
      } catch (IOException e) {
        log.error(readWriteError, e);
      }
      log.info(successMessage(output.getAbsolutePath()));
    }
  }

  private String errorMessage(String message) {
    return OutputColor.WHITE_BACKGROUND_BRIGHT+OutputColor.ANSI_RED +message+OutputColor.ANSI_RESET;
  }

  private String successMessage(String path) {
    return OutputColor.WHITE_BACKGROUND_BRIGHT+OutputColor.GREEN_BOLD_BRIGHT+path+OutputColor.ANSI_RESET;
  }

  private File getFile(String file) throws IOException {
    File output = new File(file);
    if (output.exists()) {
      output.delete();
    }
    output.createNewFile();
    return output;
  }
}
