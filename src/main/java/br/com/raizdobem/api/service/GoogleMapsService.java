package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.external.GoogleMapsEnderecoDTO;
import br.com.raizdobem.api.dto.external.GoogleMapsRequestDTO;
import br.com.raizdobem.api.dto.external.GoogleMapsResponseDTO;
import br.com.raizdobem.api.dto.external.PontoParadaDTO;
import br.com.raizdobem.api.dto.response.DentistaDTO;
import br.com.raizdobem.api.client.GoogleMapsClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class GoogleMapsService {
    @ConfigProperty(name = "google-api-key")
    String chaveApi;

    @Inject
    @RestClient
    GoogleMapsClient googleMapsClient;

    public DentistaDTO calcularDistanciaProximidade(String enderecoBeneficiario, List<DentistaDTO> dentistas) {

        GoogleMapsEnderecoDTO origem = new GoogleMapsEnderecoDTO(
                new PontoParadaDTO(enderecoBeneficiario));

        List<GoogleMapsEnderecoDTO> destinos = dentistas.stream().
                map(d -> new GoogleMapsEnderecoDTO(
                        new PontoParadaDTO(
                                d.logradouro() + ", " + d.numero() + ", " + d.cidade() + ", " + d.estado()
                        ))
                ).toList();

        if(destinos.isEmpty())
            return dentistas.getFirst();

        GoogleMapsRequestDTO request = new GoogleMapsRequestDTO();
        List<GoogleMapsEnderecoDTO> enderecoOrigem = new ArrayList<>();
        enderecoOrigem.add(origem);

        request.setOrigins(enderecoOrigem);
        request.setDestinations(destinos);

        String recursosApi = "originIndex,destinationIndex,distanceMeters,condition";

        try{
            List<GoogleMapsResponseDTO> rotas = googleMapsClient.buscarRotas(chaveApi, recursosApi, request);

            GoogleMapsResponseDTO menorRota = rotas.stream()
                    .filter(r -> "ROUTE_EXISTS".equals(r.statusRetorno()))
                            .min((Comparator.comparingInt(GoogleMapsResponseDTO::metrosDistancia)))
                            .orElse(rotas.getFirst());

            return dentistas.get(menorRota.elementoDestino());
        } catch(Exception e){
            return dentistas.getFirst();
        }
    }
}