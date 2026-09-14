package br.com.raizdobem.api.dto.request;

public record DentistaUpdateRequest(String telefone, String email,
                                    String categoriaDentista, Long idEspecialidade,
                                    String disponivel, EnderecoRequest endereco) {
}
