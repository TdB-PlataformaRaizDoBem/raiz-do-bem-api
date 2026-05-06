package br.com.raizdobem.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class BeneficiarioRequestCriacaoDTO {
    private String cpf;
    private String nomeCompleto;
    private LocalDate dataNascimento;
    private String telefone;
    private String email;
    private int idPedidoAjuda;
    private int idEndereco;
    private int idProgramaSocial;
}
