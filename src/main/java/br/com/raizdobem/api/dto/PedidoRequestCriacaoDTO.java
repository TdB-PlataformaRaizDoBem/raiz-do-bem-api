package br.com.raizdobem.api.dto;

import br.com.raizdobem.api.model.Sexo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class PedidoRequestCriacaoDTO {
    private String cpf;
    private String nomeCompleto;
    private LocalDate dataNascimento;
    private Sexo sexo;
    private String telefone;
    private String email;
    private String descricaoProblema;
}
