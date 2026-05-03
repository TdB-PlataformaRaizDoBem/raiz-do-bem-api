package br.com.raizdobem.api.exception;

public class NaoEncontradoException extends RuntimeException {

    public NaoEncontradoException(String mensagem){
        super(mensagem);
    }

}
