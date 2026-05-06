package br.com.raizdobem.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ColaboradorRequestDTO {
    private String cpf;
    private String nomeCompleto;
    private LocalDate dataNascimento;
    private LocalDate dataContratacao;
    private String email;
}
