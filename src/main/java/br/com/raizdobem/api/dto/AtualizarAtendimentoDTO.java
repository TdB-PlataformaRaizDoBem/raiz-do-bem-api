package br.com.raizdobem.api.dto;

import br.com.raizdobem.api.entity.Colaborador;
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
