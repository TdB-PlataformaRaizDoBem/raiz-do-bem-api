# Prompt: Implementar os 4 Métodos com Lógica de Negócio (requisito avaliativo)

O rubricador exige **no mínimo 4 métodos com lógica e regras de negócio** relevantes ao sistema.
Implemente os 4 métodos abaixo — cada um em seu respectivo Service.

---

## Método 1 — `agendarAtendimento` (`AtendimentoService`)

**Regra:** Um dentista não pode ter dois atendimentos no mesmo dia e horário.  
Se o beneficiário já tiver atendimento pendente, lançar `RegraDeNegocioException`.

```java
@Transactional
public Atendimento agendarAtendimento(Atendimento atendimento) {
    // 1. Verificar se dentista já tem atendimento nesse dia/hora
    boolean conflitoHorario = atendimentoRepository
        .count("dentista = ?1 and dataHora = ?2",
               atendimento.getDentista(), atendimento.getDataHora()) > 0;
    if (conflitoHorario) {
        throw new RegraDeNegocioException(
            "Dentista já possui atendimento nesse horário.");
    }
    // 2. Verificar se o beneficiário já tem atendimento pendente
    boolean pendente = atendimentoRepository
        .count("beneficiario = ?1 and status = 'PENDENTE'",
               atendimento.getBeneficiario()) > 0;
    if (pendente) {
        throw new RegraDeNegocioException(
            "Beneficiário já possui um atendimento pendente.");
    }
    atendimento.setStatus("AGENDADO");
    atendimentoRepository.persist(atendimento);
    return atendimento;
}
```

---

## Método 2 — `avaliarElegibilidadeBeneficiario` (`BeneficiarioService`)

**Regra:** O beneficiário é elegível ao programa social se:
- Tiver pelo menos 1 pedido de ajuda APROVADO.
- Não estiver já vinculado a um programa social ativo.

```java
public boolean avaliarElegibilidade(String cpf) {
    Beneficiario beneficiario = buscarPorCpf(cpf);

    boolean temPedidoAprovado = pedidoAjudaRepository
        .count("beneficiario = ?1 and status = ?2",
               beneficiario, StatusPedido.APROVADO) > 0;

    boolean jaVinculado = beneficiario.getProgramaSocial() != null;

    return temPedidoAprovado && !jaVinculado;
}
```

---

## Método 3 — `processarPedidoAjuda` (`PedidoAjudaService`)

**Regra:** Ao processar um pedido:
- Status `PENDENTE` → `APROVADO` apenas se o beneficiário não tiver dívidas (sem atendimentos `NAO_PAGO`).
- Caso contrário, muda para `REJEITADO` com justificativa.

```java
@Transactional
public PedidoAjuda processarPedido(Long pedidoId) {
    PedidoAjuda pedido = pedidoAjudaRepository.findByIdOptional(pedidoId)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado: " + pedidoId));

    if (pedido.getStatus() != StatusPedido.PENDENTE) {
        throw new RegraDeNegocioException("Apenas pedidos PENDENTES podem ser processados.");
    }

    long inadimplencias = atendimentoRepository
        .count("beneficiario = ?1 and status = 'NAO_PAGO'", pedido.getBeneficiario());

    if (inadimplencias == 0) {
        pedido.setStatus(StatusPedido.APROVADO);
        pedido.setObservacao("Aprovado automaticamente — beneficiário sem pendências.");
    } else {
        pedido.setStatus(StatusPedido.REJEITADO);
        pedido.setObservacao("Rejeitado — beneficiário possui " + inadimplencias + " atendimento(s) não pago(s).");
    }
    return pedido;
}
```

---

## Método 4 — `calcularEstatisticasPorDentista` (`AtendimentoService`)

**Regra:** Retorna um relatório com total de atendimentos, por status, para um dentista em um período.

```java
public Map<String, Long> calcularEstatisticasPorDentista(Long dentistaId,
                                                          LocalDate inicio,
                                                          LocalDate fim) {
    Dentista dentista = dentistaRepository.findByIdOptional(dentistaId)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Dentista não encontrado: " + dentistaId));

    List<Atendimento> atendimentos = atendimentoRepository
        .list("dentista = ?1 and cast(dataHora as date) between ?2 and ?3",
              dentista, inicio, fim);

    return atendimentos.stream()
        .collect(Collectors.groupingBy(Atendimento::getStatus, Collectors.counting()));
}
```

---

## Observações para a documentação

Cada método acima deve ser incluído na documentação com:
- Nome e localização do método
- Descrição da regra de negócio
- Print do código no IDE
- Exemplo de chamada (endpoint ou teste)
