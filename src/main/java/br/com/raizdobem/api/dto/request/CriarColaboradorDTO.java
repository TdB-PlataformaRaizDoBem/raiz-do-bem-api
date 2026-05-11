package br.com.raizdobem.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record CriarColaboradorDTO(@NotBlank
                                  @Pattern(regexp = "^\\d{11}$",
                                          message = "CPF deve conter 11 números.")
                                  String cpf,

                                  @NotBlank
                                  String nomeCompleto,

                                  @NotBlank
                                  LocalDate dataNascimento,

                                  @NotBlank
                                  LocalDate dataContratacao,

                                  @NotBlank
                                  @Email String email) {
}
