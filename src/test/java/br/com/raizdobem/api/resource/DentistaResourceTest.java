package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.request.AtualizarDentistaDTO;
import br.com.raizdobem.api.dto.request.CriarDentistaDTO;
import br.com.raizdobem.api.dto.request.EntradaEnderecoDTO;
import br.com.raizdobem.api.dto.response.DentistaDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.service.DentistaService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - DentistaResource")
class DentistaResourceTest {

    @Mock
    private DentistaService service;

    @InjectMocks
    private DentistaResource resource;

    private DentistaDTO criarDTO(Long id, String cpf) {
        return new DentistaDTO(
                id,
                "123456",
                cpf,
                "Dra. Camila",
                "F",
                "camila@odonto.com",
                "11988887777",
                "COORDENADOR",
                List.of(),
                List.of(),
                "true",
                "Rua Boa Vista",
                "100",
                "São Paulo",
                "SP",
                "01001000"
        );
    }

    @Test
    @DisplayName("Deve criar dentista com sucesso retornando HTTP 201 Created")
    void deveCriarDentistaRetornandoStatus201() {
        // Arrange
        CriarDentistaDTO request = new CriarDentistaDTO(
                "123456", "12345678901", "Dra. Camila", "F", "camila@odonto.com",
                "11988887777", "COORDENADOR", 1L, "true", new EntradaEnderecoDTO("01001000", "100")
        );
        DentistaDTO responseDTO = criarDTO(1L, "12345678901");
        when(service.criarDentista(request)).thenReturn(responseDTO);

        // Act
        Response response = resource.criar(request);

        // Assert
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getEntity()).isEqualTo(responseDTO);
    }

    @Test
    @DisplayName("Deve lançar RequisicaoInvalidaException quando serviço retornar nulo ao criar dentista")
    void deveLancarRequisicaoInvalidaExceptionQuandoRetornarNulo() {
        // Arrange
        CriarDentistaDTO request = new CriarDentistaDTO(
                "123456", "12345678901", "Dra. Camila", "F", "camila@odonto.com",
                "11988887777", "COORDENADOR", 1L, "true", null
        );
        when(service.criarDentista(request)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> resource.criar(request))
                .isInstanceOf(RequisicaoInvalidaException.class)
                .hasMessage("Dados de dentista inválidos.");
    }

    @Test
    @DisplayName("Deve listar todos os dentistas retornando HTTP 200")
    void deveListarTodosRetornandoStatus200() {
        // Arrange
        when(service.listarTodos()).thenReturn(List.of(criarDTO(1L, "12345678901")));

        // Act
        Response response = resource.listarTodos();

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Deve listar dentistas disponíveis retornando HTTP 200")
    void deveListarDisponiveisRetornandoStatus200() {
        // Arrange
        when(service.listarDisponiveis()).thenReturn(List.of(criarDTO(1L, "12345678901")));

        // Act
        Response response = resource.listarDisponiveis();

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException quando lista de dentistas disponíveis for nula")
    void deveLancarNaoEncontradoExceptionQuandoListaDisponiveisForNula() {
        // Arrange
        when(service.listarDisponiveis()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> resource.listarDisponiveis())
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Lista de dentistas disponíveis vazia.");
    }

    @Test
    @DisplayName("Deve exibir dentista por CPF retornando HTTP 200")
    void deveExibirDentistaPorCpfRetornandoStatus200() {
        // Arrange
        String cpf = "12345678901";
        when(service.exibirDentista(cpf)).thenReturn(criarDTO(1L, cpf));

        // Act
        Response response = resource.exibirDentista(cpf);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Deve listar dentistas por cidade retornando HTTP 200")
    void deveListarPorCidadeRetornandoStatus200() {
        // Arrange
        String cidade = "São Paulo";
        when(service.listarPorCidades(cidade)).thenReturn(List.of(criarDTO(1L, "12345678901")));

        // Act
        Response response = resource.listarTodos(cidade);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException quando não houver dentistas na cidade")
    void deveLancarNaoEncontradoExceptionQuandoNaoHouverDentistasNaCidade() {
        // Arrange
        String cidade = "CidadeSemDentistas";
        when(service.listarPorCidades(cidade)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThatThrownBy(() -> resource.listarTodos(cidade))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Lista de dentistas não foi encontrada.");
    }

    @Test
    @DisplayName("Deve exportar dentistas em arquivo CSV")
    void deveExportarCsvDentistas() {
        // Arrange
        when(service.listarParaExportacao()).thenReturn(List.of(criarDTO(1L, "12345678901")));

        // Act
        Response response = resource.exportarCsv();

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeaderString("Content-Disposition")).contains("attachment; filename=");
    }

    @Test
    @DisplayName("Deve atualizar dentista e retornar HTTP 200")
    void deveAtualizarDentistaRetornandoStatus200() {
        // Arrange
        String cpf = "12345678901";
        AtualizarDentistaDTO dto = new AtualizarDentistaDTO("11988887777", "email@email.com", "VOLUNTARIO", 1L, "true", null);
        when(service.atualizar(cpf, dto)).thenReturn(criarDTO(1L, cpf));

        // Act
        Response response = resource.atualizar(cpf, dto);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Deve excluir dentista retornando HTTP 204 No Content")
    void deveExcluirDentistaRetornandoStatus204() {
        // Arrange
        String cpf = "12345678901";
        when(service.excluir(cpf)).thenReturn(1L);

        // Act
        Response response = resource.excluir(cpf);

        // Assert
        assertThat(response.getStatus()).isEqualTo(204);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao tentar excluir dentista inexistente")
    void deveLancarNaoEncontradoExceptionAoFalharExclusaoDentista() {
        // Arrange
        String cpf = "00000000000";
        when(service.excluir(cpf)).thenReturn(0L);

        // Act & Assert
        assertThatThrownBy(() -> resource.excluir(cpf))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Dentista não encontrado para exclusão.");
    }
}
