package br.com.raizdobem.api.client;

import br.com.raizdobem.api.dto.external.GoogleMapsRequestDTO;
import br.com.raizdobem.api.dto.external.GoogleMapsResponseDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "google-maps-api")
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GoogleMapsClient {

    @POST
    @Path("/distanceMatrix/v2:computeRouteMatrix")
    List<GoogleMapsResponseDTO> buscarRotas(@HeaderParam("X-Goog-Api-Key") String apiKey,
                                            @HeaderParam("X-Goog-FieldMask") String camposNecessarios,
                                            GoogleMapsRequestDTO request);

}
