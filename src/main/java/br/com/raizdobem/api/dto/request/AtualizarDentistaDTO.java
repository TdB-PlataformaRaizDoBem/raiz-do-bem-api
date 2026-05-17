package br.com.raizdobem.api.dto.request;

public record AtualizarDentistaDTO(String telefone, String email,
                                   String categoriaDentista, Long idEspecialidade,
                                   String disponivel, EntradaEnderecoDTO endereco) {
}
