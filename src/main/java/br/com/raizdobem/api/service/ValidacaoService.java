package br.com.raizdobem.api.service;

public class ValidacaoService {
    public static boolean validarCpf(String cpf){
        return cpf != null && cpf.matches("\\d{11}");
    }
}
