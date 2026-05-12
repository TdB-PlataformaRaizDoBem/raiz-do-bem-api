package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.response.BeneficiarioDTO;
import br.com.raizdobem.api.dto.response.DentistaDTO;
import br.com.raizdobem.api.client.GoogleMapsClient;
import br.com.raizdobem.api.dto.external.GoogleMapsRequestDTO;
import br.com.raizdobem.api.dto.external.GoogleMapsResponseDTO;
import br.com.raizdobem.api.entity.Dentista;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

import static br.com.raizdobem.api.service.AtendimentoMatchService.montarEndereco;

@ApplicationScoped
public class GoogleMapsService {
    @ConfigProperty(name = "google-api-key")
    String chaveApi;

    @Inject
    @RestClient
    GoogleMapsClient googleMapsClient;
    @Inject
    DentistaService dentistaService;
    @Inject
    AtendimentoMatchService atendimentoMatchService;

    public DentistaDTO buscarDentistaProximidade(BeneficiarioDTO beneficiario) {
        List<DentistaDTO> dentistas = dentistaService.listarDisponiveis();

        String origem = montarEndereco(beneficiario.getEndereco());

//        List<String> destinos = dentistas.stream().
//                map(dentista -> montarEndereco(dentista.))
//                .toList();

//        GoogleMapsRequestDTO request = preparaRequestApi(enderecoBeneficiario, destinosFormatados);
//
//        //FieldMask do Header
//        String recursosApi = "originIndex,destinationIndex,distanceMeters,condition";
//
//        return googleMapsClient.buscarRotas(chaveApi, recursosApi, request);
    //Mexer depois
        return null;
    }
}