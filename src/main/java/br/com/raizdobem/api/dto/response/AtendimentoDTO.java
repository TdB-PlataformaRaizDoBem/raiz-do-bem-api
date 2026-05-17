package br.com.raizdobem.api.dto.response;

import java.time.LocalDate;

public record AtendimentoDTO(Long id,
                             String prontuario,
                             String beneficiario,
                             String dentista,
                             String contatoDentista,
                             String emailDentista,
                             String enderecoDentista,
                             LocalDate dataInicial,
                             String dataFim) {
}
