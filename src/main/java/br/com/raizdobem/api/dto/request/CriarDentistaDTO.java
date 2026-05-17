package br.com.raizdobem.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CriarDentistaDTO(
                              @NotBlank
                              @Pattern(regexp = "^(?i)[A-Z0-9-/ ]{3,10}$", message = "CRO deve ter entre 3 e 10 caracteres.")
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
