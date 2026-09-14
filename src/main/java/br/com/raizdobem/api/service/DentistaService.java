package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.DentistaUpdateRequest;
import br.com.raizdobem.api.dto.request.DentistaCreateRequest;
import br.com.raizdobem.api.dto.response.DentistaDTO;
import br.com.raizdobem.api.entity.*;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.mapper.DentistaMapper;
import br.com.raizdobem.api.repository.DentistaRepository;
import br.com.raizdobem.api.repository.EspecialidadeRepository;
import br.com.raizdobem.api.repository.ProgramaRepository;
import br.com.raizdobem.api.util.CpfValidatorUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

import static br.com.raizdobem.api.mapper.DentistaMapper.mapeamentoDentista;
import static br.com.raizdobem.api.mapper.DentistaMapper.mapeamentoListaDTO;

@ApplicationScoped
public class DentistaService {
    @Inject
    DentistaRepository repository;

    @Inject
    EnderecoService enderecoService;

    @Inject
    EspecialidadeRepository especialidadeRepository;

    @Inject
    ProgramaRepository programaRepository;

    @Transactional
    public DentistaDTO criarDentista(DentistaCreateRequest dto){
        if(!CpfValidatorUtil.cpfValido(dto.cpf())){
            throw new ValidacaoException("CPF inserido é inválido");
        }

        Dentista dentista = new Dentista();

        String sexoEntrada = dto.sexo().toUpperCase();

        dentista.setCpf(dto.cpf());
        dentista.setCroDentista(dto.croDentista());
        dentista.setNomeCompleto(dto.nomeCompleto());
        dentista.setSexo(Sexo.valueOf(sexoEntrada.toUpperCase()));
        dentista.setTelefone(dto.telefone());
        dentista.setEmail(dto.email());
        dentista.setCategoria(dto.categoria());
        Especialidade especialidade = especialidadeRepository.buscarPorId(dto.idEspecialidade());
        if(especialidade == null){
            throw new NaoEncontradoException("Especialidade não encontrada.");
        }
        dentista.setEspecialidades(List.of(especialidade));

        dentista.setProgramasSociais(programaRepository.listarTodos());
        dentista.setDisponivel(dto.disponivel());
        Endereco endereco = enderecoService.criarComoSuporte(dto.endereco(), TipoEndereco.PROFISSIONAL);
        if(endereco == null)
            throw new NaoEncontradoException("Endereço não encontrado!");

        dentista.setEndereco(endereco);

        repository.criar(dentista);
        return mapeamentoDentista(dentista);
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
        return mapeamentoDentista(dentista);
    }

    public Dentista buscarEntidadePorId(Long id) {
        return repository.findById(id);
    }

    public DentistaDTO exibirDentista(String cpf) {
        Dentista dentista = repository.buscarPorCpf(cpf);
        if(dentista == null)
            throw new NaoEncontradoException("Dentista não encontrado.");
        return mapeamentoDentista(dentista);
    }

    public List<DentistaDTO> listarPorCidades(String cidade) {
        List<Dentista> dentistas = repository.listarPorCidade(cidade);
        return mapeamentoListaDTO(dentistas);
    }

    @Transactional
    public DentistaDTO atualizar(String cpf, DentistaUpdateRequest request) {
        Dentista dentista = repository.buscarPorCpf(cpf);
        if(dentista == null)
            throw new NaoEncontradoException("Dentista não encontrado.");

        dentista.setTelefone(request.telefone());
        dentista.setEmail(request.email());
        dentista.setCategoria(request.categoriaDentista());
        dentista.setDisponivel(request.disponivel());

        enderecoService.entradaEndereco(dentista.getEndereco(), request.endereco(), TipoEndereco.RESIDENCIAL);
        return mapeamentoDentista(dentista);
    }

    @Transactional
    public long excluir(String cpf) {
        return repository.excluir(cpf);
    }

    public List<DentistaDTO> listarParaExportacao() {
        List<Dentista> dentistas = repository.listarTodos();
        return dentistas.stream()
                .map(DentistaMapper::mapeamentoDentista)
                .toList();
    }
}
