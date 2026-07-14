package br.com.drs.radiotv_pro_escritorio.util;

import java.security.SecureRandom;

public class CriarChaves {

    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String gerarChaveUsuario() {
        return gerar(4).toUpperCase();
    }

    public static String gerarChavePrimeiroAcesso() {
        return gerar(40).toLowerCase();
    }

    public static String gerarChaveTrocaSenha() {
        return gerar(45).toLowerCase();
    }

    private static String gerar(int tamanho) {
        StringBuilder sb = new StringBuilder(tamanho);
        for (int i = 0; i < tamanho; i++) {
            sb.append(CARACTERES.charAt(RANDOM.nextInt(CARACTERES.length())));
        }
        return sb.toString();
    }
}