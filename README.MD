# Disclaimer
Criei um código simples com foco no controle do gasto de memória e chamando o serviço com múltiplas threads.

# Analise
Com 4 milhões de clientes, e crescendo, parece possível que o arquivo de input chegue a centenas de MegaBytes de
tamanho, fazendo com que estratégias descuidadas level aplicação a gastar GigaBytes de memória.
O uso de BufferedReader e BufferedWriter garante que o Java use estratégias inteligentes de consumo de memória e acesso a disco.
Outro ponto sensível é a chamada a um serviço externo, por isso criei uma thread para cada core disponível. Como não há 
lógica que exija do processador, pode-se analisar o aumento do número de threads.


# Solução
CSV de output contem os dados de entrada, um status indicando caso de sucesso ou falha
e os motivos da falha.

Em casos de erro de sincronização com o Banco Central, a linha encontrada será como esta:

```457920683,322820590,1071064152,ERROR,SYNC```

Em casos de erro de dados de entrada, a linha encontrada será como as seguintes:

```,48920-7,29.96,ERROR,VALIDATION: agencia```

```6123,,716.88,ERROR,VALIDATION: conta```

```6123,48920-7,,ERROR,VALIDATION: saldo```

```,,181.13,ERROR,VALIDATION: agencia|conta```

# Rodar o sistema

* Existe um script que simplifica build e roda o sistema em seguida.

```./compile.sh "/Users/user/Desktop/Teste T‚cnico -alterado/Teste T‚cnico/backend/DATA.csv"```

# Gerar mais dados para testes
* Caso um teste maior seja desejado, para observar o consumo de memória e de processamento, o seguinte script pode ser usado.
Primeiro parâmetro corresponde ao número de linhas desejadas, o segundo o nome do arquivo java que gera o CSV.

```./compile_generate_data.sh "4000000" "GeraArquivoData"```

# Benchmark
Em testes com 4 milhões de registro o consumo de memória não passa de 150Mb, processado a solução, de acordo com o 
@MeasuredExecutionTime, em 6 segundos (6.040924969   100%).

Máquina utilizada:
* 2 GHz i7 com 4 cores
* 16 GB Ram
* SSD

