package br.com.raizdobem.api.dto.request;

public record CriarEnderecoDTO(String cep, String numero,
                               String tipoEndereco, String cidade, String estado,
                               br.com.raizdobem.api.entity.TipoEndereco endereco) {

}
