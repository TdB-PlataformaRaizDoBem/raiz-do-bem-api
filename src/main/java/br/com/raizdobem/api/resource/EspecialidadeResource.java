package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.entity.Especialidade;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.service.EspecialidadeService;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@RequestScoped
@Path("/especialidades")
@Tag(name = "Especialidades", description = "Disponibiliza visualização das especialidades gerais dos dentistas.")
@Produces(MediaType.APPLICATION_JSON)
public class EspecialidadeResource {
    @Inject
    EspecialidadeService service;

    @GET
    @Operation(summary = "Lista todas as especialidades que podem ser atribuídas a um dentista.")
    @PermitAll
    public Response listarTodas(){
        return Response.ok(service.listarEspecialidades()).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca especialidade específica pelo id")
    @PermitAll
    public Response buscarPorId(@PathParam("id") Long id) {
        Especialidade especialidade = service.buscarPorId(id);
        if(especialidade == null)
            throw new NaoEncontradoException("Especialidade com id " + id  + " não encontrada!");
        return Response.ok().entity(especialidade).build();
    }
}
