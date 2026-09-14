package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.request.PedidoAjudaUpdateRequest;
import br.com.raizdobem.api.dto.request.PedidoAjudaCreateRequest;
import br.com.raizdobem.api.dto.request.EnderecoRequest;
import br.com.raizdobem.api.dto.response.PedidoAjudaDTO;
import br.com.raizdobem.api.entity.PedidoAjuda;
import br.com.raizdobem.api.entity.StatusPedido;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.mapper.PedidoAjudaMapper;
import br.com.raizdobem.api.service.PedidoAjudaService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - PedidoAjudaResource")
class PedidoAjudaResourceTest {

    @Mock
    private PedidoAjudaService service;

    @InjectMocks
    private PedidoAjudaResource resource;

    private PedidoAjudaDTO criarPedidoDTO(Long id) {
        return new PedidoAjudaDTO(
                id,
                "12345678901",
                "Carlos Souza",
                LocalDate.of(2010, 5, 20),
                "11988887777",
                "carlos@email.com",
                "Tratamento dentário",
                LocalDate.now(),
                StatusPedido.PENDENTE,
                "Aguardando aprovação",
                "Rua das Flores, 10, São Paulo, SP"
        );
    }

    @Test
    @DisplayName("Deve listar todos os pedidos de ajuda e retornar HTTP 200")
    void deveListarTodosRetornandoStatus200() {
        // Arrange
        PedidoAjudaDTO dto = criarPedidoDTO(1L);
        when(service.listarTodos()).thenReturn(List.of(dto));

        // Act
        Response response = resource.listarTodos();

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isNotNull();
    }

    @Test
    @DisplayName("Deve listar pedidos por data retornando HTTP 200 quando existirem registros")
    void deveListarPorDataRetornandoStatus200() {
        // Arrange
        String dataStr = "2026-03-10";
        LocalDate data = LocalDate.parse(dataStr);
        PedidoAjudaDTO dto = criarPedidoDTO(1L);
        when(service.listarPorData(data)).thenReturn(List.of(dto));

        // Act
        Response response = resource.listarPorData(dataStr);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException quando não houver pedidos na data consultada")
    void deveLancarNaoEncontradoExceptionQuandoNaoHouverPedidosNaData() {
        // Arrange
        String dataStr = "2026-03-10";
        LocalDate data = LocalDate.parse(dataStr);
        when(service.listarPorData(data)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThatThrownBy(() -> resource.listarPorData(dataStr))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Nenhum pedido de ajuda encontrado.");
    }

    @Test
    @DisplayName("Deve criar pedido de ajuda com sucesso retornando HTTP 201 Created")
    void deveCriarPedidoRetornandoStatus201() {
        // Arrange
        PedidoAjudaCreateRequest request = new PedidoAjudaCreateRequest(
                "12345678901",
                "Carlos Souza",
                LocalDate.of(2010, 5, 20),
                "M",
                "11988887777",
                "carlos@email.com",
                "Urgência",
                new EnderecoRequest("01001000", "10")
        );
        PedidoAjuda pedido = new PedidoAjuda();
        pedido.setId(1L);

        when(service.criar(request)).thenReturn(pedido);

        // Act
        Response response = resource.criar(request);

        // Assert
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getEntity()).isEqualTo(PedidoAjudaMapper.mapeamentoPedido(pedido));
    }

    @Test
    @DisplayName("Deve lançar RequisicaoInvalidaException quando serviço retornar nulo ao criar")
    void deveLancarRequisicaoInvalidaExceptionQuandoCriacaoRetornarNulo() {
        // Arrange
        PedidoAjudaCreateRequest request = new PedidoAjudaCreateRequest(
                "12345678901", "Nome", LocalDate.now(), "M", "tel", "email@email.com", "desc", null
        );
        when(service.criar(request)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> resource.criar(request))
                .isInstanceOf(RequisicaoInvalidaException.class)
                .hasMessage("Não foi possível criar o pedido de ajuda. Dados inválidos.");
    }

    @Test
    @DisplayName("Deve atualizar pedido de ajuda e retornar HTTP 200")
    void deveAtualizarPedidoRetornandoStatus200() {
        // Arrange
        long id = 1L;
        PedidoAjudaUpdateRequest dto = new PedidoAjudaUpdateRequest(StatusPedido.APROVADO, 5L);
        PedidoAjudaDTO pedidoAtualizado = criarPedidoDTO(id);

        when(service.processarPedido(id, dto)).thenReturn(pedidoAtualizado);

        // Act
        Response response = resource.atualizar(id, dto);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isEqualTo(pedidoAtualizado);
    }

    @Test
    @DisplayName("Deve excluir pedido de ajuda com sucesso retornando HTTP 204 No Content")
    void deveExcluirPedidoRetornandoStatus204() {
        // Arrange
        long id = 1L;
        when(service.excluir(id)).thenReturn(true);

        // Act
        Response response = resource.excluir(id);

        // Assert
        assertThat(response.getStatus()).isEqualTo(204);
        verify(service, times(1)).excluir(id);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao tentar excluir pedido inexistente")
    void deveLancarNaoEncontradoExceptionAoExcluirPedidoInexistente() {
        // Arrange
        long id = 99L;
        when(service.excluir(id)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> resource.excluir(id))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Pedido de ajuda não encontrado para exclusão.");
    }
}
