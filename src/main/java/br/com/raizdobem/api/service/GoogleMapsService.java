//package br.com.raizdobem.api.service;
//
//import br.com.raizdobem.api.client.GoogleMapsClient;
//import br.com.raizdobem.api.dto.external.GoogleMapsRequestDTO;
//import br.com.raizdobem.api.dto.external.GoogleMapsResponseDTO;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.inject.Inject;
//import org.eclipse.microprofile.config.inject.ConfigProperty;
//import org.eclipse.microprofile.rest.client.inject.RestClient;
//
//
//import java.util.List;
//
//@ApplicationScoped
//public class GoogleMapsService {
//    @ConfigProperty(name = "google-api-key")
//    String chaveApi;
//
//    @Inject
//    @RestClient
//    GoogleMapsClient googleMapsClient;
//
//    @Inject
//    EnderecoService enderecoService;
//
//    public List<GoogleMapsResponseDTO> buscarRotas(String origem, List<String> destinos) {
//        //String enderecoBeneficiario;
//        //String enderecoseDentista;
//
//        List<String> destinosFormatados = destinos.stream().
//                map(destino -> viaCepService.preparaEndereco(destino.getCep(), destino.getNumero()))
//                .toList();
//
//        GoogleMapsRequestDTO request = preparaRequestApi(enderecoBeneficiario, destinosFormatados);
//
//        //FieldMask do Header
//        String recursosApi = "originIndex,destinationIndex,distanceMeters,condition";
//
//        return googleMapsClient.buscarRotas(chaveApi, recursosApi, request);
//    }
//}