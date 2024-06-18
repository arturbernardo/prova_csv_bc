package br.com.sicredi.sincronizacao.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GeraArquivoData {
    private static final String FILE_OUTPUT = "DATA.csv";
    private static final String header_agencia = "agencia";
    private static final String header_conta = "conta";
    private static final String header_saldo = "saldo";
    private static final Random random = new Random();

    public static void main(String[] args) throws IOException {
        if (args.length == 0 || !args[0].matches("^[1-9]\\d*$")) {
            System.exit(0);
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_OUTPUT));
        bufferedWriter.write(header_agencia +", "+header_conta+", " + header_saldo);
        bufferedWriter.newLine();

        for (int i = 0; i < Integer.parseInt(args[0]); i++) {
            bufferedWriter.write(Math.abs(random.nextInt()) +","+Math.abs(random.nextInt())+"," + Math.abs(random.nextInt()));
            bufferedWriter.newLine();
        }

        bufferedWriter.close();

        System.out.println(new File(FILE_OUTPUT).getAbsolutePath());
    }
}
