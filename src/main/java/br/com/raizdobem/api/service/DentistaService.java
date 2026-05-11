package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.AtualizarDentistaDTO;
import br.com.raizdobem.api.dto.request.CriarDentistaDTO;
import br.com.raizdobem.api.dto.response.BeneficiarioDTO;
import br.com.raizdobem.api.dto.response.DentistaDTO;
import br.com.raizdobem.api.entity.TipoEndereco;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.entity.Dentista;
import br.com.raizdobem.api.entity.Endereco;
import br.com.raizdobem.api.entity.Sexo;
import br.com.raizdobem.api.mapper.DentistaMapper;
import br.com.raizdobem.api.repository.DentistaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.raizdobem.api.mapper.DentistaMapper.mapeamentoDTO;
import static br.com.raizdobem.api.mapper.DentistaMapper.mapeamentoListaDTO;

@ApplicationScoped
public class DentistaService {
    @Inject
    DentistaRepository repository;

    @Inject
    EnderecoService enderecoService;

    @Transactional
    public DentistaDTO criarDentista(@Valid CriarDentistaDTO dto){
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
        return mapeamentoDTO(dentista);
    }

    public List<DentistaDTO> listarTodos() {
        List<Dentista> dentistas = repository.listarTodos();
        return mapeamentoListaDTO(dentistas);
    }

    public List<DentistaDTO> listarDisponiveis() {
        List<Dentista> dentistas = repository.listarDisponiveis();
        return mapeamentoListaDTO(dentistas);
    }

    public DentistaDTO buscarPorId(Long id) {
        Dentista dentista = repository.findById(id);
        if(dentista == null)
            throw new NaoEncontradoException("Dentista não encontrado.");
        return mapeamentoDTO(dentista);
    }

    public Dentista buscarEntidadePorId(Long id) {
        return repository.findById(id);
    }

    public DentistaDTO exibirDentista(String cpf) {
        Dentista dentista = repository.buscarPorCpf(cpf);
        if(dentista == null)
            throw new NaoEncontradoException("Dentista não encontrado.");
        return mapeamentoDTO(dentista);
    }

    public List<DentistaDTO> listarPorCidades(String cidade) {
        List<Dentista> dentistas = repository.listarPorCidade(cidade);
        return mapeamentoListaDTO(dentistas);
    }

    @Transactional
    public DentistaDTO atualizar(String cpf, @Valid AtualizarDentistaDTO request) {
        Dentista dentista = repository.atualizar(cpf, request);
        if(dentista == null)
            throw new NaoEncontradoException("Dentista não encontrado.");
        return mapeamentoDTO(dentista);
    }

    @Transactional
    public long excluir(String cpf) {
        return repository.excluir(cpf);
    }

    public List<DentistaDTO> listarParaExportacao() {
        List<Dentista> dentistas = repository.listarTodos();
        return dentistas.stream()
                .map(DentistaMapper::mapeamentoDTO)
                .toList();
    }
}
