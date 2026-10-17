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
import jakarta.ws.rs.WebApplicationException;
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
        if(colaborador == null || !senhaValida(login.senha(), colaborador.getSenha())){
            throw new ValidacaoException("Email ou senha inválido(s).");
        }

        return gerarTokens(colaborador);
    }

    public Map<String, String> refreshToken(RefreshTokenRequest request) {
        if (request == null || request.refreshToken() == null) {
            throw new ValidacaoException("Refresh token é obrigatório.");
        }

        JsonWebToken jwt;
        try {
            jwt = jwtParser.parse(request.refreshToken());
        } catch (ParseException e) {
            throw refreshInvalido();
        }

        if (!"refresh".equals(jwt.getClaim("type"))) {
            throw refreshInvalido();
        }

        Colaborador colaborador = colaboradorRepository.buscarPorEmail(jwt.getSubject());
        if (colaborador == null) {
            throw refreshInvalido();
        }

        return gerarTokens(colaborador);
    }

    private Map<String, String> gerarTokens(Colaborador colaborador) {
        String tokenAcesso = Jwt.issuer("raiz-do-bem")
                .subject(colaborador.getEmail())
                .claim("nome", colaborador.getNomeCompleto())
                .groups(Set.of(colaborador.getRole()))
                .expiresIn(Duration.ofMinutes(30))
                .sign();

        String refreshToken = Jwt.issuer("raiz-do-bem")
                .subject(colaborador.getEmail())
                .claim("type", "refresh")
                .expiresIn(Duration.ofDays(7))
                .sign();

        return Map.of("token", tokenAcesso,
                      "refreshToken", refreshToken,
                      "tipo", "BearerToken");
    }

    private WebApplicationException refreshInvalido(){
        return new WebApplicationException("Refresh token inválido ou expirado.", 401);
    }

    private boolean senhaValida(String senha, String hash){
        try {
            return hash != null && BcryptUtil.matches(senha, hash);
        } catch (RuntimeException e) {
            // hash armazenado fora do formato bcrypt (ex.: senha em texto puro no banco)
            return false;
        }
    }
}
