package br.com.raizdobem.api.dto.request;

import jakarta.validation.constraints.Pattern;


public record CriarDentistaDTO(@Pattern(regexp = "^(?i)CRO[-/ ]?[A-Z]{2}[-/ ]?\\d{4,7}$")
                              String croDentista,

                              @Pattern(regexp = "^\\d{11}$")
                              String cpf,
                              String nomeCompleto,
                              String sexo,
                              String email,
                              String telefone,
                              String categoria,
                              String disponivel,
                              CriarEnderecoDTO endereco) {
}
