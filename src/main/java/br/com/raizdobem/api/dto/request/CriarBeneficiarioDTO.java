package br.com.raizdobem.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record CriarBeneficiarioDTO(
                                    @NotNull
                                    Long idPedidoAjuda,

                                    @NotNull
                                    Long idProgramaSocial
                                    ) {

}
