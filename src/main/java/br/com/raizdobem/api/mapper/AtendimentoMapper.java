package br.com.raizdobem.api.mapper;

import br.com.raizdobem.api.dto.request.AtendimentoCreateRequest;
import br.com.raizdobem.api.dto.response.AtendimentoResponse;
import br.com.raizdobem.api.entity.Atendimento;

import java.util.List;

public final class AtendimentoMapper {
    public static AtendimentoResponse mapeamentoAtendimento(Atendimento atendimento){
        if(atendimento == null){
            return null;
        }

        return new AtendimentoResponse(
                atendimento.getId(),
                atendimento.getProntuario(),
                atendimento.getBeneficiario() != null ? atendimento.getBeneficiario().getNomeCompleto() : "N/A",
                atendimento.getDentista()!= null ? atendimento.getDentista().getNomeCompleto() : "N/A",
                atendimento.getDentista()!= null ? atendimento.getDentista().getTelefone() : "N/A",
                atendimento.getDentista()!= null ? atendimento.getDentista().getEmail() : "N/A",
                atendimento.getDentista()!= null ?
                        atendimento.getDentista().getEndereco().getLogradouro() + ", " +
                        atendimento.getDentista().getEndereco().getNumero()  + ", "  +
                        atendimento.getDentista().getEndereco().getCidade() + ", " +
                        atendimento.getDentista().getEndereco().getEstado() : "N/A",
                atendimento.getDataInicial(),
                atendimento.getDataFinal() != null ? atendimento.getDataFinal().toString() : "NÃO FINALIZADO"
        );
    }

    public static List<AtendimentoResponse> mapeamentoAtendimentos(List<Atendimento> atendimentos){
        if(atendimentos == null)
            return null;

        return atendimentos.stream().map(AtendimentoMapper::mapeamentoAtendimento).toList();
    }

    public static Atendimento mapeamentoParaEntidade(AtendimentoCreateRequest request){
        return new Atendimento(request.prontuario(), request.cpfBeneficiario());
    }
}
