package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.AtualizarBeneficiarioDTO;
import br.com.raizdobem.api.dto.request.CriarBeneficiarioDTO;
import br.com.raizdobem.api.dto.request.CriarEnderecoDTO;
import br.com.raizdobem.api.dto.response.BeneficiarioDTO;
import br.com.raizdobem.api.dto.response.EnderecoDTO;
import br.com.raizdobem.api.dto.response.PedidoAjudaResumidoDTO;
import br.com.raizdobem.api.entity.ProgramaSocial;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.entity.Beneficiario;
import br.com.raizdobem.api.entity.PedidoAjuda;
import br.com.raizdobem.api.repository.BeneficiarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class BeneficiarioService {
    @Inject
    BeneficiarioRepository repository;

    @Inject
    PedidoAjudaService pedidoAjudaService;

    @Inject
    EnderecoService enderecoService;

    @Inject
    ProgramaService programaService;


    @Transactional
    public Beneficiario criarBeneficiario(CriarBeneficiarioDTO dto) {
        Beneficiario beneficiario = new Beneficiario();

        if(ValidacaoService.validarCpf(dto.getCpf()))
            beneficiario.setCpf(dto.getCpf());
        else
            throw new ValidacaoException("CPF inserido é inválido");

        PedidoAjuda pedido = pedidoAjudaService.buscarPorCpf(dto.getCpf());
        if(beneficiario.getCpf().equals(pedido.getCpf())){
            beneficiario.setNomeCompleto(pedido.getNomeCompleto());
            beneficiario.setDataNascimento(pedido.getDataNascimento());
            beneficiario.setTelefone(pedido.getTelefone());
            beneficiario.setEmail(pedido.getEmail());
            beneficiario.setEndereco(pedido.getEndereco());
            beneficiario.setPedido(pedido);
        }

        ProgramaSocial programaSocial = programaService.buscarPorId(dto.getIdProgramaSocial());
        if(programaSocial == null)
            throw new NaoEncontradoException("Programa social não encontrado.");
        else{
            beneficiario.setProgramaSocial(programaSocial);
        }

        repository.criar(beneficiario);
        return beneficiario;
    }

    public Beneficiario buscarPorCpf(String cpf) {
        if(!ValidacaoService.validarCpf(cpf))
            throw new ValidacaoException("CPF inválido.");
        return repository.buscarPorCpf(cpf);
    }

    public Beneficiario buscarPorId(Long id) {
        return repository.buscarPorId(id);
    }

    public List<BeneficiarioDTO> listarTodos() {
        List <Beneficiario> beneficiarios = repository.listarTodos();

        return beneficiarios.stream()
                .map(b -> new BeneficiarioDTO(
                  b.getId(),
                  b.getCpf(),
                  b.getNomeCompleto(),
                  b.getDataNascimento(),
                  b.getTelefone(),
                  b.getEmail(), b.getPedido() != null ? new PedidoAjudaResumidoDTO(
                          b.getPedido().getId(),
                          b.getPedido().getDentista() != null ? b.getPedido().getDentista().getNomeCompleto() : null
                        ) : null,
                  b.getProgramaSocial() != null ? b.getProgramaSocial().getPrograma() : "N/A",
                  b.getEndereco() != null ? new EnderecoDTO(
                          b.getEndereco().getId(),
                          b.getEndereco().getLogradouro(),
                          b.getEndereco().getCep(),
                          b.getEndereco().getNumero(),
                          b.getEndereco().getBairro(),
                          b.getEndereco().getCidade(),
                          b.getEndereco().getEstado(),
                          b.getEndereco().getTipoEndereco() !=null ? b.getEndereco().getTipoEndereco().name() : null
                ) : null
            ))
                .toList();
    }

    public List<Beneficiario> listarPorCidade(String cidade) {
        return repository.listarPorCidade(cidade);
    }

    public List<Beneficiario> listarPorPrograma(long idProgramaSocial) {
        return repository.listarPorPrograma(idProgramaSocial);
    }

    @Transactional
    public Beneficiario atualizar(String cpf, AtualizarBeneficiarioDTO request) {
        Beneficiario beneficiario = repository.atualizar(cpf, request);
        if(beneficiario == null)
            throw new NaoEncontradoException("Beneficiário não encontrado, CPF inválido.");

        return beneficiario;
    }

    @Transactional
    public long excluir(String cpf) {
        return repository.excluir(cpf);
    }
}
