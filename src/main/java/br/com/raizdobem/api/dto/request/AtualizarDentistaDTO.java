package br.com.raizdobem.api.dto.request;

import br.com.raizdobem.api.entity.Especialidade;

public record AtualizarDentistaDTO(String telefone, String email,
                                   String categoriaDentista, Long idEspecialidade,
                                   String disponivel, EntradaEnderecoDTO endereco) {
}
