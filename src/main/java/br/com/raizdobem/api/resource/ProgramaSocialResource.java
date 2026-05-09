package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.entity.ProgramaSocial;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.service.ProgramaService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@RequestScoped
@Path("/programas-sociais")
@Tag(name = "Programas", description = "Disponibiliza os programas oferecidos pela Ong.")
@Produces(MediaType.APPLICATION_JSON)
public class ProgramaSocialResource {
    @Inject
    ProgramaService service;

    @GET
    @Operation(summary = "Lista programas sociais")
    public List<ProgramaSocial> listarTodos(){
        return service.listarProgramasSociais();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca programa social por id")
    public ProgramaSocial buscarPorId(@PathParam("id") Long id) {
        ProgramaSocial programa = service.buscarPorId(id);
        if (programa == null) {
            throw new NaoEncontradoException("Programa social não encontrado.");
        }
        return programa;
    }
}
