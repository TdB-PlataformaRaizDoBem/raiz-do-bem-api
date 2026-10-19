package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.dto.request.DentistaUpdateRequest;
import br.com.raizdobem.api.entity.Dentista;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - DentistaRepository")
class DentistaRepositoryTest {

    @Spy
    private DentistaRepository repository;

    @Mock
    private PanacheQuery<Dentista> queryMock;

    @Test
    @DisplayName("Deve persistir dentista ao chamar criar")
    void devePersistirDentistaAoChamarCriar() {
        Dentista d = new Dentista();
        doNothing().when(repository).persist(d);

        repository.criar(d);

        verify(repository, times(1)).persist(d);
    }

    @Test
    @DisplayName("Deve listar todos os dentistas delegando para listAll")
    void deveListarTodosOsDentistas() {
        List<Dentista> esperado = List.of(new Dentista());
        doReturn(esperado).when(repository).listAll();

        List<Dentista> resultado = repository.listarTodos();

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve retornar PanacheQuery paginada em listagemPaginacao")
    void deveRetornarQueryPaginada() {
        doReturn(queryMock).when(repository).findAll();
        when(queryMock.page(any(Page.class))).thenReturn(queryMock);

        PanacheQuery<Dentista> resultado = repository.listagemPaginacao(1, 20);

        assertThat(resultado).isSameAs(queryMock);
        ArgumentCaptor<Page> pageCaptor = ArgumentCaptor.forClass(Page.class);
        verify(queryMock).page(pageCaptor.capture());
        assertThat(pageCaptor.getValue().index).isEqualTo(1);
        assertThat(pageCaptor.getValue().size).isEqualTo(20);
    }

    @Test
    @DisplayName("Deve buscar dentista por CPF")
    void deveBuscarDentistaPorCpf() {
        Dentista esperado = new Dentista();
        doReturn(queryMock).when(repository).find("cpf", "52998224725");
        when(queryMock.firstResult()).thenReturn(esperado);

        Dentista resultado = repository.buscarPorCpf("52998224725");

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve buscar dentista por ID delegando para findById")
    void deveBuscarDentistaPorId() {
        Dentista esperado = new Dentista();
        doReturn(esperado).when(repository).findById(5L);

        Dentista resultado = repository.buscarPorId(5L);

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve listar dentistas por cidade")
    void deveListarDentistasPorCidade() {
        List<Dentista> esperado = List.of(new Dentista());
        doReturn(esperado).when(repository).list("endereco.cidade = ?1", "Campinas");

        List<Dentista> resultado = repository.listarPorCidade("Campinas");

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve listar dentistas disponíveis filtrando por S ou true")
    void deveListarDentistasDisponiveis() {
        List<Dentista> esperado = List.of(new Dentista());
        doReturn(esperado).when(repository).list("disponivel = 'S' or disponivel = 'true'");

        List<Dentista> resultado = repository.listarDisponiveis();

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve consultar dentista por CPF no método atualizar")
    void deveConsultarDentistaPorCpfNoMetodoAtualizar() {
        Dentista esperado = new Dentista();
        DentistaUpdateRequest dto = new DentistaUpdateRequest("11999990000", "d@odonto.com", "VOLUNTARIO", 1L, "S", null);
        doReturn(queryMock).when(repository).find("cpf", "52998224725");
        when(queryMock.firstResult()).thenReturn(esperado);

        Dentista resultado = repository.atualizar("52998224725", dto);

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve excluir dentista por CPF")
    void deveExcluirDentistaPorCpf() {
        doReturn(1L).when(repository).delete("cpf", "52998224725");

        long excluidos = repository.excluir("52998224725");

        assertThat(excluidos).isEqualTo(1L);
    }
}
