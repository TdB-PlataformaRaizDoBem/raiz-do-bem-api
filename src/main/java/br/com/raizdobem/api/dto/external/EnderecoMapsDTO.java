package br.com.raizdobem.api.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EnderecoMapsDTO {
    @JsonProperty("waypoint")
    private PontoParadaDTO pontoParada;
}
