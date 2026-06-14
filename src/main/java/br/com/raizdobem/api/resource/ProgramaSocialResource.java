package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.entity.ProgramaSocial;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.service.ProgramaService;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
    @Operation(summary = "Lista os programas sociais presentes na ONG: Dentista do Bem e Apolônias do Bem")
    @PermitAll
    public Response listarTodos(){
        List<ProgramaSocial> programas = service.listarProgramasSociais();
        if(programas == null || programas.isEmpty())
            throw new NaoEncontradoException("Lista de programas sociais não encontrada/vazia");
        return Response.ok().entity(programas).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca programa social com o id")
    @PermitAll
    public Response buscarPorId(@PathParam("id") Long id) {
        ProgramaSocial programa = service.buscarPorId(id);
        if (programa == null) {
            throw new NaoEncontradoException("Programa social não encontrado.");
        }
        return Response.ok().entity(programa).build();
    }
}
