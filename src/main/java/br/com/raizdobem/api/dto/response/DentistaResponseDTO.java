package br.com.raizdobem.api.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DentistaResponseDTO {
    private Long id;
    private String croDentista;
    private String cpf;
    private String nomeCompleto;
    private String sexo;
    private String email;
    private String telefone;
    private String categoria;
    private String disponivel;
    private String logradouro;
    private String numero;
}




