package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.LoginRequest;
import br.com.raizdobem.api.dto.request.RefreshTokenRequest;
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
        assertThat(tokens).containsEntry("tipo", "BearerToken");
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
    @DisplayName("Deve lançar ValidacaoException quando hash da senha no banco estiver fora do formato bcrypt")
    void deveLancarValidacaoExceptionQuandoHashSenhaForInvalido() {
        // Arrange
        String email = "admin@raizdobem.org";
        LoginRequest dto = new LoginRequest(email, "senhaTextoPuro");

        Colaborador colaborador = new Colaborador();
        colaborador.setEmail(email);
        colaborador.setSenha("senhaTextoPuroSemHashBcrypt");

        when(colaboradorRepository.buscarPorEmail(email)).thenReturn(colaborador);

        // Act & Assert
        assertThatThrownBy(() -> loginService.login(dto))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Email ou senha inválido(s).");
    }

    @Test
    @DisplayName("Deve renovar tokens com sucesso quando refresh token for válido")
    void deveRenovarTokensComSucessoQuandoRefreshTokenForValido() throws ParseException {
        // Arrange
        String tokenStr = "valid.refresh.token";
        String email = "admin@raizdobem.org";
        RefreshTokenRequest request = new RefreshTokenRequest(tokenStr);

        JsonWebToken jwt = mock(JsonWebToken.class);
        when(jwtParser.parse(tokenStr)).thenReturn(jwt);
        when(jwt.getClaim("type")).thenReturn("refresh");
        when(jwt.getSubject()).thenReturn(email);

        Colaborador colaborador = new Colaborador();
        colaborador.setId(1L);
        colaborador.setEmail(email);
        colaborador.setNomeCompleto("Admin Teste");
        colaborador.setRole("ADMIN");
        when(colaboradorRepository.buscarPorEmail(email)).thenReturn(colaborador);

        // Act
        Map<String, String> resultado = loginService.refreshToken(request);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.get("token")).isNotBlank();
        assertThat(resultado.get("refreshToken")).isNotBlank();
        assertThat(resultado).containsEntry("tipo", "BearerToken");
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando request ou refreshToken for nulo")
    void deveLancarValidacaoExceptionQuandoRefreshTokenForNulo() {
        // Act & Assert
        assertThatThrownBy(() -> loginService.refreshToken(null))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Refresh token é obrigatório.");

        assertThatThrownBy(() -> loginService.refreshToken(new RefreshTokenRequest(null)))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Refresh token é obrigatório.");
    }

    @Test
    @DisplayName("Deve lançar WebApplicationException 401 quando refresh token falhar no parse ou estiver expirado")
    void deveLancarWebApplicationExceptionQuandoRefreshTokenForInvalido() throws ParseException {
        // Arrange
        String tokenInvalido = "invalid.token";
        RefreshTokenRequest request = new RefreshTokenRequest(tokenInvalido);
        when(jwtParser.parse(tokenInvalido)).thenThrow(new ParseException("Token inválido"));

        // Act & Assert
        assertThatThrownBy(() -> loginService.refreshToken(request))
                .isInstanceOf(WebApplicationException.class)
                .hasMessage("Refresh token inválido ou expirado.")
                .extracting(ex -> ((WebApplicationException) ex).getResponse().getStatus())
                .isEqualTo(401);
    }

    @Test
    @DisplayName("Deve lançar WebApplicationException 401 quando claim type não for refresh")
    void deveLancarWebApplicationExceptionQuandoClaimTypeNaoForRefresh() throws ParseException {
        // Arrange
        String accessToken = "access.token.nao.refresh";
        RefreshTokenRequest request = new RefreshTokenRequest(accessToken);

        JsonWebToken jwt = mock(JsonWebToken.class);
        when(jwtParser.parse(accessToken)).thenReturn(jwt);
        when(jwt.getClaim("type")).thenReturn("access");

        // Act & Assert
        assertThatThrownBy(() -> loginService.refreshToken(request))
                .isInstanceOf(WebApplicationException.class)
                .hasMessage("Refresh token inválido ou expirado.")
                .extracting(ex -> ((WebApplicationException) ex).getResponse().getStatus())
                .isEqualTo(401);
    }

    @Test
    @DisplayName("Deve lançar WebApplicationException 401 quando colaborador do refresh token não existir")
    void deveLancarWebApplicationExceptionQuandoColaboradorNaoExistirNoRefresh() throws ParseException {
        // Arrange
        String tokenStr = "valid.refresh.token";
        String email = "removido@raizdobem.org";
        RefreshTokenRequest request = new RefreshTokenRequest(tokenStr);

        JsonWebToken jwt = mock(JsonWebToken.class);
        when(jwtParser.parse(tokenStr)).thenReturn(jwt);
        when(jwt.getClaim("type")).thenReturn("refresh");
        when(jwt.getSubject()).thenReturn(email);
        when(colaboradorRepository.buscarPorEmail(email)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> loginService.refreshToken(request))
                .isInstanceOf(WebApplicationException.class)
                .hasMessage("Refresh token inválido ou expirado.")
                .extracting(ex -> ((WebApplicationException) ex).getResponse().getStatus())
                .isEqualTo(401);
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando senha armazenada no colaborador for nula")
    void deveLancarValidacaoExceptionQuandoHashSenhaForNulo() {
        // Arrange
        String email = "admin@raizdobem.org";
        LoginRequest dto = new LoginRequest(email, "qualquerSenha");

        Colaborador colaborador = new Colaborador();
        colaborador.setEmail(email);
        colaborador.setSenha(null);

        when(colaboradorRepository.buscarPorEmail(email)).thenReturn(colaborador);

        // Act & Assert
        assertThatThrownBy(() -> loginService.login(dto))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Email ou senha inválido(s).");
    }
}

