package br.com.raizdobem.api.dto;

import br.com.raizdobem.api.entity.Endereco;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CriarPedidoAjudaDTO {
    @Pattern(regexp = "^\\d{11}$")
    private String cpf;
    private String nome;
    private LocalDate dataNascimento;
    private String sexo;
    private String telefone;
    private String email;
    private String descricaoProblema;
    private EnderecoRequestDTO endereco;
}
