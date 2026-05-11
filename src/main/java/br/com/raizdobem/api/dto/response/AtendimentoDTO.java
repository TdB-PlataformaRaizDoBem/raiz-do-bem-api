package br.com.raizdobem.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtendimentoDTO {
    private Long id;
    private String prontuario;
    private String beneficiario;
    private String dentista;
    private LocalDate dataInicial;
    private String dataFim;
}
