package br.com.raizdobem.api.mapper;

import br.com.raizdobem.api.dto.response.PedidoAjudaDTO;
import br.com.raizdobem.api.entity.PedidoAjuda;
import br.com.raizdobem.api.exception.NaoEncontradoException;

import java.util.List;
import java.util.stream.Collectors;

public class PedidoAjudaMapper {
    public static PedidoAjudaDTO mapeamentoPedido(PedidoAjuda pedido){
        if(pedido == null){
            throw new NaoEncontradoException("Pedido inválido!");
        }

        return new PedidoAjudaDTO(
                pedido.getId(),
                pedido.getCpf(),
                pedido.getNomeCompleto(),
                pedido.getDataNascimento(),
                pedido.getTelefone(),
                pedido.getEmail(),
                pedido.getDescricaoProblema(),
                pedido.getDataPedido(),
                pedido.getStatus(),
                pedido.getDentista() != null ? pedido.getDentista().getNomeCompleto() : "Aguardando aprovação",
                pedido.getEndereco()!= null ? pedido.getEndereco().getLogradouro() + ", " + pedido.getEndereco().getNumero()
                + ", " + pedido.getEndereco().getCidade() + ", " + pedido.getEndereco().getEstado() : null
        );
    }

    public static List<PedidoAjudaDTO> mapeamentoListaPedidos(List<PedidoAjuda> pedidos){
        if (pedidos == null) {
            return null;
        }

        return pedidos.stream()
                .map(PedidoAjudaMapper ::mapeamentoPedido)
                .collect(Collectors.toList());
    }
}
