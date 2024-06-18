#!/bin/bash

if [ $# -eq 0 ]; then
    echo "Por favor, insira parâmetros."
    exit 1
fi

lines="$1"
main_file="$2"

javac src/main/java/br/com/sicredi/sincronizacao/utils/${main_file}.java
java -cp src/main/java br.com.sicredi.sincronizacao.utils.${main_file} ${lines}

echo "Finalizado"