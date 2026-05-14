package br.com.raizdobem.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record CriarColaboradorDTO(@Pattern(regexp = "^\\d{11}$",
                                          message = "CPF deve conter 11 números.")
                                  String cpf,
                                  @NotBlank
                                  String nomeCompleto,
                                  LocalDate dataNascimento,
                                  LocalDate dataContratacao,
                                  @Email String email) {
}
