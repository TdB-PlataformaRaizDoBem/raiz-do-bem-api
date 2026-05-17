package br.com.raizdobem.api.dto.request;

public record AtualizarBeneficiarioDTO(String telefone,
                                       String email,
                                       EntradaEnderecoDTO endereco) {
}
