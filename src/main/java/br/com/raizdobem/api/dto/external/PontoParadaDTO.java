package br.com.raizdobem.api.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PontoParadaDTO(
        @JsonProperty("address")
        String endereco) {
}
