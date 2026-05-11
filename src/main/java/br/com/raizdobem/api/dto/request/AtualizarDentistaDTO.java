package br.com.raizdobem.api.dto.request;

public record AtualizarDentistaDTO(String telefone, String email,
                                   String categoriaDentista, CriarEnderecoDTO endereco,
                                   String disponivel) {
}
