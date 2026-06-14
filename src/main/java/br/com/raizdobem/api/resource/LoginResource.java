package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.request.LoginDTO;
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
    @Path("/login")
    @PermitAll
    @Operation(summary = "Login de colaborador", description = "Realiza o login do usuário e retorna um token JWT para autenticação.")
    public Response login(LoginDTO loginDTO) {
        return Response.ok(Map.of(
                "token", service.login(loginDTO), "tipo", "BearerToken"
        )).build();
    }
}
