package br.com.raizdobem.api.dto.response;

public record DentistaDTO(Long id,
                          String croDentista,
                          String cpf,
                          String nomeCompleto,
                          String sexo,
                          String email,
                          String telefone,
                          String categoria,
                          String disponivel,
                          String logradouro,
                          String numero,
                          String cidade,
                          String estado) {
}




