# Prompt: Implementar Camadas Repository (DAO) e Service (BO)

Implemente as camadas de acesso a dados e servicos para todas as entidades do projeto.
Os metodos customizados de busca abaixo sao baseados nos DAOs da Sprint 3 (JDBC puro)
adaptados para Quarkus Panache.

---

## Padrao Repository (DAO)

Cada repository deve:
1. Estar em `br.com.raizdobem.api.repository`
2. Ser anotado com `@ApplicationScoped`
3. Implementar `PanacheRepository<Entidade>`
4. Conter metodos de busca relevantes baseados nas queries da Sprint 3

### `BeneficiarioRepository.java`
```java
@ApplicationScoped
public class BeneficiarioRepository implements PanacheRepository<Beneficiario> {

    public Optional<Beneficiario> findByCpf(String cpf) {
        return find("cpf", cpf).firstResultOptional();
    }

    public boolean existsByCpf(String cpf) {
        return count("cpf", cpf) > 0;
    }

    // Sprint 3: listarPorCidade — JOIN via JPQL
    public List<Beneficiario> findByCidade(String cidade) {
        return getEntityManager()
            .createQuery(
                "SELECT b FROM BeneficiarioDTO b JOIN EnderecoDTO e ON b.idEndereco = e.id WHERE e.cidade = :cidade",
                Beneficiario.class)
            .setParameter("cidade", cidade)
            .getResultList();
    }

    public List<Beneficiario> findByPrograma(Long idPrograma) {
        return list("idProgramaSocial", idPrograma);
    }
}
```

### `DentistaRepository.java`
```java
@ApplicationScoped
public class DentistaRepository implements PanacheRepository<Dentista> {

    public Optional<Dentista> findByCpf(String cpf) {
        return find("cpf", cpf).firstResultOptional();
    }

    public boolean existsByCpf(String cpf) {
        return count("cpf", cpf) > 0;
    }

    // Sprint 3: listarDisponiveis — disponivel = 'S'
    public List<Dentista> findDisponiveis() {
        return list("disponivel", "S");
    }

    // Sprint 3: listarPorCidade
    public List<Dentista> findByCidade(String cidade) {
        return getEntityManager()
            .createQuery(
                "SELECT d FROM DentistaDTO d JOIN EnderecoDTO e ON d.idEndereco = e.id WHERE e.cidade = :cidade",
                Dentista.class)
            .setParameter("cidade", cidade)
            .getResultList();
    }
}
```

### `AtendimentoRepository.java`
```java
@ApplicationScoped
public class AtendimentoRepository implements PanacheRepository<Atendimento> {

    // Sprint 3: buscarPorCpf — JOIN com BeneficiarioDTO
    public Optional<Atendimento> findByBeneficiarioCpf(String cpf) {
        return getEntityManager()
            .createQuery(
                "SELECT a FROM AtendimentoDTO a JOIN BeneficiarioDTO b ON a.idBeneficiario = b.id WHERE b.cpf = :cpf",
                Atendimento.class)
            .setParameter("cpf", cpf)
            .getResultStream()
            .findFirst();
    }
}
```

### `PedidoAjudaRepository.java`
```java
@ApplicationScoped
public class PedidoAjudaRepository implements PanacheRepository<PedidoAjuda> {

    public Optional<PedidoAjuda> findByCpf(String cpf) {
        return find("cpf", cpf).firstResultOptional();
    }

    // Sprint 3: listarPedidosData
    public List<PedidoAjuda> findByDataPedido(LocalDate data) {
        return list("dataPedido", data);
    }

    public List<PedidoAjuda> findByStatus(StatusPedido status) {
        return list("status", status);
    }
}
```

### `EnderecoRepository.java`
```java
@ApplicationScoped
public class EnderecoRepository implements PanacheRepository<Endereco> {

    public List<Endereco> findByCidade(String cidade) {
        return list("cidade", cidade);
    }
}
```

### `ColaboradorRepository.java`
```java
@ApplicationScoped
public class ColaboradorRepository implements PanacheRepository<Colaborador> {

    public Optional<Colaborador> findByCpf(String cpf) {
        return find("cpf", cpf).firstResultOptional();
    }

    public boolean existsByCpf(String cpf) {
        return count("cpf", cpf) > 0;
    }
}
```

---

## Padrao Service (BO)

Cada service deve:
1. Estar em `br.com.raizdobem.api.service`
2. Ser anotado com `@ApplicationScoped`
3. Injetar o repository correspondente via `@Inject`
4. Metodos de escrita devem ser `@Transactional`
5. Lancar excecoes de `br.com.raizdobem.api.exception`

### Template — `BeneficiarioService.java`
```java
@ApplicationScoped
public class BeneficiarioService {

    @Inject BeneficiarioRepository beneficiarioRepository;
    @Inject PedidoAjudaRepository pedidoAjudaRepository;

    public List<Beneficiario> listarTodos() {
        return beneficiarioRepository.listAll();
    }

    public Beneficiario buscarPorCpf(String cpf) {
        return beneficiarioRepository.findByCpf(cpf)
            .orElseThrow(() -> new RecursoNaoEncontradoException("BeneficiarioDTO nao encontrado: " + cpf));
    }

    // Metodo de negocio 1 — ver implementar-logica-negocio.prompt.md
    @Transactional
    public Beneficiario criarBeneficiario(Long idPedido, Long idProgramaSocial) { /* ... */ }

    @Transactional
    public Beneficiario atualizar(String cpf, Beneficiario dados) {
        Beneficiario existente = buscarPorCpf(cpf);
        existente.setTelefone(dados.getTelefone());
        existente.setEmail(dados.getEmail());
        existente.setIdEndereco(dados.getIdEndereco());
        return existente;
    }

    @Transactional
    public void excluir(String cpf) {
        Beneficiario existente = buscarPorCpf(cpf);
        beneficiarioRepository.delete(existente);
    }

    public List<Beneficiario> listarPorCidade(String cidade) {
        return beneficiarioRepository.findByCidade(cidade);
    }

    public List<Beneficiario> listarPorPrograma(Long idPrograma) {
        return beneficiarioRepository.findByPrograma(idPrograma);
    }
}
```

Replicar o mesmo padrao para `AtendimentoService`, `DentistaService`, `ColaboradorService`,
`PedidoAjudaService`, `ProgramaService`, `EnderecoService`.
