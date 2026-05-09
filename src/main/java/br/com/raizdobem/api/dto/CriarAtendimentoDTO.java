package br.com.raizdobem.api.dto;

import br.com.raizdobem.api.entity.Beneficiario;
import br.com.raizdobem.api.entity.Dentista;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CriarAtendimentoDTO {
    private String prontuario;
    private LocalDate dataInicial;
    private LocalDate dataFinal = null;
    private Beneficiario beneficiario;
    private Dentista dentista;
}
