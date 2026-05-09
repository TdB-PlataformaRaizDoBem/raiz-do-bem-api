package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.entity.ProgramaSocial;
import br.com.raizdobem.api.service.ProgramaService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@RequestScoped
@Path("/programas-sociais")
@Tag(name = "Programas", description = "Disponibiliza os programas oferecidos pela Ong.")
public class ProgramaSocialController {
    @Inject
    ProgramaService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProgramaSocial> listarTodos(){
        return service.listarProgramasSociais();
    }
}
