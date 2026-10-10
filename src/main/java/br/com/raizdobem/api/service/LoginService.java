package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.LoginRequest;
import br.com.raizdobem.api.entity.Colaborador;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.repository.ColaboradorRepository;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import io.quarkus.elytron.security.common.BcryptUtil;

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
        if(login == null || login.email() == null || login.senha() == null){
            throw new ValidacaoException("Email ou senha inválido(s).");
        }
        Colaborador colaborador = colaboradorRepository.buscarPorEmail(login.email());
        if(colaborador == null || !senhaConfere(login.senha(), colaborador.getSenha())){
            throw new ValidacaoException("Email ou senha inválido(s).");
        }

        return emitirTokens(colaborador);
    }

    public Map<String, String> renovarToken(String refreshToken){
        if(refreshToken == null || refreshToken.isBlank()){
            throw refreshInvalido();
        }

        JsonWebToken jwt;
        try {
            jwt = jwtParser.parse(refreshToken);
        } catch (ParseException e) {
            throw refreshInvalido();
        }

        if(!"refresh".equals(jwt.getClaim("type"))){
            throw refreshInvalido();
        }

        Colaborador colaborador = colaboradorRepository.buscarPorEmail(jwt.getSubject());
        if(colaborador == null){
            throw refreshInvalido();
        }

        return emitirTokens(colaborador);
    }

    private Map<String, String> emitirTokens(Colaborador colaborador){
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

        return Map.of("token", tokenAcesso, "refreshToken", refreshToken);
    }

    private WebApplicationException refreshInvalido(){
        return new WebApplicationException("Refresh token inválido ou expirado.", 401);
    }

    private boolean senhaConfere(String senha, String hash){
        try {
            return hash != null && BcryptUtil.matches(senha, hash);
        } catch (RuntimeException e) {
            // hash armazenado fora do formato bcrypt (ex.: senha em texto puro no banco)
            return false;
        }
    }
}
