package br.com.raizdobem.api.dto.response;

import java.util.List;

public record DentistaDTO(Long id,
                          String croDentista,
                          String cpf,
                          String nomeCompleto,
                          String sexo,
                          String email,
                          String telefone,
                          String categoria,
                          List<String> especialidades,
                          List<String> programasSociais,
                          String disponivel,
                          String logradouro,
                          String numero,
                          String cidade,
                          String estado) {
}




