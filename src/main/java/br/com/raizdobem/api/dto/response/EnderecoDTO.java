package br.com.raizdobem.api.dto.response;

public record EnderecoDTO(Long id, String logradouro, String cep,
                          String numero, String bairro, String cidade,
                          String estado, String tipoEndereco) {
}
