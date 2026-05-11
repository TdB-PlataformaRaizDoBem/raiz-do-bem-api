package br.com.raizdobem.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiarioDTO {
    private Long id;
    private String cpf;
    private String nomeCompleto;
    private LocalDate dataNascimento;
    private String telefone;
    private String email;
    private PedidoAjudaResumidoDTO pedido;
    private String programaSocial;
    private EnderecoDTO endereco;
}
