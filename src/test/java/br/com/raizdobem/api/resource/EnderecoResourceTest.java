package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.external.ViaCepDTO;
import br.com.raizdobem.api.dto.request.EnderecoCompletoRequest;
import br.com.raizdobem.api.dto.response.EnderecoResponse;
import br.com.raizdobem.api.entity.Endereco;
import br.com.raizdobem.api.entity.TipoEndereco;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.service.EnderecoService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - EnderecoResource")
class EnderecoResourceTest {

    @Mock
    private EnderecoService service;

    @InjectMocks
    private EnderecoResource resource;

    private Endereco criarEndereco(Long id) {
        Endereco e = new Endereco();
        e.setId(id);
        e.setCep("01001000");
        e.setLogradouro("Praça da Sé");
        e.setNumero("100");
        e.setBairro("Sé");
        e.setCidade("São Paulo");
        e.setEstado("SP");
        e.setTipoEndereco(TipoEndereco.RESIDENCIAL);
        return e;
    }

    private EnderecoResponse criarEnderecoResponse(Long id) {
        return new EnderecoResponse(
                id,
                "Praça da Sé",
                "01001000",
                "100",
                "Sé",
                "São Paulo",
                "SP",
                "RESIDENCIAL"
        );
    }

    @Test
    @DisplayName("Deve criar endereço com sucesso e retornar HTTP 201 Created")
    void deveCriarEnderecoComSucesso() {
        // Arrange
        EnderecoCompletoRequest request = new EnderecoCompletoRequest("01001000", "100", "RESIDENCIAL");
        Endereco endereco = criarEndereco(1L);
        when(service.criar(request)).thenReturn(endereco);

        // Act
        Response response = resource.criar(request);

        // Assert
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getEntity()).isEqualTo(endereco);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao criar com tipo de endereço nulo")
    void deveLancarNaoEncontradoExceptionQuandoTipoEnderecoForNulo() {
        // Arrange
        EnderecoCompletoRequest request = new EnderecoCompletoRequest("01001000", "100", "INVALIDO");
        Endereco endereco = new Endereco();
        endereco.setTipoEndereco(null);
        when(service.criar(request)).thenReturn(endereco);

        // Act & Assert
        assertThatThrownBy(() -> resource.criar(request))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Tipo de endereço inválido.");
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao criar quando CEP for vazio")
    void deveLancarNaoEncontradoExceptionQuandoCepForVazio() {
        // Arrange
        EnderecoCompletoRequest request = new EnderecoCompletoRequest("", "100", "RESIDENCIAL");
        Endereco endereco = new Endereco();
        endereco.setTipoEndereco(TipoEndereco.RESIDENCIAL);
        when(service.criar(request)).thenReturn(endereco);

        // Act & Assert
        assertThatThrownBy(() -> resource.criar(request))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("CEP não encontrado.");
    }

    @Test
    @DisplayName("Deve listar todos os endereços retornando HTTP 200")
    void deveListarTodosRetornandoStatus200() {
        // Arrange
        when(service.listarTodos()).thenReturn(List.of(criarEnderecoResponse(1L)));

        // Act
        Response response = resource.listarTodos();

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isNotNull();
    }

    @Test
    @DisplayName("Deve listar endereços por cidade retornando HTTP 200")
    void deveListarPorCidadeRetornandoStatus200() {
        // Arrange
        when(service.listarPorCidades("Campinas")).thenReturn(List.of(criarEnderecoResponse(1L)));

        // Act
        Response response = resource.listarPorCidade("Campinas");

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isNotNull();
    }

    @Test
    @DisplayName("Deve retornar lista vazia e HTTP 200 ao listar por cidade sem resultados")
    void deveRetornarListaVaziaAoListarPorCidadeSemResultados() {
        // Arrange
        when(service.listarPorCidades("CidadeInexistente")).thenReturn(List.of());

        // Act
        Response response = resource.listarPorCidade("CidadeInexistente");

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isEqualTo(List.of());
    }

    @Test
    @DisplayName("Deve buscar endereço por ID retornando HTTP 200")
    void deveBuscarPorIdRetornandoStatus200() {
        // Arrange
        EnderecoResponse endereco = criarEnderecoResponse(10L);
        when(service.buscaPorId(10L)).thenReturn(endereco);

        // Act
        Response response = resource.buscarEndereco(10L);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isEqualTo(endereco);
    }

    @Test
    @DisplayName("Deve buscar endereço pelo ViaCEP com sucesso retornando HTTP 200")
    void deveBuscarViaCepComSucesso() {
        // Arrange
        String cep = "01001000";
        ViaCepDTO viaCep = new ViaCepDTO(cep, "Praça da Sé", "Sé", "São Paulo", "SP", "São Paulo", null);
        when(service.buscarEndereco(cep)).thenReturn(viaCep);

        // Act
        Response response = resource.buscarViaCep(cep);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isEqualTo(viaCep);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException quando ViaCEP retornar nulo")
    void deveLancarNaoEncontradoExceptionQuandoViaCepRetornarNulo() {
        // Arrange
        String cep = "99999999";
        when(service.buscarEndereco(cep)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> resource.buscarViaCep(cep))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Endereço não encontrado na Api do ViaCep.");
    }

    @Test
    @DisplayName("Deve atualizar endereço com sucesso retornando HTTP 200")
    void deveAtualizarEnderecoRetornandoStatus200() {
        // Arrange
        long id = 1L;
        EnderecoCompletoRequest request = new EnderecoCompletoRequest("01001000", "200", "RESIDENCIAL");
        Endereco atualizado = criarEndereco(id);
        atualizado.setNumero("200");
        when(service.atualizarEndereco(id, request)).thenReturn(atualizado);

        // Act
        Response response = resource.atualizar(id, request);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isEqualTo(atualizado);
    }

    @Test
    @DisplayName("Deve excluir endereço com sucesso retornando HTTP 204 No Content")
    void deveExcluirEnderecoRetornandoStatus204() {
        // Arrange
        long id = 1L;
        when(service.excluir(id)).thenReturn(true);

        // Act
        Response response = resource.excluir(id);

        // Assert
        assertThat(response.getStatus()).isEqualTo(204);
        verify(service, times(1)).excluir(id);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao falhar na exclusão de endereço")
    void deveLancarNaoEncontradoExceptionAoFalharExclusao() {
        // Arrange
        long id = 999L;
        when(service.excluir(id)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> resource.excluir(id))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Endereço não encontrado.");
    }
}
