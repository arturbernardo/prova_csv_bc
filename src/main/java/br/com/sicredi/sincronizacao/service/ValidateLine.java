package br.com.sicredi.sincronizacao.service;

import java.util.List;

record ValidateLine(Boolean success, List<String> errors){}

