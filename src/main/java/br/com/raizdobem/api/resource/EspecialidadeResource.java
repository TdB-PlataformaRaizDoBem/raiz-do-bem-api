package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.entity.Especialidade;
import br.com.raizdobem.api.service.EspecialidadeService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@RequestScoped
@Path("/especialidades")
@Tag(name = "Especialidades", description = "Disponibiliza visualização das especialidades gerais dos dentistas.")
@Produces(MediaType.APPLICATION_JSON)
public class EspecialidadeResource {
    @Inject
    EspecialidadeService service;

    @GET
    @Operation(summary = "Lista todas as especialidades")
    public List<Especialidade> listarTodas(){
        return service.listarEspecialidades();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca especialidade por id")
    public Especialidade buscarPorId(@PathParam("id") Long id) {
        return service.buscarPorId(id);
    }

}
