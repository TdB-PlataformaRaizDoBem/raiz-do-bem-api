package br.com.raizdobem.api.dto.request;

import java.time.LocalDate;

public record AtualizarAtendimentoDTO(String prontuario,
                                      Long idColaborador,
                                      LocalDate dataFinal) {
}
