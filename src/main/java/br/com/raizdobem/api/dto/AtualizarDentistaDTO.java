package br.com.raizdobem.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AtualizarDentistaDTO {
    private String telefone;
    private String email;
    private String categoriaDentista;
    private int idEndereco;
    private String disponivel;
}
