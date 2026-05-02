package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.model.dto.EspecialidadeDTO;
import br.com.raizdobem.api.service.EspecialidadeService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@RequestScoped
@Path("/especialidades")
@Tag(name = "Especialidades", description = "Disponibiliza visualizção das especialidades gerais dos dentistas.")
public class EspecialidadeController {
    @Inject
    EspecialidadeService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<EspecialidadeDTO> listarTodas(){
        return service.listarEspecialidades();
    }
}
