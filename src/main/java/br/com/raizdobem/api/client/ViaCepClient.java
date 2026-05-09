package br.com.raizdobem.api.client;

import br.com.raizdobem.api.dto.external.ViaCepDTO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "via-cep-api")
@Path("/")
public interface ViaCepClient {

    @GET
    @Path("/{cep}/json/")
    ViaCepDTO buscarEndereco(@PathParam("cep") String cep);

}
