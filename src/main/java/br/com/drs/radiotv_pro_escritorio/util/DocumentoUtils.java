package br.com.drs.radiotv_pro_escritorio.util;

public class DocumentoUtils {

    public static boolean isCPF(String cpf) {
        if (cpf == null) return false;
        String limpo = cpf.replaceAll("\\D", ""); // Remove tudo que não for dígito

        if (limpo.length() != 11 || limpo.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0, resto;
            for (int i = 0; i < 9; i++) soma += Integer.parseInt(limpo.substring(i, i + 1)) * (10 - i);
            resto = (soma * 10) % 11;
            if (resto == 10 || resto == 11) resto = 0;
            if (resto != Integer.parseInt(limpo.substring(9, 10))) return false;

            soma = 0;
            for (int i = 0; i < 10; i++) soma += Integer.parseInt(limpo.substring(i, i + 1)) * (11 - i);
            resto = (soma * 10) % 11;
            if (resto == 10 || resto == 11) resto = 0;
            return resto == Integer.parseInt(limpo.substring(10, 11));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isCNPJ(String cnpj) {
        if (cnpj == null) return false;
        String limpo = cnpj.replaceAll("\\D", ""); // Remove tudo que não for dígito

        if (limpo.length() != 14 || limpo.matches("(\\d)\\1{13}")) return false;

        try {
            int[] peso1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

            int soma = 0;
            for (int i = 0; i < 12; i++) soma += Integer.parseInt(limpo.substring(i, i + 1)) * peso1[i];
            int digito1 = (soma % 11 < 2) ? 0 : 11 - (soma % 11);

            soma = 0;
            for (int i = 0; i < 13; i++) soma += Integer.parseInt(limpo.substring(i, i + 1)) * peso2[i];
            int digito2 = (soma % 11 < 2) ? 0 : 11 - (soma % 11);

            return limpo.endsWith("" + digito1 + digito2);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}