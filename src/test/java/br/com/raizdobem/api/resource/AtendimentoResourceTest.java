package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.request.AtendimentoUpdateRequest;
import br.com.raizdobem.api.dto.request.AtendimentoCreateRequest;
import br.com.raizdobem.api.dto.response.AtendimentoResponse;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.service.AtendimentoService;
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
@DisplayName("Testes Unitários - AtendimentoResource")
class AtendimentoResourceTest {

    @Mock
    private AtendimentoService service;

    @InjectMocks
    private AtendimentoResource resource;

    private AtendimentoResponse criarAtendimentoDTO(Long id) {
        return new AtendimentoResponse(
                id,
                "PRONT-001",
                "Joãozinho Silva",
                "Dra. Paula Fernandes",
                "11988887777",
                "paula@odonto.com",
                "Rua Augusta, 100",
                LocalDate.of(2026, 3, 1),
                "NAO FINALIZADO"
        );
    }

    @Test
    @DisplayName("Deve listar todos os atendimentos retornando HTTP 200")
    void deveListarTodosRetornandoStatus200() {
        // Arrange
        when(service.listarAtendimentos()).thenReturn(List.of(criarAtendimentoDTO(1L)));

        // Act
        Response response = resource.listarTodos();

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isNotNull();
    }

    @Test
    @DisplayName("Deve listar atendimentos com paginação retornando HTTP 200")
    void deveListarAtendimentosComPaginacaoRetornandoStatus200() {
        // Arrange
        int pagina = 0;
        int tamanho = 10;
        when(service.listarComPaginacao(pagina, tamanho)).thenReturn(List.of(criarAtendimentoDTO(1L)));

        // Act
        Response response = resource.listarTodos(pagina, tamanho);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isNotNull();
        verify(service, times(1)).listarComPaginacao(pagina, tamanho);
    }

    @Test
    @DisplayName("Deve criar atendimento com sucesso retornando HTTP 201 Created com Location")
    void deveCriarAtendimentoRetornandoStatus201() {
        // Arrange
        AtendimentoCreateRequest request = new AtendimentoCreateRequest("PRONT-001", "12345678901", LocalDate.now());
        AtendimentoResponse responseDTO = criarAtendimentoDTO(5L);
        when(service.criarAtendimento(request)).thenReturn(responseDTO);

        // Act
        Response response = resource.criar(request);

        // Assert
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getLocation()).isNotNull();
        assertThat(response.getLocation().toString()).isEqualTo("api/atendimento/" + responseDTO.id());
    }

    @Test
    @DisplayName("Deve lançar RequisicaoInvalidaException quando serviço retornar nulo ao criar atendimento")
    void deveLancarRequisicaoInvalidaExceptionQuandoCriacaoRetornarNulo() {
        // Arrange
        AtendimentoCreateRequest request = new AtendimentoCreateRequest("PRONT-001", "12345678901", LocalDate.now());
        when(service.criarAtendimento(request)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> resource.criar(request))
                .isInstanceOf(RequisicaoInvalidaException.class)
                .hasMessage("Não foi possível criar atendimento.");
    }

    @Test
    @DisplayName("Deve buscar atendimento por CPF e retornar HTTP 200")
    void deveBuscarPorCpfRetornandoStatus200() {
        // Arrange
        String cpf = "12345678901";
        AtendimentoResponse dto = criarAtendimentoDTO(1L);
        when(service.buscarPorCpf(cpf)).thenReturn(dto);

        // Act
        Response response = resource.buscarPorCpf(cpf);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isEqualTo(dto);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException quando atendimento por CPF não for encontrado")
    void deveLancarNaoEncontradoExceptionQuandoAtendimentoNaoExistirPorCpf() {
        // Arrange
        String cpf = "00000000000";
        when(service.buscarPorCpf(cpf)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> resource.buscarPorCpf(cpf))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Não foi possível encontrar atendimento com o CPF inserido.");
    }

    @Test
    @DisplayName("Deve exportar lista de atendimentos em CSV com cabeçalhos apropriados")
    void deveExportarCsvAtendimentos() {
        // Arrange
        when(service.listarAtendimentos()).thenReturn(List.of(criarAtendimentoDTO(1L)));

        // Act
        Response response = resource.exportarCsv();

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeaderString("Content-Disposition")).contains("attachment; filename=");
    }

    @Test
    @DisplayName("Deve finalizar atendimento por CPF e retornar HTTP 200")
    void deveFinalizarAtendimentoRetornandoStatus200() {
        // Arrange
        String cpf = "12345678901";
        AtendimentoUpdateRequest dto = new AtendimentoUpdateRequest("PRONT-ENCERRADO", 2L);

        // Act
        Response response = resource.atualizar(cpf, dto);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        verify(service, times(1)).encerrarAtendimento(cpf, dto);
    }

    @Test
    @DisplayName("Deve excluir atendimento com sucesso retornando HTTP 204 No Content")
    void deveExcluirAtendimentoRetornandoStatus204() {
        // Arrange
        long id = 1L;
        when(service.excluir(id)).thenReturn(true);

        // Act
        Response response = resource.excluirAtendimento(id);

        // Assert
        assertThat(response.getStatus()).isEqualTo(204);
        verify(service, times(1)).excluir(id);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao falhar na exclusão de atendimento")
    void deveLancarNaoEncontradoExceptionAoFalharExclusao() {
        // Arrange
        long id = 99L;
        when(service.excluir(id)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> resource.excluirAtendimento(id))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Pedido de ajuda não encontrado para exclusão.");
    }
}
