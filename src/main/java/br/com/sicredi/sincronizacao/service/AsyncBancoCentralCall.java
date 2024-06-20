package br.com.sicredi.sincronizacao.service;

import br.com.sicredi.sincronizacao.dto.ContaDTO;
import br.com.sicredi.sincronizacao.handler.CSVHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;

@Component
@Slf4j
public class AsyncBancoCentralCall {

    @Value("${synchronization.readWriteError}")
    private String readWriteError;

    @Async
    public void executeAndWrite(BufferedWriter writer, String[] lineArray, ContaDTO contaDTO, BancoCentralService service, CSVHandler csvHandler) {
        System.out.println(Thread.currentThread().getName());
        boolean success = service.atualizaConta(contaDTO);
        try {
            writer.append(csvHandler.buildLine(lineArray, success));
        } catch (IOException e) {
            log.error(readWriteError, e);
        }
    }
}
