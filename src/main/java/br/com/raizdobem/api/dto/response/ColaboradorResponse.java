package br.com.raizdobem.api.dto.response;

import java.time.LocalDate;

public record ColaboradorResponse(Long id, String cpf, String nomeCompleto, LocalDate dataNascimento,
                                  LocalDate dataContratacao, String email, String role) {
}
