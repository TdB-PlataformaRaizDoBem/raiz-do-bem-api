package br.com.raizdobem.api.dto.request;

import br.com.raizdobem.api.dto.response.BeneficiarioDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarAtendimentoDTO(@NotBlank
                                  String prontuario,
                                  @NotNull
                                  @Valid
                                  BeneficiarioDTO beneficiarioDTO) {

}
