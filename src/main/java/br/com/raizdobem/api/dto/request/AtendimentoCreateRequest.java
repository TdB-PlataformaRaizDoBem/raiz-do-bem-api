package br.com.raizdobem.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AtendimentoCreateRequest(@NotBlank
                                  String prontuario,
                                       @NotBlank
                                  String cpfBeneficiario) {

}
