package br.com.raizdobem.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record BeneficiarioCreateRequest(
                                    @NotNull
                                    Long idPedidoAjuda,

                                    @NotNull
                                    Long idProgramaSocial
                                    ) {

}
