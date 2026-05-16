package br.com.raizdobem.api.dto.response;
public record DentistaResumido(Long id,
                            String croDentista,
                            String nomeCompleto,
                            String telefone,
                            String logradouro,
                            String numero,
                            String cidade,
                            String estado) {
        }