package br.com.raizdobem.api.dto;

import br.com.raizdobem.api.entity.Endereco;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CriarBeneficiarioDTO {
    @Pattern(regexp = "^\\d{11}$")
    private String cpf;
    private String nomeCompleto;
    private LocalDate dataNascimento;
    private String telefone;
    private String email;
    private long idPedidoAjuda;
    private Endereco endereco;
    private long idProgramaSocial;
}
