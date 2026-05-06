package br.com.raizdobem.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class CriarDentistaDTO {
    @Pattern(regexp = "^(?i)CRO[-/ ]?[A-Z]{2}[-/ ]?\\d{4,7}$")
    private String croDentista;

    @Pattern(regexp = "^\\d{11}$")
    private String cpf;
    private String nomeCompleto;
    private String sexo;
    private String email;
    private String telefone;
    private String categoria;
    private String disponivel;
    private long id_endereco;
}
