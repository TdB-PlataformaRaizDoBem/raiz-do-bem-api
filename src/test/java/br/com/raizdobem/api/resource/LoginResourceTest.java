package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.request.LoginDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.service.LoginService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - LoginResource")
class LoginResourceTest {

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginResource loginResource;

    @Test
    @DisplayName("Deve autenticar com sucesso e retornar token JWT com HTTP 200")
    @SuppressWarnings("unchecked")
    void deveAutenticarComSucesso() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("usuario@raizdobem.org", "Senha@123");
        String tokenEsperado = "jwt.token.gerado";

        when(loginService.login(loginDTO)).thenReturn(tokenEsperado);

        // Act
        Response response = loginResource.login(loginDTO);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        Map<String, String> entity = (Map<String, String>) response.getEntity();
        assertThat(entity).containsEntry("token", tokenEsperado);
        assertThat(entity).containsEntry("tipo", "BearerToken");
    }

    @Test
    @DisplayName("Deve propagar NaoEncontradoException quando email não for encontrado")
    void devePropagarNaoEncontradoExceptionQuandoEmailNaoExistir() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("inexistente@raizdobem.org", "Senha@123");
        when(loginService.login(loginDTO)).thenThrow(new NaoEncontradoException("Email inválido."));

        // Act & Assert
        assertThatThrownBy(() -> loginResource.login(loginDTO))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Email inválido.");
    }

    @Test
    @DisplayName("Deve propagar RequisicaoInvalidaException quando senha for incorreta")
    void devePropagarRequisicaoInvalidaExceptionQuandoSenhaIncorreta() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("usuario@raizdobem.org", "SenhaErrada");
        when(loginService.login(loginDTO)).thenThrow(new RequisicaoInvalidaException("Senha inválida."));

        // Act & Assert
        assertThatThrownBy(() -> loginResource.login(loginDTO))
                .isInstanceOf(RequisicaoInvalidaException.class)
                .hasMessage("Senha inválida.");
    }
}
