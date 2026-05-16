package br.com.raizdobem.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


import java.time.LocalDate;


public record CriarPedidoAjudaDTO(@NotBlank @Pattern(regexp = "^\\d{11}$",
                                            message = "CPF deve conter 11 números.")
                                  String cpf,

                                  @NotBlank
                                  String nome,

                                  @NotNull
                                  LocalDate dataNascimento,

                                  @NotBlank
                                  String sexo,

                                  @NotBlank
                                  String telefone,

                                  @NotBlank
                                  @Email
                                  String email,

                                  @NotBlank
                                  String descricaoProblema,

                                  @NotNull
                                  @Valid
                                  EntradaEnderecoCompletoDTO endereco) {

}
