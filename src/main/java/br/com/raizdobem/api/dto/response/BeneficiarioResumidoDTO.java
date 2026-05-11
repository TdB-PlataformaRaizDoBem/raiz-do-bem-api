package br.com.raizdobem.api.dto.response;

public record BeneficiarioResumidoDTO(
        Long id,
        String cpf,
        String nomeCompleto,
        String cidade
) {}

