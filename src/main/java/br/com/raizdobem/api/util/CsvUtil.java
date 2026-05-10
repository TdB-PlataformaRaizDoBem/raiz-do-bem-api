package br.com.raizdobem.api.util;

import br.com.raizdobem.api.dto.response.DentistaResponseDTO;

import java.util.List;

public class CsvUtil {
    public static String gerarCsvDentistas(List<DentistaResponseDTO> dentistas){
        StringBuilder csv = new StringBuilder();
        csv.append("ID,CRO,CPF,Nome Completo,Sexo,Email,Telefone,Categoria,Disponível,Logradouro,Número\n");

        for(DentistaResponseDTO dentista : dentistas){
            csv.append(dentista.getId()).append(",");
            csv.append(dentista.getCroDentista()).append(",");
            csv.append(dentista.getCpf()).append(",");
            csv.append(dentista.getNomeCompleto()).append(",");
            csv.append(dentista.getSexo()).append(",");
            csv.append(dentista.getEmail()).append(",");
            csv.append(dentista.getTelefone()).append(",");
            csv.append(dentista.getCategoria()).append(",");
            csv.append(dentista.getDisponivel()).append(",");
            csv.append(dentista.getLogradouro()).append(",");
            csv.append(dentista.getNumero()).append("\n");
        }
        return csv.toString();
    }
}
