package br.com.raizdobem.api.dto.request;


import br.com.raizdobem.api.entity.Beneficiario;
import br.com.raizdobem.api.entity.Dentista;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CriarAtendimentoDTO {
    @NotBlank
    private String prontuario;

    @NotNull
    @Valid
    private Beneficiario beneficiario;
}
