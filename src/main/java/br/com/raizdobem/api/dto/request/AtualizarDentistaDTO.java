package br.com.raizdobem.api.dto.request;

public record AtualizarDentistaDTO(String telefone, String email,
                                   String categoriaDentista, EntradaEnderecoDTO endereco,
                                   String disponivel) {
}
