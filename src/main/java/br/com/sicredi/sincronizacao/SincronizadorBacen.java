package br.com.sicredi.sincronizacao;

import br.com.sicredi.sincronizacao.service.SincronizacaoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SincronizadorBacen {

	public static void main(String[] args) {
		SpringApplication.run(SincronizadorBacen.class, args).close();
	}

	@Bean
	public CommandLineRunner commandLineRunner(SincronizacaoService sincronizacaoService) {
		return args -> {
			sincronizacaoService.syncAccounts(args);
		};
	}

}
