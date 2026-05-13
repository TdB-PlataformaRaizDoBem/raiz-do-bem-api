package br.com.raizdobem.api.dto.request;

import br.com.raizdobem.api.entity.TipoEndereco;

public record EntradaEnderecoDTO(String cep, String numero,
                                 TipoEndereco tipoEndereco) {

}
