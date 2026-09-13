package br.com.raizdobem.api.exception;

import br.com.raizdobem.api.dto.response.ErroDTO;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários - ExceptionsMapperGlobal")
class ExceptionsMapperGlobalTest {

    private final ExceptionsMapperGlobal mapper = new ExceptionsMapperGlobal();

    @Test
    @DisplayName("Deve mapear NaoEncontradoException para HTTP 404")
    void deveMapearNaoEncontradoExceptionParaStatus404() {
        // Arrange
        NaoEncontradoException ex = new NaoEncontradoException("Recurso não encontrado");

        // Act
        Response response = mapper.toResponse(ex);

        // Assert
        assertThat(response.getStatus()).isEqualTo(404);
        ErroDTO erro = (ErroDTO) response.getEntity();
        assertThat(erro.statusCode()).isEqualTo(404);
        assertThat(erro.mensagem()).isEqualTo("Recurso não encontrado");
        assertThat(erro.timestamp()).isNotNull();
    }

    @Test
    @DisplayName("Deve mapear ValidacaoException para HTTP 422")
    void deveMapearValidacaoExceptionParaStatus422() {
        // Arrange
        ValidacaoException ex = new ValidacaoException("Campo inválido");

        // Act
        Response response = mapper.toResponse(ex);

        // Assert
        assertThat(response.getStatus()).isEqualTo(422);
        ErroDTO erro = (ErroDTO) response.getEntity();
        assertThat(erro.statusCode()).isEqualTo(422);
        assertThat(erro.mensagem()).isEqualTo("Campo inválido");
    }

    @Test
    @DisplayName("Deve mapear RegraNegocioException para HTTP 409")
    void deveMapearRegraNegocioExceptionParaStatus409() {
        // Arrange
        RegraNegocioException ex = new RegraNegocioException("Regra violada");

        // Act
        Response response = mapper.toResponse(ex);

        // Assert
        assertThat(response.getStatus()).isEqualTo(409);
        ErroDTO erro = (ErroDTO) response.getEntity();
        assertThat(erro.statusCode()).isEqualTo(409);
        assertThat(erro.mensagem()).isEqualTo("Regra violada");
    }

    @Test
    @DisplayName("Deve mapear RequisicaoInvalidaException para HTTP 400")
    void deveMapearRequisicaoInvalidaExceptionParaStatus400() {
        // Arrange
        RequisicaoInvalidaException ex = new RequisicaoInvalidaException("Requisição malformada");

        // Act
        Response response = mapper.toResponse(ex);

        // Assert
        assertThat(response.getStatus()).isEqualTo(400);
        ErroDTO erro = (ErroDTO) response.getEntity();
        assertThat(erro.statusCode()).isEqualTo(400);
        assertThat(erro.mensagem()).isEqualTo("Requisição malformada");
    }

    @Test
    @DisplayName("Deve mapear exceções não tratadas para HTTP 500 com mensagem genérica")
    void deveMapearExcecoesGenericasParaStatus500() {
        // Arrange
        NullPointerException ex = new NullPointerException("Ponteiro nulo imprevisto");

        // Act
        Response response = mapper.toResponse(ex);

        // Assert
        assertThat(response.getStatus()).isEqualTo(500);
        ErroDTO erro = (ErroDTO) response.getEntity();
        assertThat(erro.statusCode()).isEqualTo(500);
        assertThat(erro.mensagem()).isEqualTo("Erro interno do servidor.");
    }
}
