#!/bin/bash

if [ $# -eq 0 ]; then
    echo "Por favor, forneça o nome do arquivo de dados como parâmetro."
    exit 1
fi

file="$1"

./gradlew build
echo "Build complete"

java -jar build/libs/sincronizacao-0.0.1-SNAPSHOT.jar "${file}"
echo "Finalizado"

