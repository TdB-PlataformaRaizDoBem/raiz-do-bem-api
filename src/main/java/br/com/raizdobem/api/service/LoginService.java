package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.LoginRequest;
import br.com.raizdobem.api.dto.request.RefreshTokenRequest;
import br.com.raizdobem.api.entity.Colaborador;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.repository.ColaboradorRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class LoginService {
    @Inject
    ColaboradorRepository colaboradorRepository;

    @Inject
    JWTParser jwtParser;

    public Map<String, String> login(LoginRequest login){
        Colaborador colaborador = colaboradorRepository.buscarPorEmail(login.email());
        if(colaborador == null || !BcryptUtil.matches(login.senha(), colaborador.getSenha())){
            throw new ValidacaoException("Email ou senha inválido(s).");
        }

        return gerarTokens(colaborador);
    }

    public Map<String, String> refreshToken(RefreshTokenRequest request) {
        if (request == null || request.refreshToken() == null || request.refreshToken().isBlank()) {
            throw new ValidacaoException("Refresh token é obrigatório.");
        }

        JsonWebToken jwt;
        try {
            jwt = jwtParser.parse(request.refreshToken());
        } catch (ParseException e) {
            throw new ValidacaoException("Refresh token inválido ou expirado.");
        }

        if (!"refresh".equals(jwt.getClaim("type"))) {
            throw new ValidacaoException("Token informado não é um refresh token válido.");
        }

        String email = jwt.getSubject();
        Colaborador colaborador = colaboradorRepository.buscarPorEmail(email);
        if (colaborador == null) {
            throw new ValidacaoException("Colaborador não encontrado.");
        }

        return gerarTokens(colaborador);
    }

    private Map<String, String> gerarTokens(Colaborador colaborador) {
        String tokenAcesso = Jwt.issuer("raiz-do-bem")
                .subject(colaborador.getEmail())
                .groups(Set.of(colaborador.getRole()))
                .expiresIn(Duration.ofHours(8))
                .sign();

        String refreshToken = Jwt.issuer("raiz-do-bem")
                .subject(colaborador.getEmail())
                .claim("type", "refresh")
                .expiresIn(Duration.ofDays(7))
                .sign();

        return Map.of("token", tokenAcesso, "refreshToken", refreshToken, "tipo", "BearerToken");
    }
}
