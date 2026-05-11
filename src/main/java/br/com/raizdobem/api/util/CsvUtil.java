package br.com.raizdobem.api.util;

import br.com.raizdobem.api.dto.response.AtendimentoDTO;
import br.com.raizdobem.api.dto.response.DentistaDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CsvUtil {
    public static String gerarCsvDentistas(List<DentistaDTO> dentistas){
        StringBuilder csv = new StringBuilder();
        csv.append("ID,CRO,CPF,Nome Completo,Sexo,Email,Telefone,Categoria,Disponível,Logradouro,Número,Cidade,Estado\n");

        for(DentistaDTO dentista : dentistas){
            csv.append(dentista.id()).append(",");
            csv.append(dentista.croDentista()).append(",");
            csv.append(dentista.cpf()).append(",");
            csv.append(dentista.nomeCompleto()).append(",");
            csv.append(dentista.sexo()).append(",");
            csv.append(dentista.email()).append(",");
            csv.append(dentista.telefone()).append(",");
            csv.append(dentista.categoria()).append(",");
            csv.append(dentista.disponivel()).append(",");
            csv.append(dentista.especialidades().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("; "))).append(",");
            csv.append(dentista.programasSociais().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("; "))).append(",");
            csv.append(dentista.logradouro()).append(" ").append(dentista.numero()).append(",");
            csv.append(dentista.cidade()).append(",");
            csv.append(dentista.estado()).append("\n");
        }
        return csv.toString();
    }

    public static String gerarCsvAtendimentos(List<AtendimentoDTO> atendimentos){
        StringBuilder csv = new StringBuilder();
        csv.append("ID|Prontuário|Beneficiário|Dentista|Data Inicial| Data Final\n");

        for(AtendimentoDTO a : atendimentos){
            csv.append(a.id()).append("|");
            csv.append(a.prontuario()).append("|");
            csv.append(a.beneficiario()).append("|");
            csv.append(a.dataInicial()).append("|");
            csv.append(a.dataFim()).append("\n");
        }
        return csv.toString();
    }
}
