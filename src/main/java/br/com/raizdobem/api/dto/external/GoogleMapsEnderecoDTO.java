package br.com.raizdobem.api.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleMapsEnderecoDTO(@JsonProperty("waypoint")
                              PontoParadaDTO pontoParada) {
}
