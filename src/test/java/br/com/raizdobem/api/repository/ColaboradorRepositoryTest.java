package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.dto.request.ColaboradorUpdateRequest;
import br.com.raizdobem.api.entity.Colaborador;
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
@DisplayName("Testes Unitários - ColaboradorRepository")
class ColaboradorRepositoryTest {

    @Spy
    private ColaboradorRepository repository;

    @Mock
    private PanacheQuery<Colaborador> queryMock;

    @Test
    @DisplayName("Deve persistir colaborador ao chamar criar")
    void devePersistirColaboradorAoChamarCriar() {
        Colaborador c = new Colaborador();
        doNothing().when(repository).persist(c);

        repository.criar(c);

        verify(repository, times(1)).persist(c);
    }

    @Test
    @DisplayName("Deve listar todos os colaboradores")
    void deveListarTodosOsColaboradores() {
        List<Colaborador> esperado = List.of(new Colaborador());
        doReturn(esperado).when(repository).listAll();

        List<Colaborador> resultado = repository.listarTodos();

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve retornar PanacheQuery paginada em listagemPaginacao")
    void deveRetornarQueryPaginada() {
        doReturn(queryMock).when(repository).findAll();
        when(queryMock.page(any(Page.class))).thenReturn(queryMock);

        PanacheQuery<Colaborador> resultado = repository.listagemPaginacao(0, 10);

        assertThat(resultado).isSameAs(queryMock);
        ArgumentCaptor<Page> pageCaptor = ArgumentCaptor.forClass(Page.class);
        verify(queryMock).page(pageCaptor.capture());
        assertThat(pageCaptor.getValue().index).isZero();
        assertThat(pageCaptor.getValue().size).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve buscar colaborador por CPF")
    void deveBuscarColaboradorPorCpf() {
        Colaborador esperado = new Colaborador();
        doReturn(queryMock).when(repository).find("cpf", "52998224725");
        when(queryMock.firstResult()).thenReturn(esperado);

        Colaborador resultado = repository.buscarPorCpf("52998224725");

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve buscar colaborador por ID")
    void deveBuscarColaboradorPorId() {
        Colaborador esperado = new Colaborador();
        doReturn(esperado).when(repository).findById(3L);

        Colaborador resultado = repository.buscarPorId(3L);

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve buscar colaborador por email")
    void deveBuscarColaboradorPorEmail() {
        Colaborador esperado = new Colaborador();
        doReturn(queryMock).when(repository).find("email", "admin@raizdobem.org");
        when(queryMock.firstResult()).thenReturn(esperado);

        Colaborador resultado = repository.buscarPorEmail("admin@raizdobem.org");

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve atualizar email e senha do colaborador por CPF")
    void deveAtualizarEmailESenhaPorCpf() {
        ColaboradorUpdateRequest dto = new ColaboradorUpdateRequest("novo@raizdobem.org", "hashNovaSenha");
        doReturn(1).when(repository).update(
                "email = ?1, senha = ?2 where cpf = ?3",
                "novo@raizdobem.org",
                "hashNovaSenha",
                "52998224725"
        );

        repository.atualizar("52998224725", dto);

        verify(repository, times(1)).update(
                "email = ?1, senha = ?2 where cpf = ?3",
                "novo@raizdobem.org",
                "hashNovaSenha",
                "52998224725"
        );
    }

    @Test
    @DisplayName("Deve excluir colaborador por CPF")
    void deveExcluirColaboradorPorCpf() {
        doReturn(1L).when(repository).delete("cpf", "52998224725");

        long excluidos = repository.excluir("52998224725");

        assertThat(excluidos).isEqualTo(1L);
    }
}
