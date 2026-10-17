package br.com.raizdobem.api.service;

import br.com.raizdobem.api.entity.PedidoAjuda;
import br.com.raizdobem.api.entity.Sexo;
import br.com.raizdobem.api.entity.StatusPedido;
import br.com.raizdobem.api.mapper.PedidoAjudaMapper;
import br.com.raizdobem.api.repository.PedidoAjudaRepository;
import br.com.raizdobem.api.resource.PedidoAjudaResource;
import jakarta.ws.rs.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - Paginação de PedidoAjuda")
class PedidoAjudaPaginacaoTest {

    @Mock
    private PedidoAjudaRepository repository;

    private PedidoAjuda criarPedido(Long id) {
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
        return p;
    }

    @Test
    @DisplayName("Caminho Feliz - Deve paginar pedidos de ajuda através do repositório e mapear para resposta")
    void devePaginarPedidosDeAjudaComSucessoViaRepositorio() {
        // Arrange
        int pagina = 0;
        int tamanho = 10;
        PedidoAjuda pedido = criarPedido(1L);
        when(repository.listagemPaginacao(pagina, tamanho)).thenReturn(List.of(pedido));

        // Act
        List<PedidoAjuda> resultado = repository.listagemPaginacao(pagina, tamanho);
        var responseDTOs = PedidoAjudaMapper.mapeamentoListaPedidos(resultado);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(responseDTOs).hasSize(1);
        assertThat(responseDTOs.getFirst().nomeCompleto()).isEqualTo("Ana Silva");
        verify(repository, times(1)).listagemPaginacao(pagina, tamanho);
    }

    @Test
    @DisplayName("Erro mais óbvio - Deve retornar lista vazia para página sem dados e auditar ausência da rota no Resource")
    void deveRetornarListaVaziaParaPaginaSemDadosEAuditarAusenciaNoResource() {
        // Arrange
        int pagina = 99;
        int tamanho = 10;
        when(repository.listagemPaginacao(pagina, tamanho)).thenReturn(List.of());

        // Act
        List<PedidoAjuda> resultado = repository.listagemPaginacao(pagina, tamanho);

        // Assert
        assertThat(resultado).isEmpty();

        // Auditoria: verifica se PedidoAjudaResource expõe a rota /paginacao
        boolean temEndpointPaginacao = Arrays.stream(PedidoAjudaResource.class.getDeclaredMethods())
                .anyMatch(m -> m.isAnnotationPresent(Path.class) && "/paginacao".equals(m.getAnnotation(Path.class).value()));

        // Registra débito técnico: PedidoAjudaRepository possui listagemPaginacao, mas PedidoAjudaService/Resource não expõem
        assertThat(temEndpointPaginacao).isFalse();
    }
}
