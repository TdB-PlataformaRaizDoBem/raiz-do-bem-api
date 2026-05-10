package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.dto.request.AtualizarDentistaDTO;
import br.com.raizdobem.api.entity.Dentista;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.ValidacaoException;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class DentistaRepository implements PanacheRepository<Dentista> {

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

        if(dto.getTelefone() == null)
            throw new NaoEncontradoException("Telefone não encontrado. Valor inserido inválido.");
        dentista.setTelefone(dto.getTelefone());

        if(dto.getEmail() == null)
            throw new NaoEncontradoException("Email não encontrado. Valor inserido inválido.");
        dentista.setEmail(dto.getEmail());

        if(dto.getCategoriaDentista() == null)
            throw new NaoEncontradoException("Categoria não encontrada. Valor inserido inválido");
        dentista.setCategoria(dto.getCategoriaDentista());

        if(dto.getDisponivel() == null)
            throw new NaoEncontradoException("Status de disponibilidade indisponível. Valor inserido inválido");
        dentista.setDisponivel(dto.getDisponivel());

        return dentista;
    }
    public long excluir(String cpf) {
        return delete("cpf", cpf);
    }
}
