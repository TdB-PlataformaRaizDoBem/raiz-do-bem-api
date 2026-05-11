package br.com.raizdobem.api.dto.response;

import java.time.LocalDate;

public record AtendimentoDTO(Long id,
                             String prontuario,
                             String beneficiario,
                             String dentista,
                             LocalDate dataInicial,
                             String dataFim) {
}
