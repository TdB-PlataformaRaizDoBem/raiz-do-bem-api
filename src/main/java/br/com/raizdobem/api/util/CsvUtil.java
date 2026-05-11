package br.com.raizdobem.api.util;

import br.com.raizdobem.api.dto.response.AtendimentoDTO;
import br.com.raizdobem.api.dto.response.DentistaDTO;

import java.util.List;

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
            csv.append(a.getId()).append("|");
            csv.append(a.getProntuario()).append("|");
            csv.append(a.getBeneficiario()).append("|");
            csv.append(a.getDataInicial()).append("|");
            csv.append(a.getDataFim()).append("\n");
        }
        return csv.toString();
    }
}
