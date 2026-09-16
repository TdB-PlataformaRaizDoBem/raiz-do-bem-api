package br.com.raizdobem.api.mapper;

import br.com.raizdobem.api.dto.response.EnderecoResponse;
import br.com.raizdobem.api.entity.Endereco;

import java.util.List;

public final class EnderecoMapper {
    public EnderecoMapper(){

    }

    public static EnderecoResponse mapeamentoParaResponse(Endereco endereco){
        return new EnderecoResponse(endereco.getId(), endereco.getLogradouro(), endereco.getCep(),
                                    endereco.getNumero(), endereco.getBairro(), endereco.getCidade(),
                                    endereco.getEstado(), endereco.getTipoEndereco().name());
    }

    public static List<EnderecoResponse> mapeamentoParaResponse(List<Endereco> enderecos){
        return enderecos.stream().map(EnderecoMapper::mapeamentoParaResponse).toList();
    }
}
