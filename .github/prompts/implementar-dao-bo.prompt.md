# Prompt: Implementar Camadas DAO (Repository) e BO (Service)

Implemente as camadas de acesso a dados e regras de negócio para **todas** as entidades do projeto.

## Padrão Repository (DAO)

Cada repository deve:
1. Estar em `br.com.raizdobem.api.repository`
2. Ser anotado com `@ApplicationScoped`
3. Estender `PanacheRepository<Entidade>`
4. Conter métodos de busca customizados relevantes para a entidade

### Exemplo — `BeneficiarioRepository`

```java
package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.Beneficiario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class BeneficiarioRepository implements PanacheRepository<Beneficiario> {

    public Optional<Beneficiario> findByCpf(String cpf) {
        return find("cpf", cpf).firstResultOptional();
    }

    public boolean existsByCpf(String cpf) {
        return count("cpf", cpf) > 0;
    }
}
```

Crie repositories equivalentes para: `Atendimento`, `Dentista`, `Colaborador`, `PedidoAjuda`, `ProgramaSocial`, `Endereco`, `Especialidade`.

---

## Padrão Service (BO)

Cada service deve:
1. Estar em `br.com.raizdobem.api.service`
2. Ser anotado com `@ApplicationScoped`
3. Injetar o repository correspondente via `@Inject`
4. Conter os métodos CRUD básicos + lógica de negócio específica
5. Métodos de escrita devem ser `@Transactional`
6. Lançar exceções customizadas de `br.com.raizdobem.api.exception`

### Exemplo — `BeneficiarioService`

```java
package br.com.raizdobem.api.service;

import br.com.raizdobem.api.exception.RecursoNaoEncontradoException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.model.Beneficiario;
import br.com.raizdobem.api.repository.BeneficiarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class BeneficiarioService {

    @Inject
    BeneficiarioRepository repository;

    public List<Beneficiario> listarTodos() {
        return repository.listAll();
    }

    public Beneficiario buscarPorCpf(String cpf) {
        return repository.findByCpf(cpf)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Beneficiário não encontrado: " + cpf));
    }

    @Transactional
    public Beneficiario criar(Beneficiario beneficiario) {
        if (repository.existsByCpf(beneficiario.getCpf())) {
            throw new ValidacaoException("CPF já cadastrado: " + beneficiario.getCpf());
        }
        repository.persist(beneficiario);
        return beneficiario;
    }

    @Transactional
    public Beneficiario atualizar(String cpf, Beneficiario dados) {
        Beneficiario existente = buscarPorCpf(cpf);
        existente.setNomeCompleto(dados.getNomeCompleto());
        existente.setTelefone(dados.getTelefone());
        existente.setEmail(dados.getEmail());
        return existente;
    }

    @Transactional
    public void excluir(String cpf) {
        Beneficiario existente = buscarPorCpf(cpf);
        repository.delete(existente);
    }
}
```

Replique o mesmo padrão para todos os outros services do projeto.
