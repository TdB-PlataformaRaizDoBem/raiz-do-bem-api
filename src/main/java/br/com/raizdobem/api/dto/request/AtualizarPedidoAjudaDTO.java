package br.com.raizdobem.api.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AtualizarPedidoAjudaDTO {
    private String statusPedido;
    private Long idDentista;
}
