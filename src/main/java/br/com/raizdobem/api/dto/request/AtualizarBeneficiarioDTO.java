package br.com.raizdobem.api.dto.request;

import br.com.raizdobem.api.entity.Endereco;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AtualizarBeneficiarioDTO {
    private String telefone;
    private String email;
    private Endereco endereco;
}
