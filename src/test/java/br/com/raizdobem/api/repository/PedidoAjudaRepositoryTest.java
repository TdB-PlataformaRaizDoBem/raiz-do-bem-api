package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.dto.request.PedidoAjudaUpdateRequest;
import br.com.raizdobem.api.entity.PedidoAjuda;
import br.com.raizdobem.api.entity.StatusPedido;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - PedidoAjudaRepository")
class PedidoAjudaRepositoryTest {

    @Spy
    private PedidoAjudaRepository repository;

    @Mock
    private PanacheQuery<PedidoAjuda> queryMock;

    @Test
    @DisplayName("Deve listar todos os pedidos de ajuda")
    void deveListarTodosOsPedidos() {
        List<PedidoAjuda> esperado = List.of(new PedidoAjuda());
        doReturn(esperado).when(repository).listAll();

        List<PedidoAjuda> resultado = repository.listarTodos();

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve retornar PanacheQuery paginada em listagemPaginacao")
    void deveRetornarQueryPaginada() {
        doReturn(queryMock).when(repository).findAll();
        when(queryMock.page(any(Page.class))).thenReturn(queryMock);

        PanacheQuery<PedidoAjuda> resultado = repository.listagemPaginacao(0, 5);

        assertThat(resultado).isSameAs(queryMock);
        ArgumentCaptor<Page> pageCaptor = ArgumentCaptor.forClass(Page.class);
        verify(queryMock).page(pageCaptor.capture());
        assertThat(pageCaptor.getValue().index).isZero();
        assertThat(pageCaptor.getValue().size).isEqualTo(5);
    }

    @Test
    @DisplayName("Deve persistir pedido de ajuda ao chamar criar")
    void devePersistirPedidoAoChamarCriar() {
        PedidoAjuda p = new PedidoAjuda();
        doNothing().when(repository).persist(p);

        repository.criar(p);

        verify(repository, times(1)).persist(p);
    }

    @Test
    @DisplayName("Deve buscar pedido de ajuda por CPF")
    void deveBuscarPedidoPorCpf() {
        PedidoAjuda esperado = new PedidoAjuda();
        doReturn(queryMock).when(repository).find("cpf", "52998224725");
        when(queryMock.firstResult()).thenReturn(esperado);

        PedidoAjuda resultado = repository.buscarPorCpf("52998224725");

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve buscar pedido de ajuda por ID")
    void deveBuscarPedidoPorId() {
        PedidoAjuda esperado = new PedidoAjuda();
        doReturn(esperado).when(repository).findById(7L);

        PedidoAjuda resultado = repository.buscarPeloId(7L);

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve listar pedidos de ajuda por data")
    void deveListarPedidosPorData() {
        LocalDate data = LocalDate.of(2026, 5, 1);
        List<PedidoAjuda> esperado = List.of(new PedidoAjuda());
        doReturn(esperado).when(repository).list("dataPedido", data);

        List<PedidoAjuda> resultado = repository.listarPorData(data);

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve executar consulta por CPF no método atualizar")
    void deveExecutarConsultaPorCpfNoMetodoAtualizar() {
        PedidoAjudaUpdateRequest dto = new PedidoAjudaUpdateRequest(StatusPedido.APROVADO, 1L);
        doReturn(queryMock).when(repository).find("cpf", "52998224725");
        when(queryMock.firstResult()).thenReturn(new PedidoAjuda());

        repository.atualizar("52998224725", dto);

        verify(repository, times(1)).find("cpf", "52998224725");
        verify(queryMock, times(1)).firstResult();
    }

    @Test
    @DisplayName("Deve excluir pedido de ajuda por ID")
    void deveExcluirPedidoPorId() {
        doReturn(true).when(repository).deleteById(10L);

        boolean excluido = repository.excluir(10L);

        assertThat(excluido).isTrue();
    }
}
