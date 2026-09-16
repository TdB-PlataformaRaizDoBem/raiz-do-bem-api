package br.com.raizdobem.api.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record AtendimentoCreateRequest(@NotBlank
                                  String prontuario,
                                       @NotBlank
                                  String cpfBeneficiario,
                                  LocalDate dataInicial) {
    @Override
    public LocalDate dataInicial() {
        return LocalDate.now();
    }
}
