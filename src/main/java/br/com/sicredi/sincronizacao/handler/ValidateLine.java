package br.com.sicredi.sincronizacao.handler;

import java.util.List;

public record ValidateLine(Boolean success, List<String> errors){}

