package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.LoginRequest;
import br.com.raizdobem.api.entity.Colaborador;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.repository.ColaboradorRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - LoginService")
class LoginServiceTest {

    @Mock
    private ColaboradorRepository colaboradorRepository;

    @Mock
    private JWTParser jwtParser;

    @InjectMocks
    private LoginService loginService;

    @Test
    @DisplayName("Deve gerar access token e refresh token com sucesso quando email e senha forem válidos")
    void deveGerarTokenJwtQuandoCredenciaisForemValidas() {
        // Arrange
        String email = "admin@raizdobem.org";
        String senha = "senhaCorreta123";
        LoginRequest dto = new LoginRequest(email, senha);

        Colaborador colaborador = new Colaborador();
        colaborador.setId(1L);
        colaborador.setEmail(email);
        colaborador.setNomeCompleto("Admin Teste");
        colaborador.setSenha(BcryptUtil.bcryptHash(senha));
        colaborador.setRole("ADMIN");

        when(colaboradorRepository.buscarPorEmail(email)).thenReturn(colaborador);

        // Act
        Map<String, String> tokens = loginService.login(dto);

        // Assert
        assertThat(tokens).isNotNull();
        assertThat(tokens).containsKey("token");
        assertThat(tokens.get("token")).isNotBlank();
        assertThat(tokens).containsKey("refreshToken");
        assertThat(tokens.get("refreshToken")).isNotBlank();
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando email do colaborador não for encontrado")
    void deveLancarValidacaoExceptionQuandoEmailNaoExistir() {
        // Arrange
        String emailInexistente = "inexistente@raizdobem.org";
        LoginRequest dto = new LoginRequest(emailInexistente, "qualquerSenha");

        when(colaboradorRepository.buscarPorEmail(emailInexistente)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> loginService.login(dto))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Email ou senha inválido(s).");
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando senha informada for incorreta")
    void deveLancarValidacaoExceptionQuandoSenhaForIncorreta() {
        // Arrange
        String email = "admin@raizdobem.org";
        LoginRequest dto = new LoginRequest(email, "senhaErrada");

        Colaborador colaborador = new Colaborador();
        colaborador.setEmail(email);
        colaborador.setSenha(BcryptUtil.bcryptHash("senhaCerta"));

        when(colaboradorRepository.buscarPorEmail(email)).thenReturn(colaborador);

        // Act & Assert
        assertThatThrownBy(() -> loginService.login(dto))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Email ou senha inválido(s).");
    }

    @Test
    @DisplayName("Deve renovar tokens quando o refresh token for válido")
    void deveRenovarTokensQuandoRefreshForValido() throws ParseException {
        Colaborador colaborador = new Colaborador();
        colaborador.setEmail("admin@raizdobem.org");
        colaborador.setNomeCompleto("Admin Teste");
        colaborador.setRole("ADMIN");

        JsonWebToken jwt = mock(JsonWebToken.class);
        when(jwt.getClaim("type")).thenReturn("refresh");
        when(jwt.getSubject()).thenReturn(colaborador.getEmail());
        when(jwtParser.parse("refresh.token")).thenReturn(jwt);
        when(colaboradorRepository.buscarPorEmail(colaborador.getEmail())).thenReturn(colaborador);

        Map<String, String> tokens = loginService.renovarToken("refresh.token");

        assertThat(tokens.get("token")).isNotBlank();
        assertThat(tokens.get("refreshToken")).isNotBlank();
    }

    @Test
    @DisplayName("Deve rejeitar com 401 quando o refresh token for inválido ou expirado")
    void deveRejeitarRefreshInvalido() throws ParseException {
        when(jwtParser.parse("ruim")).thenThrow(new ParseException("inválido"));

        assertThatThrownBy(() -> loginService.renovarToken("ruim"))
                .isInstanceOfSatisfying(WebApplicationException.class,
                        e -> assertThat(e.getResponse().getStatus()).isEqualTo(401));
    }

    @Test
    @DisplayName("Deve rejeitar com 401 quando o token informado não for um refresh token")
    void deveRejeitarTokenQueNaoEhRefresh() throws ParseException {
        JsonWebToken jwt = mock(JsonWebToken.class);
        when(jwt.getClaim("type")).thenReturn(null);
        when(jwtParser.parse("access.token")).thenReturn(jwt);

        assertThatThrownBy(() -> loginService.renovarToken("access.token"))
                .isInstanceOfSatisfying(WebApplicationException.class,
                        e -> assertThat(e.getResponse().getStatus()).isEqualTo(401));
    }

    @Test
    @DisplayName("Deve rejeitar com 401 quando o refresh token estiver em branco")
    void deveRejeitarRefreshEmBranco() {
        assertThatThrownBy(() -> loginService.renovarToken(" "))
                .isInstanceOf(WebApplicationException.class);
    }
}
