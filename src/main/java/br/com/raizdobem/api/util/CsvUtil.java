package br.com.raizdobem.api.util;

import br.com.raizdobem.api.dto.response.*;

import java.time.LocalDate;
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
        csv.append("ID|Prontuário|Beneficiário|Dentista|Data Inicial|Data Final\n");

        for(AtendimentoDTO a : atendimentos){
            csv.append(a.id()).append("|");
            csv.append(a.prontuario()).append("|");
            csv.append(a.beneficiario()).append("|");
            csv.append(a.dentista()).append("|");
            csv.append(a.dataInicial()).append("|");
            csv.append(a.dataFim()).append("\n");
        }
        return csv.toString();
    }

    public static String gerarCsvBeneficiarios(List<BeneficiarioDTO> beneficiarios){
        StringBuilder csv = new StringBuilder();
        csv.append("ID,CPF,Nome Completo,Data de Nascimento,Telefone,Email,IdPedidoAjuda,ProgramaSocial,Logradouro,Número,Cidade,Estado\n");

        for(BeneficiarioDTO b : beneficiarios){
            csv.append(b.id()).append(",");
            csv.append(b.cpf()).append(",");
            csv.append(b.nomeCompleto()).append(",");
            csv.append(b.dataNascimento()).append(",");
            csv.append(b.telefone()).append(",");
            csv.append(b.email()).append(",");
            csv.append(b.pedido().id()).append(",");
            csv.append(b.programaSocial()).append(",");
            csv.append(b.endereco().logradouro()).append(",");
            csv.append(b.endereco().numero()).append(",");
            csv.append(b.endereco().cidade()).append(",");
            csv.append(b.endereco().estado()).append("\n");
        }
        return csv.toString();
    }
}
