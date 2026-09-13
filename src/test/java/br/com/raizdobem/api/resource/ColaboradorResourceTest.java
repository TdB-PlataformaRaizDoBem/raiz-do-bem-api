package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.request.AtualizarColaboradorDTO;
import br.com.raizdobem.api.dto.request.CriarColaboradorDTO;
import br.com.raizdobem.api.entity.Colaborador;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.service.ColaboradorService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - ColaboradorResource")
class ColaboradorResourceTest {

    @Mock
    private ColaboradorService service;

    @InjectMocks
    private ColaboradorResource resource;

    private Colaborador criarColaborador(Long id, String cpf) {
        Colaborador c = new Colaborador();
        c.setId(id);
        c.setNomeCompleto("João Silva");
        c.setCpf(cpf);
        c.setEmail("joao@raizdobem.org");
        c.setDataNascimento(LocalDate.of(1990, 5, 20));
        c.setDataContratacao(LocalDate.now());
        return c;
    }

    @Test
    @DisplayName("Deve criar colaborador com sucesso e retornar HTTP 201 Created")
    void deveCriarColaboradorComSucesso() {
        // Arrange
        CriarColaboradorDTO request = new CriarColaboradorDTO(
                "12345678901",
                "João Silva",
                LocalDate.of(1990, 5, 20),
                LocalDate.now(),
                "joao@raizdobem.org",
                "Senha@123",
                "COLABORADOR"
        );
        Colaborador colaborador = criarColaborador(1L, "12345678901");
        when(service.criarColaborador(request)).thenReturn(colaborador);

        // Act
        Response response = resource.criar(request);

        // Assert
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getEntity()).isEqualTo(colaborador);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException quando criação de colaborador retornar nulo")
    void deveLancarNaoEncontradoExceptionQuandoColaboradorRetornadoForNulo() {
        // Arrange
        CriarColaboradorDTO request = new CriarColaboradorDTO(
                "12345678901",
                "João Silva",
                LocalDate.of(1990, 5, 20),
                LocalDate.now(),
                "joao@raizdobem.org",
                "Senha@123",
                "COLABORADOR"
        );
        when(service.criarColaborador(request)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> resource.criar(request))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Dados de colaborador inválidos.");
    }

    @Test
    @DisplayName("Deve listar todos os colaboradores retornando HTTP 200")
    void deveListarTodosRetornandoStatus200() {
        // Arrange
        when(service.listarTodos()).thenReturn(List.of(criarColaborador(1L, "12345678901")));

        // Act
        Response response = resource.listarTodos();

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isNotNull();
    }

    @Test
    @DisplayName("Deve buscar colaborador único por CPF")
    void deveBuscarUnicoPorCpf() {
        // Arrange
        String cpf = "12345678901";
        Colaborador colaborador = criarColaborador(1L, cpf);
        when(service.exibirColaborador(cpf)).thenReturn(colaborador);

        // Act
        Colaborador resultado = resource.buscarUnico(cpf);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getCpf()).isEqualTo(cpf);
    }

    @Test
    @DisplayName("Deve atualizar colaborador e retornar HTTP 200")
    void deveAtualizarColaboradorRetornandoStatus200() {
        // Arrange
        String cpf = "12345678901";
        AtualizarColaboradorDTO dto = new AtualizarColaboradorDTO("novoemail@raizdobem.org", "NovaSenha@123");

        // Act
        Response response = resource.atualizar(cpf, dto);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        verify(service, times(1)).atualizarColaborador(cpf, dto);
    }

    @Test
    @DisplayName("Deve excluir colaborador com sucesso retornando HTTP 204 No Content")
    void deveExcluirColaboradorRetornandoStatus204() {
        // Arrange
        String cpf = "12345678901";
        when(service.excluir(cpf)).thenReturn(1L);

        // Act
        Response response = resource.excluir(cpf);

        // Assert
        assertThat(response.getStatus()).isEqualTo(204);
        verify(service, times(1)).excluir(cpf);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao excluir colaborador inexistente")
    void deveLancarNaoEncontradoExceptionAoExcluirColaboradorInexistente() {
        // Arrange
        String cpf = "00000000000";
        when(service.excluir(cpf)).thenReturn(0L);

        // Act & Assert
        assertThatThrownBy(() -> resource.excluir(cpf))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Colaborador não encontrado.");
    }
}
