package br.com.raizdobem.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CriarAtendimentoDTO {
    private String prontuario;
    private LocalDate dataInicial;
    private LocalDate dataFinal = null;
    private long idBeneficiario;
    private long idDentista;
}
