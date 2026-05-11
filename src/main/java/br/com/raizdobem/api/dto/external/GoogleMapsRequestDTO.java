package br.com.raizdobem.api.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GoogleMapsRequestDTO {
    @JsonProperty("origins")
    private List<GoogleMapsEnderecoDTO> origins;

    @JsonProperty("destinations")
    private List<GoogleMapsEnderecoDTO> destinations;

    @JsonProperty("travelMode")
    private String tipoViagem = "DRIVE";

    @JsonProperty("routingPreference")
    private String rotaPreferida = "TRAFFIC_UNAWARE";
}
