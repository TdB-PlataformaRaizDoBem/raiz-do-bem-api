package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.entity.Endereco;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - EnderecoRepository")
class EnderecoRepositoryTest {

    @Spy
    private EnderecoRepository repository;

    @Mock
    private PanacheQuery<Endereco> queryMock;

    @Test
    @DisplayName("Deve persistir endereço ao chamar criar")
    void devePersistirEnderecoAoChamarCriar() {
        Endereco e = new Endereco();
        doNothing().when(repository).persist(e);

        repository.criar(e);

        verify(repository, times(1)).persist(e);
    }

    @Test
    @DisplayName("Deve listar todos os endereços")
    void deveListarTodosOsEnderecos() {
        List<Endereco> esperado = List.of(new Endereco());
        doReturn(esperado).when(repository).listAll();

        List<Endereco> resultado = repository.listarTodos();

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve buscar endereço por CEP")
    void deveBuscarEnderecoPorCep() {
        Endereco esperado = new Endereco();
        doReturn(queryMock).when(repository).find("cep", "01001000");
        when(queryMock.firstResult()).thenReturn(esperado);

        Endereco resultado = repository.buscarPorCep("01001000");

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve listar endereços por cidade")
    void deveListarEnderecosPorCidade() {
        List<Endereco> esperado = List.of(new Endereco());
        doReturn(esperado).when(repository).list("cidade = ?1", "São Paulo");

        List<Endereco> resultado = repository.listarPorCidade("São Paulo");

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve buscar endereço por ID")
    void deveBuscarEnderecoPorId() {
        Endereco esperado = new Endereco();
        doReturn(esperado).when(repository).findById(9L);

        Endereco resultado = repository.buscarPeloId(9L);

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve persistir endereço ao chamar atualizar")
    void devePersistirEnderecoAoChamarAtualizar() {
        Endereco e = new Endereco();
        doNothing().when(repository).persist(e);

        repository.atualizar(e);

        verify(repository, times(1)).persist(e);
    }

    @Test
    @DisplayName("Deve excluir endereço por ID")
    void deveExcluirEnderecoPorId() {
        doReturn(true).when(repository).deleteById(9L);

        boolean excluido = repository.excluir(9L);

        assertThat(excluido).isTrue();
    }
}
