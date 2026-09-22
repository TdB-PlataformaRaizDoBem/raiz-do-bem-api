package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.LoginRequest;
import br.com.raizdobem.api.entity.Colaborador;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.repository.ColaboradorRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
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
@DisplayName("Testes Unitários - LoginService")
class LoginServiceTest {

    @Mock
    private ColaboradorRepository colaboradorRepository;

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
}
