package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.request.LoginRequest;
import br.com.raizdobem.api.exception.ValidacaoException;
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
    @DisplayName("Deve autenticar com sucesso e retornar tokens com HTTP 200")
    @SuppressWarnings("unchecked")
    void deveAutenticarComSucesso() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("usuario@raizdobem.org", "Senha@123");
        Map<String, String> tokensEsperados = Map.of(
                "token", "jwt.access.token",
                "refreshToken", "jwt.refresh.token"
        );

        when(loginService.login(loginRequest)).thenReturn(tokensEsperados);

        // Act
        Response response = loginResource.login(loginRequest);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertThat(entity).containsEntry("token", tokensEsperados);
        assertThat(entity).containsEntry("tipo", "BearerToken");
    }

    @Test
    @DisplayName("Deve propagar ValidacaoException quando credenciais forem inválidas")
    void devePropagarValidacaoExceptionQuandoCredenciaisForemInvalidas() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("inexistente@raizdobem.org", "Senha@123");
        when(loginService.login(loginRequest)).thenThrow(new ValidacaoException("Email ou senha inválido(s)."));

        // Act & Assert
        assertThatThrownBy(() -> loginResource.login(loginRequest))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Email ou senha inválido(s).");
    }
}
