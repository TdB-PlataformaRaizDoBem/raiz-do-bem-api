package br.com.raizdobem.api.dto.response;

import java.util.List;

public record PageResponse<T>(
        List<T> conteudo,
        int pagina,
        int tamanho,
        long totalElementos,
        int totalPaginas
){}
