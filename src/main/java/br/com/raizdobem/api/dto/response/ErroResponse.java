package br.com.raizdobem.api.dto.response;

import java.time.LocalDateTime;

public record ErroResponse(int statusCode, String mensagem, LocalDateTime timestamp) {
}
