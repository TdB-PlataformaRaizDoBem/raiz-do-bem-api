package br.com.raizdobem.api.mapper;

import br.com.raizdobem.api.dto.response.ColaboradorResponse;
import br.com.raizdobem.api.entity.Colaborador;

import java.util.List;

public final class ColaboradorMapper {
    private ColaboradorMapper(){

    }
    public static ColaboradorResponse mapeamentoParaResponse(Colaborador colaborador){
        if(colaborador == null){
            return null;
        }
        return new ColaboradorResponse(colaborador.getId(), colaborador.getCpf(), colaborador.getNomeCompleto(),
                colaborador.getDataNascimento(), colaborador.getDataContratacao(), colaborador.getEmail(),
                colaborador.getRole()
        );
    }

    public static List<ColaboradorResponse> mapeamentoParaResponse(List<Colaborador> colaboradores) {
        if(colaboradores == null){
            return null;
        }
        return colaboradores.stream().map(ColaboradorMapper::mapeamentoParaResponse).toList();
    }
}
