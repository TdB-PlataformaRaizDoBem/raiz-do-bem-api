package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.AtualizarDentistaDTO;
import br.com.raizdobem.api.dto.request.CriarDentistaDTO;
import br.com.raizdobem.api.dto.response.DentistaDTO;
import br.com.raizdobem.api.entity.TipoEndereco;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.entity.Dentista;
import br.com.raizdobem.api.entity.Endereco;
import br.com.raizdobem.api.entity.Sexo;
import br.com.raizdobem.api.repository.DentistaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DentistaService {
    @Inject
    DentistaRepository repository;

    @Inject
    EnderecoService enderecoService;

    @Transactional
    public Dentista criarDentista(@Valid CriarDentistaDTO dto){
        Dentista dentista = new Dentista();

        String cpfEntrada = dto.cpf();
        String sexoEntrada = dto.sexo().toUpperCase();

        if(ValidacaoService.validarCpf(cpfEntrada))
            dentista.setCpf(cpfEntrada);
        else
            throw new ValidacaoException("CPF inserido é inválido");

        dentista.setCroDentista(dto.croDentista());
        dentista.setNomeCompleto(dto.nomeCompleto());
        dentista.setSexo(Sexo.valueOf(sexoEntrada));
        dentista.setTelefone(dto.telefone());
        dentista.setEmail(dto.email());
        dentista.setCategoria(dto.categoria());
        Endereco endereco = enderecoService.criar(dto.endereco());
        if(endereco == null)
            throw new NaoEncontradoException("Endereço não encontrado!");
        else{
            endereco.setTipoEndereco(TipoEndereco.PROFISSIONAL);
            dentista.setEndereco(endereco);
        }


        dentista.setDisponivel(dto.disponivel());

        repository.criar(dentista);
        return dentista;
    }

    public List<Dentista> listarTodos() {
        return repository.listarTodos();
    }

    public List<Dentista> listarDisponiveis() {
        return repository.listarDisponiveis();
    }

    public Dentista buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Dentista exibirDentista(String cpf) {
        return repository.buscarPorCpf(cpf);
    }

    public List<Dentista> listarPorCidades(String cidade) {
        return repository.listarPorCidade(cidade);
    }

    @Transactional
    public Dentista atualizar(String cpf, @Valid AtualizarDentistaDTO request) {
        return repository.atualizar(cpf, request);
    }

    @Transactional
    public long excluir(String cpf) {
        return repository.excluir(cpf);
    }

    public List<DentistaDTO> listarParaExportacao() {
        return listarTodos().stream()
                .map(d -> new DentistaDTO(
                        d.getId(),
                        d.getCroDentista(),
                        d.getCpf(),
                        d.getNomeCompleto(),
                        d.getSexo() != null ? d.getSexo().name() : "",
                        d.getEmail(),
                        d.getTelefone(),
                        d.getTelefone(),
                        d.getDisponivel(),
                        d.getEndereco() != null ? d.getEndereco().getLogradouro() : "N/A",
                        d.getEndereco() != null ? d.getEndereco().getNumero() : "N/A",
                        d.getEndereco() != null ? d.getEndereco().getCidade() : "N/A",
                        d.getEndereco() != null ? d.getEndereco().getEstado() : "N/A"
                    ))
                .collect(Collectors.toList());
    }
}
