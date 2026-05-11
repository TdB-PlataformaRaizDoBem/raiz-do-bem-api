package br.com.raizdobem.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CriarDentistaDTO(
                              @NotBlank
                              @Pattern(regexp = "^(?i)CRO[-/ ]?[A-Z]{2}[-/ ]?\\d{4,7}$",
                                        message = "CRO inválido.")
                              String croDentista,

                              @NotBlank
                              @Pattern(regexp = "^\\d{11}$",
                                        message = "CPF deve conter 11 números.")
                              String cpf,

                              @NotBlank
                              String nomeCompleto,

                              @NotBlank
                              String sexo,

                              @NotBlank
                              @Email
                              String email,

                              @NotBlank
                              String telefone,

                              @NotBlank
                              String categoria,

                              @NotBlank
                              String disponivel,

                              @NotNull
                              @Valid
                              EntradaEnderecoDTO endereco) {
}
