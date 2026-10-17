package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.response.PageResponse;
import br.com.raizdobem.api.dto.response.PedidoAjudaResponse;
import br.com.raizdobem.api.entity.Endereco;
import br.com.raizdobem.api.entity.PedidoAjuda;
import br.com.raizdobem.api.entity.Sexo;
import br.com.raizdobem.api.entity.StatusPedido;
import br.com.raizdobem.api.entity.TipoEndereco;
import br.com.raizdobem.api.repository.PedidoAjudaRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - Paginação de PedidoAjuda")
class PedidoAjudaPaginacaoTest {

    @Mock
    private PedidoAjudaRepository repository;

    @InjectMocks
    private PedidoAjudaService pedidoAjudaService;

    private PedidoAjuda criarPedido(Long id) {
        Endereco e = new Endereco();
        e.setId(1L);
        e.setTipoEndereco(TipoEndereco.RESIDENCIAL);
        e.setCep("01001000");
        e.setLogradouro("Praça da Sé");
        e.setNumero("100");
        e.setBairro("Sé");
        e.setCidade("São Paulo");
        e.setEstado("SP");

        PedidoAjuda p = new PedidoAjuda();
        p.setId(id);
        p.setCpf("52998224725");
        p.setNomeCompleto("Ana Silva");
        p.setDataNascimento(LocalDate.of(2015, 6, 1));
        p.setSexo(Sexo.F);
        p.setTelefone("11988887777");
        p.setEmail("ana@email.com");
        p.setStatus(StatusPedido.PENDENTE);
        p.setDataPedido(LocalDate.now());
        p.setEndereco(e);
        return p;
    }

    @Test
    @DisplayName("Caminho Feliz - Deve paginar pedidos de ajuda com sucesso e retornar PageResponse")
    @SuppressWarnings("unchecked")
    void devePaginarPedidosDeAjudaComSucesso() {
        // Arrange
        int pagina = 0;
        int tamanho = 10;
        PedidoAjuda pedido = criarPedido(1L);
        PanacheQuery<PedidoAjuda> queryMock = mock(PanacheQuery.class);
        when(queryMock.list()).thenReturn(List.of(pedido));
        when(queryMock.count()).thenReturn(1L);
        when(queryMock.pageCount()).thenReturn(1);
        when(repository.listagemPaginacao(pagina, tamanho)).thenReturn(queryMock);

        // Act
        PageResponse<PedidoAjudaResponse> resultado = pedidoAjudaService.listarComPaginacao(pagina, tamanho);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.conteudo()).hasSize(1);
        assertThat(resultado.conteudo().getFirst().nomeCompleto()).isEqualTo("Ana Silva");
        assertThat(resultado.pagina()).isEqualTo(pagina);
        assertThat(resultado.tamanho()).isEqualTo(tamanho);
        assertThat(resultado.totalElementos()).isEqualTo(1L);
        assertThat(resultado.totalPaginas()).isEqualTo(1);
        verify(repository, times(1)).listagemPaginacao(pagina, tamanho);
    }

    @Test
    @DisplayName("Deve retornar página vazia para página sem dados em PedidoAjudaService")
    @SuppressWarnings("unchecked")
    void deveRetornarPaginaVaziaParaPaginaSemDados() {
        // Arrange
        int pagina = 99;
        int tamanho = 10;
        PanacheQuery<PedidoAjuda> queryMock = mock(PanacheQuery.class);
        when(queryMock.list()).thenReturn(List.of());
        when(queryMock.count()).thenReturn(0L);
        when(queryMock.pageCount()).thenReturn(0);
        when(repository.listagemPaginacao(pagina, tamanho)).thenReturn(queryMock);

        // Act
        PageResponse<PedidoAjudaResponse> resultado = pedidoAjudaService.listarComPaginacao(pagina, tamanho);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.conteudo()).isEmpty();
        assertThat(resultado.totalElementos()).isZero();
        assertThat(resultado.totalPaginas()).isZero();
        verify(repository, times(1)).listagemPaginacao(pagina, tamanho);
    }
}
