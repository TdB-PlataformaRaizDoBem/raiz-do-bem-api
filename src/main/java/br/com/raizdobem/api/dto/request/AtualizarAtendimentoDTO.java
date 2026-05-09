package br.com.raizdobem.api.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class AtualizarAtendimentoDTO {
    private String prontuario;
    private Long idColaborador;
    private LocalDate dataFinal;
}
