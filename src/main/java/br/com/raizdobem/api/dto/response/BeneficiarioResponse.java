package br.com.raizdobem.api.dto.response;

import java.time.LocalDate;

public record BeneficiarioResponse(Long id,
                                   String cpf,
                                   String nomeCompleto,
                                   LocalDate dataNascimento,
                                   String telefone,
                                   String email,
                                   PedidoAjudaResumidoDTO pedido,
                                   String programaSocial,
                                   EnderecoResponse endereco) {
}
