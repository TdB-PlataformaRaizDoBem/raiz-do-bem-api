package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.dto.request.AtualizarDentistaDTO;
import br.com.raizdobem.api.entity.Dentista;
import br.com.raizdobem.api.entity.Endereco;
import br.com.raizdobem.api.entity.Especialidade;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DentistaRepository implements PanacheRepository<Dentista> {

    @Inject
    EspecialidadeRepository repository;

    public void criar(Dentista dentista){
        persist(dentista);
    }

    public List<Dentista> listarTodos(){
        return listAll();
    }

    public Dentista buscarPorCpf(String cpf){
        return find("cpf", cpf).firstResult();
    }

    public Dentista buscarPorId(long id){
        return findById(id);
    }

    public List<Dentista> listarPorCidade(String cidade){
        return list("endereco.cidade = ?1", cidade);
    }

    public List<Dentista> listarDisponiveis() {
        return list("disponivel", "S");
    }

    public Dentista atualizar(String cpf, AtualizarDentistaDTO dto) {
        Dentista dentista = find("cpf", cpf).firstResult();
        if(dentista == null)
            return null;

        if(dto.telefone() != null)
            dentista.setTelefone(dto.telefone());

        if(dto.email() != null)
            dentista.setEmail(dto.email());

        if(dto.categoriaDentista() != null)
            dentista.setCategoria(dto.categoriaDentista());

        if(dto.disponivel() != null)
            dentista.setDisponivel(dto.disponivel());

        if(dto.idEspecialidade()!= null){
            Especialidade especialidade = repository.buscarPorId(dto.idEspecialidade());
            dentista.setEspecialidades(
                    new ArrayList<>(List.of(especialidade)));
        }

        if(dto.endereco() != null){
            Endereco endereco = dentista.getEndereco();

            endereco.setCep(dto.endereco().cep());
            endereco.setNumero(dto.endereco().numero());

            dentista.setEndereco(endereco);
        }
        return dentista;
    }
    public long excluir(String cpf) {
        return delete("cpf", cpf);
    }
}
