package br.com.raizdobem.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErroDTO {
    public int statusCode;
    public String mensagem;

    public ErroDTO(int statusCode, String mensagem) {
        this.statusCode = statusCode;
        this.mensagem = mensagem;
    }
}
