package br.com.raizdobem.api.dto.request;

import br.com.raizdobem.api.entity.StatusPedido;

public record PedidoAjudaUpdateRequest(StatusPedido statusPedido,
                                       Long idDentista) {
}
