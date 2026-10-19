package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.entity.Atendimento;
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
@DisplayName("Testes Unitários - AtendimentoRepository")
class AtendimentoRepositoryTest {

    @Spy
    private AtendimentoRepository repository;

    @Mock
    private PanacheQuery<Atendimento> queryMock;

    @Test
    @DisplayName("Deve persistir atendimento ao chamar criar")
    void devePersistirAtendimentoAoChamarCriar() {
        Atendimento a = new Atendimento();
        doNothing().when(repository).persist(a);

        repository.criar(a);

        verify(repository, times(1)).persist(a);
    }

    @Test
    @DisplayName("Deve listar todos os atendimentos")
    void deveListarTodosOsAtendimentos() {
        List<Atendimento> esperado = List.of(new Atendimento());
        doReturn(esperado).when(repository).listAll();

        List<Atendimento> resultado = repository.listarTodos();

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve retornar PanacheQuery paginada em listagemPaginacao")
    void deveRetornarQueryPaginada() {
        doReturn(queryMock).when(repository).findAll();
        when(queryMock.page(any(Page.class))).thenReturn(queryMock);

        PanacheQuery<Atendimento> resultado = repository.listagemPaginacao(1, 10);

        assertThat(resultado).isSameAs(queryMock);
        ArgumentCaptor<Page> pageCaptor = ArgumentCaptor.forClass(Page.class);
        verify(queryMock).page(pageCaptor.capture());
        assertThat(pageCaptor.getValue().index).isEqualTo(1);
        assertThat(pageCaptor.getValue().size).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve buscar atendimento pelo CPF do beneficiário")
    void deveBuscarAtendimentoPeloCpfDoBeneficiario() {
        Atendimento esperado = new Atendimento();
        doReturn(queryMock).when(repository).find("beneficiario.cpf", "52998224725");
        when(queryMock.firstResult()).thenReturn(esperado);

        Atendimento resultado = repository.buscarPeloCpf("52998224725");

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve excluir atendimento por ID")
    void deveExcluirAtendimentoPorId() {
        doReturn(true).when(repository).deleteById(4L);

        boolean excluido = repository.excluir(4L);

        assertThat(excluido).isTrue();
    }
}
