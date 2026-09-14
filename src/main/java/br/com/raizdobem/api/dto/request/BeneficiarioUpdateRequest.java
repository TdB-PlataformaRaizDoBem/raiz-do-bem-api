package br.com.raizdobem.api.dto.request;

public record BeneficiarioUpdateRequest(String telefone,
                                        String email,
                                        EnderecoRequest endereco) {
}
