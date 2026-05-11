package br.com.raizdobem.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record CriarBeneficiarioDTO(@NotBlank
                                    @Pattern(
                                            regexp = "^\\d{11}$",
                                            message = "CPF deve conter 11 números."
                                    )
                                    String cpf,
                                    @NotBlank
                                    String nomeCompleto,

                                    @NotNull
                                    LocalDate dataNascimento,

                                    @NotBlank
                                    String telefone,

                                    @NotBlank
                                    String email,

                                    @NotNull
                                    Long idPedidoAjuda,

                                    @NotNull
                                    @Valid
                                   EntradaEnderecoDTO endereco,

                                    @NotNull
                                    Long idProgramaSocial
                                    ) {

}
