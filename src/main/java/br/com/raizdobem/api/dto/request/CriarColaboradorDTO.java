package br.com.raizdobem.api.dto.request;

import java.time.LocalDate;

public record CriarColaboradorDTO(String cpf,
                                 String nomeCompleto,
                                 LocalDate dataNascimento,
                                 LocalDate dataContratacao,
                                 String email) {
}
