package br.com.raizdobem.api.dto.request;

import br.com.raizdobem.api.entity.StatusPedido;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AtualizarPedidoAjudaDTO {
    private StatusPedido statusPedido;
    private Long idDentista;
}
