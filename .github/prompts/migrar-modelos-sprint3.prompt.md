# Prompt: Migrar Modelos da Sprint 3 para o Quarkus (Sprint 4)

O projeto anterior usava Java puro com JDBC (Sprint 3 em `raiz-do-bem-backend-java`).
Este prompt lista exatamente o que precisa ser ajustado nos modelos ja existentes no projeto Quarkus.

---

## Diferencas criticas a corrigir

### 1. `Atendimento.java` — campo renomeado
- Sprint 3 chamava o campo de **`prontuario`** (coluna `prontuario` no banco).
- O modelo atual do Quarkus pode ter um campo `descricao` — **renomear para `prontuario`**.
- Adicionar `dataInicial` (criada com `LocalDate.now()`) e `dataFinal` (nullable).

### 2. `Dentista.java` — campo `disponivel` e `croDentista`
- No banco, `disponivel` e **CHAR(1) = 'S' ou 'N'**, nao boolean.
- Mapear como `String disponivel` com metodo helper:
  ```java
  public boolean isDisponivelFlag() { return "S".equals(disponivel); }
  public void setDisponivelFlag(boolean d) { this.disponivel = d ? "S" : "N"; }
  ```
- A coluna do banco para o CRO e **`cro`** (nao `cro_dentista`).
- O Dentista tem campo `categoria` (texto, ex: "Ortodontia") — nao e FK para Especialidade.

### 3. `Beneficiario.java` — IDs como chaves estrangeiras simples
- Na Sprint 3, os relacionamentos eram guardados como IDs (`idPedidoAjuda`, `idProgramaSocial`, `idEndereco`).
- No Quarkus/JPA voce pode usar `@ManyToOne` ou manter IDs simples com `@Column`.
- **Recomendado para compatibilidade**: manter IDs (`Long`) com `@Column`.

### 4. `PedidoAjuda.java` — campo `status` e `descricaoProblema`
- Coluna real: `status_pedido` (enum armazenado como String: PENDENTE/APROVADO/REJEITADO).
- Coluna real: `descricao_problema` (nao `descricao`).
- `dataPedido` e gerada automaticamente no service (`LocalDate.now()`), nao vem do request.
- `idDentista` e nullable (preenchido apenas ao processar o pedido).

### 5. `Colaborador.java` — campo `dataContratacao`
- Adicionar `dataContratacao` (coluna `data_contratacao`, tipo DATE).

---

## Mapa completo de nomes de tabela vs. classe Java

| Classe Java     | Tabela Oracle          | PK Coluna        |
|-----------------|------------------------|------------------|
| Beneficiario    | Beneficiario           | id_beneficiario  |
| Dentista        | Dentista               | id_dentista      |
| Atendimento     | Atendimento            | id_atendimento   |
| PedidoAjuda     | Pedido_Ajuda           | id_pedido        |
| Endereco        | Endereco               | id_endereco      |
| Colaborador     | Colaborador            | id_colaborador   |
| Especialidade   | Especialidade          | id_especialidade |
| ProgramaSocial  | Programa_Social        | id_programa_social |

---

## Enums — armazenamento como String

Todos os enums devem usar `@Enumerated(EnumType.STRING)`:
- `Sexo`: MASCULINO, FEMININO
- `StatusPedido`: PENDENTE, APROVADO, REJEITADO
- `TipoEndereco`: RESIDENCIAL, COMERCIAL

---

## Checklist de ajuste dos modelos

- [ ] `Atendimento`: renomear `descricao` → `prontuario`, adicionar `dataInicial`/`dataFinal`, `idColaborador`
- [ ] `Dentista`: `disponivel` como String 'S'/'N', coluna `cro` (nao `cro_dentista`), campo `categoria`
- [ ] `PedidoAjuda`: `descricaoProblema` (coluna `descricao_problema`), `status` → `status_pedido`, `dataPedido`
- [ ] `Beneficiario`: `idPedidoAjuda`, `idProgramaSocial`, `idEndereco` como `Long` com `@Column`
- [ ] `Colaborador`: adicionar `dataContratacao`
- [ ] Todos: anotar com `@Entity`, `@Table(name=...)`, `@Id`, `@GeneratedValue`, `@Column(name=...)`
- [ ] Enums: anotar com `@Enumerated(EnumType.STRING)`
