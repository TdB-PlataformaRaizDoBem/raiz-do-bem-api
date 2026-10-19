package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.dto.request.BeneficiarioUpdateRequest;
import br.com.raizdobem.api.entity.Beneficiario;
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
@DisplayName("Testes Unitários - BeneficiarioRepository")
class BeneficiarioRepositoryTest {

    @Spy
    private BeneficiarioRepository repository;

    @Mock
    private PanacheQuery<Beneficiario> queryMock;

    @Test
    @DisplayName("Deve persistir beneficiário ao chamar criar")
    void devePersistirBeneficiarioAoChamarCriar() {
        Beneficiario b = new Beneficiario();
        doNothing().when(repository).persist(b);

        repository.criar(b);

        verify(repository, times(1)).persist(b);
    }

    @Test
    @DisplayName("Deve listar todos os beneficiários delegando para listAll")
    void deveListarTodosOsBeneficiarios() {
        List<Beneficiario> esperado = List.of(new Beneficiario());
        doReturn(esperado).when(repository).listAll();

        List<Beneficiario> resultado = repository.listarTodos();

        assertThat(resultado).isSameAs(esperado);
        verify(repository, times(1)).listAll();
    }

    @Test
    @DisplayName("Deve retornar PanacheQuery paginada em listagemPaginacao")
    void deveRetornarQueryPaginada() {
        doReturn(queryMock).when(repository).findAll();
        when(queryMock.page(any(Page.class))).thenReturn(queryMock);

        PanacheQuery<Beneficiario> resultado = repository.listagemPaginacao(2, 15);

        assertThat(resultado).isSameAs(queryMock);
        ArgumentCaptor<Page> pageCaptor = ArgumentCaptor.forClass(Page.class);
        verify(queryMock).page(pageCaptor.capture());
        assertThat(pageCaptor.getValue().index).isEqualTo(2);
        assertThat(pageCaptor.getValue().size).isEqualTo(15);
    }

    @Test
    @DisplayName("Deve buscar beneficiário por CPF filtrando pelo campo cpf")
    void deveBuscarBeneficiarioPorCpf() {
        Beneficiario esperado = new Beneficiario();
        doReturn(queryMock).when(repository).find("cpf", "52998224725");
        when(queryMock.firstResult()).thenReturn(esperado);

        Beneficiario resultado = repository.buscarPorCpf("52998224725");

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve buscar beneficiário por ID delegando para findById")
    void deveBuscarBeneficiarioPorId() {
        Beneficiario esperado = new Beneficiario();
        doReturn(esperado).when(repository).findById(10L);

        Beneficiario resultado = repository.buscarPorId(10L);

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve listar beneficiários por cidade")
    void deveListarBeneficiariosPorCidade() {
        List<Beneficiario> esperado = List.of(new Beneficiario());
        doReturn(esperado).when(repository).list("endereco.cidade = ?1", "São Paulo");

        List<Beneficiario> resultado = repository.listarPorCidade("São Paulo");

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve listar beneficiários por programa social")
    void deveListarBeneficiariosPorPrograma() {
        List<Beneficiario> esperado = List.of(new Beneficiario());
        doReturn(esperado).when(repository).list("programaSocial.id = ?1", 1L);

        List<Beneficiario> resultado = repository.listarPorPrograma(1L);

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve consultar beneficiário por CPF no método atualizar")
    void deveConsultarBeneficiarioPorCpfNoMetodoAtualizar() {
        Beneficiario esperado = new Beneficiario();
        BeneficiarioUpdateRequest dto = new BeneficiarioUpdateRequest("11999990000", "teste@email.com", null);
        doReturn(queryMock).when(repository).find("cpf", "52998224725");
        when(queryMock.firstResult()).thenReturn(esperado);

        Beneficiario resultado = repository.atualizar("52998224725", dto);

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve excluir beneficiário por CPF delegando para delete")
    void deveExcluirBeneficiarioPorCpf() {
        doReturn(1L).when(repository).delete("cpf", "52998224725");

        long excluidos = repository.excluir("52998224725");

        assertThat(excluidos).isEqualTo(1L);
    }
}
