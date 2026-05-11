package br.com.raizdobem.api.dto.response;

import java.time.LocalDateTime;

public record ErroDTO(int statusCode, String mensagem, LocalDateTime timestamp) {
}
