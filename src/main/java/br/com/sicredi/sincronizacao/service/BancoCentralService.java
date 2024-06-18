package br.com.sicredi.sincronizacao.service;

import br.com.sicredi.sincronizacao.dto.ContaDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class BancoCentralService {

  private static final int MIN_AGENCIA_LENGTH = 4;

  private static final int MIN_CONTA_LENGTH = 7;

  private final Random random = new Random();

  /**
   * Realiza o envio da conta para atualização junto ao Banco Central
   * @param contaDTO dados da conta a ser atualizada
   * @return <b>true</b> se a conta foi atualizada ou <b>false</b> caso contrário.
   */
  public boolean atualizaConta(ContaDTO contaDTO) {
    validarConta(contaDTO);
    return random.nextBoolean();
  }

  private void validarConta(ContaDTO contaDTO) {
    if(StringUtils.isAnyBlank(contaDTO.agencia(), contaDTO.conta())
       && !validarTamanhos(contaDTO.agencia(), contaDTO.conta())) {
      throw new IllegalArgumentException();
    }
  }

  private boolean validarTamanhos(String agencia, String conta) {
    return agencia.length() >= MIN_AGENCIA_LENGTH && conta.length() >= MIN_CONTA_LENGTH;
  }
}
