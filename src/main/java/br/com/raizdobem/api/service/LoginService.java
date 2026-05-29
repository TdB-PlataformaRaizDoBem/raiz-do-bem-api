package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.LoginDTO;
import br.com.raizdobem.api.entity.Colaborador;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.repository.ColaboradorRepository;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LoginService {
    @Inject
    ColaboradorRepository colaboradorRepository;

    public String login(LoginDTO login){
        Colaborador colaborador = colaboradorRepository.buscarPorEmail(login.email());
        if(colaborador == null || !login.senha().equals(colaborador.getSenha())){
            throw new NaoEncontradoException("Email/senha inválido(s).");
        }

        return Jwt.issuer("raiz-do-bem")
                .subject(colaborador.getEmail())
                .claim("nome", colaborador.getNomeCompleto())
                .expiresIn(10000L)
                .sign();
    }
}
