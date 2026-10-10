package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.request.LoginRequest;
import br.com.raizdobem.api.dto.request.RefreshTokenRequest;
import br.com.raizdobem.api.service.LoginService;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;

@RequestScoped
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Autenticação", description = "Disponibiliza funções para autenticação JWT.")
public class LoginResource {

    @Inject
    LoginService service;

    @POST
    @Path("/tokenAcesso")
    @PermitAll
    @Operation(summary = "Login de colaborador", description = "Realiza o login do usuário e retorna um token JWT para autenticação.")
    public Response login(LoginRequest loginRequest) {
        Map<String, String> tokens = service.login(loginRequest);
        return Response.ok(Map.of(
                "token", tokens.get("token"),
                "refreshToken", tokens.get("refreshToken"),
                "tipo", "BearerToken"
        )).build();
    }

    @POST
    @Path("/refreshToken")
    @PermitAll
    @Operation(summary = "Renovação de token", description = "Recebe um refresh token válido e retorna um novo token de acesso e um novo refresh token.")
    public Response renovar(RefreshTokenRequest request) {
        Map<String, String> tokens = service.renovarToken(request == null ? null : request.refreshToken());
        return Response.ok(Map.of(
                "token", tokens.get("token"),
                "refreshToken", tokens.get("refreshToken"),
                "tipo", "BearerToken"
        )).build();
    }
}
