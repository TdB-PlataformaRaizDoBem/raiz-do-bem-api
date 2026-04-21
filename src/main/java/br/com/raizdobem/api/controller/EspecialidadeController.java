package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.model.Especialidade;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@RequestScoped
public class EspecialidadeController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Especialidade> listarTodas(){
        return null;
    }
}
