package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.request.BeneficiarioUpdateRequest;
import br.com.raizdobem.api.dto.request.BeneficiarioCreateRequest;
import br.com.raizdobem.api.dto.response.BeneficiarioResponse;
import br.com.raizdobem.api.dto.response.EnderecoResponse;
import br.com.raizdobem.api.dto.response.PedidoAjudaResumidoDTO;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.service.BeneficiarioService;
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
@DisplayName("Testes Unitários - BeneficiarioResource")
class BeneficiarioResourceTest {

    @Mock
    private BeneficiarioService service;

    @InjectMocks
    private BeneficiarioResource resource;

    private BeneficiarioResponse criarDTO(Long id, String cpf) {
        return new BeneficiarioResponse(
                id,
                cpf,
                "Maria Joana Silva",
                LocalDate.of(2012, 5, 10),
                "11988887777",
                "maria@email.com",
                new PedidoAjudaResumidoDTO(1L, "Dra. Dentista"),
                "Sorriso Criança",
                new EnderecoResponse(1L, "Praça da Sé", "01001000", "100", "Sé", "São Paulo", "SP", "RESIDENCIAL")
        );
    }

    @Test
    @DisplayName("Deve listar todos os beneficiários e retornar HTTP 200")
    void deveListarTodosRetornandoStatus200() {
        // Arrange
        when(service.listarTodos()).thenReturn(List.of(criarDTO(1L, "12345678901")));

        // Act
        Response response = resource.listarTodos();

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isNotNull();
    }

    @Test
    @DisplayName("Deve criar beneficiário com sucesso e retornar HTTP 201 Created")
    void deveCriarBeneficiarioRetornandoStatus201() {
        // Arrange
        BeneficiarioCreateRequest request = new BeneficiarioCreateRequest(10L, 1L);
        BeneficiarioResponse responseDTO = criarDTO(1L, "12345678901");
        when(service.criarBeneficiario(request)).thenReturn(responseDTO);

        // Act
        Response response = resource.criar(request);

        // Assert
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getEntity()).isEqualTo(responseDTO);
    }

    @Test
    @DisplayName("Deve buscar beneficiário por CPF e retornar HTTP 200")
    void deveBuscarPorCpfRetornandoStatus200() {
        // Arrange
        String cpf = "12345678901";
        BeneficiarioResponse dto = criarDTO(1L, cpf);
        when(service.buscarPorCpf(cpf)).thenReturn(dto);

        // Act
        Response response = resource.buscarPorCpf(cpf);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isEqualTo(dto);
    }

    @Test
    @DisplayName("Deve listar beneficiários por cidade e retornar HTTP 200")
    void deveListarPorCidadeRetornandoStatus200() {
        // Arrange
        String cidade = "São Paulo";
        when(service.listarPorCidade(cidade)).thenReturn(List.of(criarDTO(1L, "12345678901")));

        // Act
        Response response = resource.listarPorCidade(cidade);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Deve listar beneficiários por programa social e retornar HTTP 200")
    void deveListarPorProgramaRetornandoStatus200() {
        // Arrange
        long programaId = 1L;
        when(service.listarPorPrograma(programaId)).thenReturn(List.of(criarDTO(1L, "12345678901")));

        // Act
        Response response = resource.listarPorPrograma(programaId);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Deve exportar beneficiários em formato CSV com headers corretos")
    void deveExportarCsvComSucesso() {
        // Arrange
        when(service.listarTodos()).thenReturn(List.of(criarDTO(1L, "12345678901")));

        // Act
        Response response = resource.exportarCsv();

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeaderString("Content-Type")).isEqualTo("text/csv; charset=UTF-8");
        assertThat(response.getHeaderString("Content-Disposition")).contains("attachment; filename=");
    }

    @Test
    @DisplayName("Deve atualizar beneficiário por CPF e retornar HTTP 200")
    void deveAtualizarBeneficiarioRetornandoStatus200() {
        // Arrange
        String cpf = "12345678901";
        BeneficiarioUpdateRequest dto = new BeneficiarioUpdateRequest("11999998888", "novo@email.com", null);
        BeneficiarioResponse atualizado = criarDTO(1L, cpf);

        when(service.atualizar(cpf, dto)).thenReturn(atualizado);

        // Act
        Response response = resource.atualizar(cpf, dto);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isEqualTo(atualizado);
    }

    @Test
    @DisplayName("Deve excluir beneficiário com sucesso e retornar HTTP 204 No Content")
    void deveExcluirBeneficiarioRetornandoStatus204() {
        // Arrange
        String cpf = "12345678901";
        when(service.excluir(cpf)).thenReturn(true);

        // Act
        Response response = resource.excluir(cpf);

        // Assert
        assertThat(response.getStatus()).isEqualTo(204);
        verify(service, times(1)).excluir(cpf);
    }

    @Test
    @DisplayName("Deve lançar RequisicaoInvalidaException ao tentar excluir beneficiário sem sucesso")
    void deveLancarRequisicaoInvalidaExceptionAoFalharExclusao() {
        // Arrange
        String cpf = "12345678901";
        when(service.excluir(cpf)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> resource.excluir(cpf))
                .isInstanceOf(RequisicaoInvalidaException.class)
                .hasMessage("Não foi possível excluir beneficiário.");
    }
}
