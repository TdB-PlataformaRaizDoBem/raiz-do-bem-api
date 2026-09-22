package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.LoginRequest;
import br.com.raizdobem.api.entity.Colaborador;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.repository.ColaboradorRepository;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.elytron.security.common.BcryptUtil;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class LoginService {
    @Inject
    ColaboradorRepository colaboradorRepository;

    public Map<String, String> login(LoginRequest login){
        Colaborador colaborador = colaboradorRepository.buscarPorEmail(login.email());
        if(colaborador == null || !BcryptUtil.matches(login.senha(), colaborador.getSenha())){
            throw new ValidacaoException("Email ou senha inválido(s).");
        }

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

        return Map.of("token", tokenAcesso, "refreshToken", refreshToken);
    }
}
