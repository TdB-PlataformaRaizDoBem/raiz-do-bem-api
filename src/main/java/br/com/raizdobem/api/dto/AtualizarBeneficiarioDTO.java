package br.com.raizdobem.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AtualizarBeneficiarioDTO {
    private String telefone;
    private String email;
    private int idEndereco;
}
