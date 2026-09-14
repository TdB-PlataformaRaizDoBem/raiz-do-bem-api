package br.com.raizdobem.api.mapper;

import br.com.raizdobem.api.dto.response.PedidoAjudaDTO;
import br.com.raizdobem.api.entity.Dentista;
import br.com.raizdobem.api.entity.Endereco;
import br.com.raizdobem.api.entity.PedidoAjuda;
import br.com.raizdobem.api.entity.StatusPedido;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários - PedidoAjudaMapper")
class PedidoAjudaMapperTest {

    private PedidoAjuda criarPedido(Long id, boolean comDentista, boolean comEndereco) {
        PedidoAjuda pedido = new PedidoAjuda();
        pedido.setId(id);
        pedido.setCpf("52998224725");
        pedido.setNomeCompleto("Paciente Teste");
        pedido.setDataNascimento(LocalDate.of(2005, 5, 15));
        pedido.setTelefone("11988887777");
        pedido.setEmail("paciente@teste.com");
        pedido.setDescricaoProblema("Necessita de restauração");
        pedido.setDataPedido(LocalDate.of(2026, 3, 1));
        pedido.setStatus(StatusPedido.PENDENTE);

        if (comDentista) {
            Dentista dentista = new Dentista();
            dentista.setId(10L);
            dentista.setNomeCompleto("Dra. Beatriz Santos");
            pedido.setDentista(dentista);
        }

        if (comEndereco) {
            Endereco endereco = new Endereco();
            endereco.setLogradouro("Rua das Palmeiras");
            endereco.setNumero("123");
            endereco.setCidade("Campinas");
            endereco.setEstado("SP");
            pedido.setEndereco(endereco);
        }

        return pedido;
    }

    @Test
    @DisplayName("Deve retornar null quando pedido fornecido for null")
    void deveRetornarNullQuandoPedidoForNull() {
        // Act
        PedidoAjudaDTO resultado = PedidoAjudaMapper.mapeamentoPedido(null);

        // Assert
        assertThat(resultado).isNull();
    }

    @Test
    @DisplayName("Deve mapear pedido completo com dentista e endereço preenchidos")
    void deveMapearPedidoCompleto() {
        // Arrange
        PedidoAjuda pedido = criarPedido(1L, true, true);

        // Act
        PedidoAjudaDTO dto = PedidoAjudaMapper.mapeamentoPedido(pedido);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.cpf()).isEqualTo("52998224725");
        assertThat(dto.nomeCompleto()).isEqualTo("Paciente Teste");
        assertThat(dto.dentistaResponsavel()).isEqualTo("Dra. Beatriz Santos");
        assertThat(dto.endereco()).isEqualTo("Rua das Palmeiras, 123, Campinas, SP");
        assertThat(dto.status()).isEqualTo(StatusPedido.PENDENTE);
    }

    @Test
    @DisplayName("Deve definir 'Aguardando aprovação' e endereco null quando dentista e endereco forem nulos")
    void deveMapearPedidoSemDentistaESemEndereco() {
        // Arrange
        PedidoAjuda pedido = criarPedido(2L, false, false);

        // Act
        PedidoAjudaDTO dto = PedidoAjudaMapper.mapeamentoPedido(pedido);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(2L);
        assertThat(dto.dentistaResponsavel()).isEqualTo("Aguardando aprovação");
        assertThat(dto.endereco()).isNull();
    }

    @Test
    @DisplayName("Deve retornar null ao mapear lista quando a lista for nula")
    void deveRetornarNullAoMapearListaNula() {
        // Act
        List<PedidoAjudaDTO> resultado = PedidoAjudaMapper.mapeamentoListaPedidos(null);

        // Assert
        assertThat(resultado).isNull();
    }

    @Test
    @DisplayName("Deve mapear lista de pedidos com múltiplos elementos")
    void deveMapearListaPedidosComSucesso() {
        // Arrange
        PedidoAjuda p1 = criarPedido(1L, true, true);
        PedidoAjuda p2 = criarPedido(2L, false, false);
        List<PedidoAjuda> lista = List.of(p1, p2);

        // Act
        List<PedidoAjudaDTO> dtos = PedidoAjudaMapper.mapeamentoListaPedidos(lista);

        // Assert
        assertThat(dtos).isNotNull().hasSize(2);
        assertThat(dtos.get(0).id()).isEqualTo(1L);
        assertThat(dtos.get(0).dentistaResponsavel()).isEqualTo("Dra. Beatriz Santos");
        assertThat(dtos.get(1).id()).isEqualTo(2L);
        assertThat(dtos.get(1).dentistaResponsavel()).isEqualTo("Aguardando aprovação");
    }
}
