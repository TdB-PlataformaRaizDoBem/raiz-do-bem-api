package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.response.DentistaResponse;
import br.com.raizdobem.api.dto.response.PageResponse;
import br.com.raizdobem.api.entity.Dentista;
import br.com.raizdobem.api.entity.Endereco;
import br.com.raizdobem.api.entity.Sexo;
import br.com.raizdobem.api.entity.TipoEndereco;
import br.com.raizdobem.api.repository.DentistaRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - Paginação de Dentista")
class DentistaPaginacaoTest {

    @Mock
    private DentistaRepository repository;

    @InjectMocks
    private DentistaService dentistaService;

    private Dentista criarDentista(Long id) {
        Endereco e = new Endereco();
        e.setId(1L);
        e.setTipoEndereco(TipoEndereco.PROFISSIONAL);
        e.setCep("01310100");
        e.setLogradouro("Avenida Paulista");
        e.setNumero("1000");
        e.setBairro("Bela Vista");
        e.setCidade("São Paulo");
        e.setEstado("SP");

        Dentista d = new Dentista();
        d.setId(id);
        d.setCpf("52998224725");
        d.setCroDentista("123456");
        d.setNomeCompleto("Dra. Camila");
        d.setSexo(Sexo.F);
        d.setEmail("camila@odonto.com");
        d.setTelefone("11988887777");
        d.setCategoria("COORDENADOR");
        d.setDisponivel("true");
        d.setEndereco(e);
        d.setEspecialidades(List.of());
        d.setProgramasSociais(List.of());
        return d;
    }

    @Test
    @DisplayName("Caminho Feliz - Deve paginar dentistas com sucesso e retornar PageResponse")
    @SuppressWarnings("unchecked")
    void devePaginarDentistasComSucesso() {
        // Arrange
        int pagina = 0;
        int tamanho = 10;
        Dentista dentista = criarDentista(1L);
        PanacheQuery<Dentista> queryMock = mock(PanacheQuery.class);
        when(queryMock.list()).thenReturn(List.of(dentista));
        when(queryMock.count()).thenReturn(1L);
        when(queryMock.pageCount()).thenReturn(1);
        when(repository.listagemPaginacao(pagina, tamanho)).thenReturn(queryMock);

        // Act
        PageResponse<DentistaResponse> resultado = dentistaService.listarComPaginacao(pagina, tamanho);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.conteudo()).hasSize(1);
        assertThat(resultado.conteudo().getFirst().nomeCompleto()).isEqualTo("Dra. Camila");
        assertThat(resultado.pagina()).isEqualTo(pagina);
        assertThat(resultado.tamanho()).isEqualTo(tamanho);
        assertThat(resultado.totalElementos()).isEqualTo(1L);
        assertThat(resultado.totalPaginas()).isEqualTo(1);
        verify(repository, times(1)).listagemPaginacao(pagina, tamanho);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando paginação de dentistas não encontrar dados")
    @SuppressWarnings("unchecked")
    void deveRetornarPaginaVaziaQuandoNaoEncontrarDentistas() {
        // Arrange
        int pagina = 5;
        int tamanho = 10;
        PanacheQuery<Dentista> queryMock = mock(PanacheQuery.class);
        when(queryMock.list()).thenReturn(List.of());
        when(queryMock.count()).thenReturn(0L);
        when(queryMock.pageCount()).thenReturn(0);
        when(repository.listagemPaginacao(pagina, tamanho)).thenReturn(queryMock);

        // Act
        PageResponse<DentistaResponse> resultado = dentistaService.listarComPaginacao(pagina, tamanho);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.conteudo()).isEmpty();
        assertThat(resultado.totalElementos()).isZero();
        assertThat(resultado.totalPaginas()).isZero();
        verify(repository, times(1)).listagemPaginacao(pagina, tamanho);
    }
}
