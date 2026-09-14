package br.com.raizdobem.api.util;

public final class CpfValidatorUtil {
    private CpfValidatorUtil(){

    }

    public static boolean cpfValido(String cpf) {
        if (cpf == null) {
            return false;
        }

        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        return calcularDigitoVerificador(cpf, 9) == Character.getNumericValue(cpf.charAt(9))
                && calcularDigitoVerificador(cpf, 10) == Character.getNumericValue(cpf.charAt(10));
    }

    private static int calcularDigitoVerificador(String cpf, int quantidadeDigitos) {
        int soma = 0;
        int peso = quantidadeDigitos + 1;

        for (int i = 0; i < quantidadeDigitos; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (peso - i);
        }

        int resultado = (soma * 10) % 11;

        return resultado == 10 ? 0 : resultado;
    }
}
