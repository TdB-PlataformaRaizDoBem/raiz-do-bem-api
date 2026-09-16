package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.DentistaUpdateRequest;
import br.com.raizdobem.api.dto.request.DentistaCreateRequest;
import br.com.raizdobem.api.dto.response.DentistaResponse;
import br.com.raizdobem.api.entity.*;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.mapper.DentistaMapper;
import br.com.raizdobem.api.repository.DentistaRepository;
import br.com.raizdobem.api.repository.EspecialidadeRepository;
import br.com.raizdobem.api.repository.ProgramaRepository;
import br.com.raizdobem.api.util.CpfValidatorUtil;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

import static br.com.raizdobem.api.mapper.DentistaMapper.mapeamentoParaResponse;
import static br.com.raizdobem.api.mapper.DentistaMapper.mapeamentoParaResponse;

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
    public DentistaResponse criarDentista(DentistaCreateRequest dto){
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
        return mapeamentoParaResponse(dentista);
    }

    @CacheResult(cacheName = "dentistas")
    public List<DentistaResponse> listarTodos() {
        List<Dentista> dentistas = repository.listarTodos();
        return DentistaMapper.mapeamentoParaResponse(dentistas);
    }

//    @CacheResult(cacheName = "dentistas-disponiveis")
    public List<DentistaResponse> listarDisponiveis() {
        List<Dentista> dentistas = repository.listarDisponiveis();
        return DentistaMapper.mapeamentoParaResponse(dentistas);
    }

    public DentistaResponse buscarPorId(Long id) {
        Dentista dentista = repository.findById(id);
        if(dentista == null)
            throw new NaoEncontradoException("Dentista não encontrado.");
        return mapeamentoParaResponse(dentista);
    }

    public Dentista buscarEntidadePorId(Long id) {
        return repository.findById(id);
    }

    public DentistaResponse exibirDentista(String cpf) {
        Dentista dentista = repository.buscarPorCpf(cpf);
        if(dentista == null)
            throw new NaoEncontradoException("Dentista não encontrado.");
        return mapeamentoParaResponse(dentista);
    }

    public List<DentistaResponse> listarPorCidades(String cidade) {
        List<Dentista> dentistas = repository.listarPorCidade(cidade);
        return DentistaMapper.mapeamentoParaResponse(dentistas);
    }

    @Transactional
    public DentistaResponse atualizar(String cpf, DentistaUpdateRequest request) {
        Dentista dentista = repository.buscarPorCpf(cpf);
        if(dentista == null)
            throw new NaoEncontradoException("Dentista não encontrado.");

        dentista.setTelefone(request.telefone());
        dentista.setEmail(request.email());
        dentista.setCategoria(request.categoriaDentista());
        dentista.setDisponivel(request.disponivel());

        enderecoService.entradaEndereco(dentista.getEndereco(), request.endereco(), TipoEndereco.RESIDENCIAL);
        return mapeamentoParaResponse(dentista);
    }

    @Transactional
    public long excluir(String cpf) {
        return repository.excluir(cpf);
    }
}
