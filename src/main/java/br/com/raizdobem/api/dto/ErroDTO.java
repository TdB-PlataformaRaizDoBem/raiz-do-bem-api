package br.com.raizdobem.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErroDTO {
    private int statusCode;
    private String mensagem;
}
