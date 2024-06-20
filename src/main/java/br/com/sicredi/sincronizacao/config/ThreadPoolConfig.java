package br.com.sicredi.sincronizacao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {
    @Bean
    public Executor taskExecutor() {
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
}
