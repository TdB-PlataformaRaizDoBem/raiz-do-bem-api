package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.response.ColaboradorResponse;
import br.com.raizdobem.api.dto.response.PageResponse;
import br.com.raizdobem.api.entity.Colaborador;
import br.com.raizdobem.api.repository.ColaboradorRepository;
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
@DisplayName("Testes Unitários - Paginação de Colaborador")
class ColaboradorPaginacaoTest {

    @Mock
    private ColaboradorRepository repository;

    @InjectMocks
    private ColaboradorService colaboradorService;

    private Colaborador criarColaborador(Long id) {
        Colaborador c = new Colaborador();
        c.setId(id);
        c.setCpf("52998224725");
        c.setNomeCompleto("João Silva");
        c.setDataNascimento(LocalDate.of(1990, 5, 20));
        c.setDataContratacao(LocalDate.of(2022, 1, 10));
        c.setEmail("joao@raizdobem.org");
        c.setRole("COLABORADOR");
        return c;
    }

    @Test
    @DisplayName("Caminho Feliz - Deve paginar colaboradores com sucesso e retornar PageResponse")
    @SuppressWarnings("unchecked")
    void devePaginarColaboradoresComSucesso() {
        // Arrange
        int pagina = 0;
        int tamanho = 10;
        Colaborador colaborador = criarColaborador(1L);
        PanacheQuery<Colaborador> queryMock = mock(PanacheQuery.class);
        when(queryMock.list()).thenReturn(List.of(colaborador));
        when(queryMock.count()).thenReturn(1L);
        when(queryMock.pageCount()).thenReturn(1);
        when(repository.listagemPaginacao(pagina, tamanho)).thenReturn(queryMock);

        // Act
        PageResponse<ColaboradorResponse> resultado = colaboradorService.listarComPaginacao(pagina, tamanho);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.conteudo()).hasSize(1);
        assertThat(resultado.conteudo().getFirst().nomeCompleto()).isEqualTo("João Silva");
        assertThat(resultado.pagina()).isEqualTo(pagina);
        assertThat(resultado.tamanho()).isEqualTo(tamanho);
        assertThat(resultado.totalElementos()).isEqualTo(1L);
        assertThat(resultado.totalPaginas()).isEqualTo(1);
        verify(repository, times(1)).listagemPaginacao(pagina, tamanho);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando paginação de colaboradores não encontrar dados")
    @SuppressWarnings("unchecked")
    void deveRetornarPaginaVaziaQuandoNaoEncontrarColaboradores() {
        // Arrange
        int pagina = 5;
        int tamanho = 10;
        PanacheQuery<Colaborador> queryMock = mock(PanacheQuery.class);
        when(queryMock.list()).thenReturn(List.of());
        when(queryMock.count()).thenReturn(0L);
        when(queryMock.pageCount()).thenReturn(0);
        when(repository.listagemPaginacao(pagina, tamanho)).thenReturn(queryMock);

        // Act
        PageResponse<ColaboradorResponse> resultado = colaboradorService.listarComPaginacao(pagina, tamanho);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.conteudo()).isEmpty();
        assertThat(resultado.totalElementos()).isZero();
        assertThat(resultado.totalPaginas()).isZero();
        verify(repository, times(1)).listagemPaginacao(pagina, tamanho);
    }
}
