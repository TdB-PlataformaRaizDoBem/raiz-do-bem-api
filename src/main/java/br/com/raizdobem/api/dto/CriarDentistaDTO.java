package br.com.raizdobem.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DentistaRequestCriacaoDTO {
    private String croDentista;
    private String cpf;
    private String nomeCompleto;
    private String sexo;
    private String email;
    private String telefone;
    private String categoria;
    private String disponivel;
    private long id_endereco;
}
