package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.dto.request.AtualizarBeneficiarioDTO;
import br.com.raizdobem.api.entity.Beneficiario;
import br.com.raizdobem.api.entity.Endereco;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class BeneficiarioRepository implements PanacheRepository<Beneficiario> {

    public void criar(Beneficiario beneficiario){
        persist(beneficiario);
    }

    public List<Beneficiario> listarTodos(){
        return listAll();
    }

    public Beneficiario buscarPorCpf(String cpf){
        return find("cpf", cpf).firstResult();
    }

    public Beneficiario buscarPorId(long id){
        return findById(id);
    }

    public List<Beneficiario> listarPorCidade(String cidade) {
        return list("endereco.cidade = ?1", cidade);
    }

    public List<Beneficiario> listarPorPrograma(long idProgramaSocial) {
        return list("programaSocial.id = ?1", idProgramaSocial);
    }

    public Beneficiario atualizar(String cpf, AtualizarBeneficiarioDTO dto){
        Beneficiario beneficiario = find("cpf", cpf).firstResult();
        if(beneficiario == null)
            return null;

        beneficiario.setTelefone(dto.telefone());
        beneficiario.setEmail(dto.email());

        if(dto.endereco() != null){
            Endereco endereco = beneficiario.getEndereco();

            endereco.setCep(dto.endereco().cep());
            endereco.setNumero(dto.endereco().numero());

            beneficiario.setEndereco(endereco);
        }
        return beneficiario;
    }

    public long excluir(String cpf) {
        return delete("cpf", cpf);
    }
}
