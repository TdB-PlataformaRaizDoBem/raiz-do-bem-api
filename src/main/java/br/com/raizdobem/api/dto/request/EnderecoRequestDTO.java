package br.com.raizdobem.api.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EnderecoRequestDTO {
    private String cep;
    private String numero;
    private String tipoEndereco;
}
