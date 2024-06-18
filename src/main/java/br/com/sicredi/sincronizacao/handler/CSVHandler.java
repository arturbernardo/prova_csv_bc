package br.com.sicredi.sincronizacao.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CSVHandler {
    @Value("${synchronization.csv.column_separator}")
    public String COLUMN_SEPARATOR;
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
    private final String NEW_LINE = System.lineSeparator();

    public ValidateLine validateInput(String[] line) {
        if (line.length < 3) return new ValidateLine(false, new ArrayList<>());
        List<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(line[0])) errors.add(HEADER_AGENCIA);
        if (StringUtils.isEmpty(line[1])) errors.add(HEADER_CONTA);
        if (StringUtils.isEmpty(line[2]) || !line[2].matches("[0-9.]+")) errors.add(HEADER_SALDO);

        return new ValidateLine(errors.size() == 0, errors);
    }

    public String buildLineValidationError(String[] lineArray, ValidateLine validateLine) {
        return Stream.of(lineArray[0],
                        lineArray[1],
                        lineArray[2],
                        ERROR,
                        INPUT_REASON + validateLine.errors().stream().collect(Collectors.joining("|")))
                .collect(Collectors.joining(COLUMN_SEPARATOR)) + NEW_LINE;
    }

    public String buildLine(String[] lineArray, boolean success) {
        return Stream.of(lineArray[0],
                        lineArray[1],
                        lineArray[2],
                        (success ? SUCCESS : ERROR),
                        (success ? "" : SYNC_REASON))
                .collect(Collectors.joining(COLUMN_SEPARATOR)) + NEW_LINE;
    }

    public String getHeaders() {
        return Stream.of(HEADER_AGENCIA, HEADER_CONTA, HEADER_SALDO, HEADER_STATUS, HEADER_REASON)
                .collect(Collectors.joining(COLUMN_SEPARATOR)) + NEW_LINE;
    }
}
