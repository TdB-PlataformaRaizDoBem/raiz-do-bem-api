package br.com.raizdobem.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class AtualizarAtendimentoDTO {
    private String prontuario;
    private int idColaborador;
    private final LocalDate dataFinal = LocalDate.now();
}
