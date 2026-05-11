package br.com.raizdobem.api.dto.response;

import br.com.raizdobem.api.entity.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoAjudaDTO {
    private Long id;
    private String cpf;
    private String nomeCompleto;
    private LocalDate dataNascimento;
    private String telefone;
    private String email;
    private String descricaoProblema;
    private LocalDate dataPedido;
    private StatusPedido status;
    private String dentistaAprovador;
}
