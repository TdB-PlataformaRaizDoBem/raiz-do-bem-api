package br.com.raizdobem.api.dto.response;

import br.com.raizdobem.api.entity.StatusPedido;

import java.time.LocalDate;

public record PedidoAjudaDTO(Long id,
                             String cpf,
                             String nomeCompleto,
                             LocalDate dataNascimento,
                             String telefone,
                             String email,
                             String descricaoProblema,
                             LocalDate dataPedido,
                             StatusPedido status,
                             String dentistaAprovador) {

}
