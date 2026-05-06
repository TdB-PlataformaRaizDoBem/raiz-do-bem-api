package br.com.raizdobem.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AtualizarPedidoAjudaDTO {
    private String StatusPedido;
    private int idDentista;
}
