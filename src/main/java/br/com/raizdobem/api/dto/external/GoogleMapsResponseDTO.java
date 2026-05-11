package br.com.raizdobem.api.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleMapsResponseDTO(@JsonProperty("originIndex")
                                    int elementoOrigem,

                                    @JsonProperty("destinationIndex")
                                    int elementoDestino,

                                    @JsonProperty("distanceMeters")
                                    int metrosDistancia,

                                    @JsonProperty("condition")
                                    String statusRetorno) {
}
