# Prompt: Implementar os 4 Metodos com Logica de Negocio (requisito avaliativo)

Os 4 metodos abaixo sao baseados diretamente nas regras de negocio da Sprint 3 (raiz-do-bem-backend-java),
adaptadas para Quarkus/Panache. Implemente cada um no respectivo Service.

---

## Metodo 1 — `criarBeneficiario` (`BeneficiarioService`)

**Regra (herdada de BeneficiarioBO.adicionar):**
Um beneficiario so pode ser criado a partir de um `PedidoAjuda` com status `APROVADO`.
Os dados pessoais sao copiados do pedido para o novo registro de beneficiario.

```java
@Transactional
public Beneficiario criarBeneficiario(Long idPedido, Long idProgramaSocial) {
    PedidoAjuda pedido = pedidoAjudaRepository.findByIdOptional(idPedido)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido de ajuda nao encontrado: " + idPedido));

    if (!StatusPedido.APROVADO.equals(pedido.getStatus())) {
        throw new RegraDeNegocioException(
            "Impossivel criar beneficiario — pedido nao aprovado. Status atual: " + pedido.getStatus());
    }

    Beneficiario beneficiario = new Beneficiario();
    beneficiario.setCpf(pedido.getCpf());
    beneficiario.setNomeCompleto(pedido.getNomeCompleto());
    beneficiario.setDataNascimento(pedido.getDataNascimento());
    beneficiario.setTelefone(pedido.getTelefone());
    beneficiario.setEmail(pedido.getEmail());
    beneficiario.setIdPedidoAjuda(idPedido);
    beneficiario.setIdEndereco(pedido.getIdEndereco());
    beneficiario.setIdProgramaSocial(idProgramaSocial);

    beneficiarioRepository.persist(beneficiario);
    return beneficiario;
}
```

---

## Metodo 2 — `processarPedido` (`PedidoAjudaService`)

**Regra (herdada de PedidoAjudaBO.validarGerarBeneficiario):**
Ao aprovar um pedido, atualiza `status_pedido` e `id_dentista` no banco.
Se o novo status for `APROVADO`, cria automaticamente o beneficiario chamando `BeneficiarioService`.

```java
@Transactional
public PedidoAjuda processarPedido(Long id, StatusPedido novoStatus, Long idDentista, Long idPrograma) {
    PedidoAjuda pedido = pedidoAjudaRepository.findByIdOptional(id)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido nao encontrado: " + id));

    if (!StatusPedido.PENDENTE.equals(pedido.getStatus())) {
        throw new RegraDeNegocioException(
            "Apenas pedidos PENDENTES podem ser processados. Status atual: " + pedido.getStatus());
    }

    if (idDentista == null || idDentista <= 0) {
        throw new ValidacaoException("ID do dentista e obrigatorio para processar o pedido.");
    }

    pedido.setStatus(novoStatus);
    pedido.setIdDentista(idDentista);

    if (StatusPedido.APROVADO.equals(novoStatus)) {
        beneficiarioService.criarBeneficiario(id, idPrograma);
    }

    return pedido;
}
```

---

## Metodo 3 — `validarCro` (`DentistaService`)

**Regra (herdada de DentistaBO.validarCro):**
O CRO do dentista deve seguir o formato: 2+ letras seguidas de exatamente 2 digitos.
Exemplo valido: `SP12`, `RJ99`.

```java
public void validarCro(String cro) {
    if (cro == null || !cro.matches("^[a-zA-Z]{2,}\\d{2}$")) {
        throw new ValidacaoException(
            "CRO invalido: '" + cro + "'. Formato esperado: 2+ letras seguidas de 2 digitos (ex: SP12).");
    }
}

@Transactional
public Dentista criar(Dentista dentista) {
    validarCro(dentista.getCroDentista());
    if (dentistaRepository.existsByCpf(dentista.getCpf())) {
        throw new ValidacaoException("CPF ja cadastrado para outro dentista: " + dentista.getCpf());
    }
    dentistaRepository.persist(dentista);
    return dentista;
}

public List<Dentista> listarDisponiveis() {
    return dentistaRepository.find("disponivel", "S").list();
}
```

---

## Metodo 4 — `validarIdadePedido` (`PedidoAjudaService`)

**Regra (herdada de PedidoAjudaBO.invalidarHomens):**
O solicitante do pedido de ajuda deve ter 18 anos ou mais para que o pedido seja valido.

```java
public void validarIdadeSolicitante(LocalDate dataNascimento) {
    if (dataNascimento == null) {
        throw new ValidacaoException("Data de nascimento e obrigatoria.");
    }
    int idade = Period.between(dataNascimento, LocalDate.now()).getYears();
    if (idade < 18) {
        throw new RegraDeNegocioException(
            "O solicitante deve ter no minimo 18 anos. Idade calculada: " + idade + " anos.");
    }
}

@Transactional
public PedidoAjuda criar(PedidoAjuda pedido) {
    validarIdadeSolicitante(pedido.getDataNascimento());
    pedido.setDataPedido(LocalDate.now());
    pedido.setStatus(StatusPedido.PENDENTE);
    pedidoAjudaRepository.persist(pedido);
    return pedido;
}
```

---

## Observacoes para a documentacao

Cada metodo acima deve ser incluido na documentacao PDF com:
- Nome e localizacao do metodo (classe + pacote)
- Descricao da regra de negocio
- Print do codigo no IDE (IntelliJ ou VS Code)
- Exemplo de chamada via Swagger UI (endpoint + payload)
