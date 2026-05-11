package br.com.raizdobem.api.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ViaCepDTO(String cep,
                        String logradouro,
                        String bairro,
                        @JsonProperty("localidade")
                        String cidade,
                        String uf,
                        String estado,
                        @JsonProperty("erro")
                        String erro) {

}
