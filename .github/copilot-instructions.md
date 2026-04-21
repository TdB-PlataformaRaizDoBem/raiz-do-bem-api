# Raiz do Bem API — Instruções para o GitHub Copilot

## Visão Geral do Projeto

**Solução:** Raiz do Bem — plataforma de gestão de beneficiários, atendimentos odontológicos, colaboradores e programas sociais.  
**Contexto acadêmico:** FIAP Challenge 2025-26 · disciplina Domain Driven Design Using Java · **Sprint 4 (entrega final)**.  
**Stack:** Java 21 · Quarkus 3.x · Maven · Oracle DB (servidor FIAP) · REST/JSON · OpenAPI (Swagger UI).

> **Base:** O projeto migrou de um backend Java puro com JDBC (Sprint 3 em `raiz-do-bem-backend-java`) para Quarkus com Panache (Sprint 4). Os nomes de tabela, colunas e regras de negócio abaixo são exatamente os do banco Oracle já existente.

### Pacote raiz
```
br.com.raizdobem.api
```

### Estrutura de camadas (DDD)
```
controller/   → recursos REST (JAX-RS) — camada de apresentação
service/      → regras de negócio (BO) — camada de domínio
repository/   → acesso a dados (DAO) — camada de infraestrutura
model/        → entidades e enums — camada de domínio
exception/    → exceções customizadas — camada transversal
```

---

## Banco de Dados Oracle — Mapeamento Real

**URL do servidor:** `jdbc:oracle:thin:@//oracle.fiap.com.br:1521/orcl`  
**Credenciais:** via variáveis de ambiente `DB_USER` e `DB_PASSWORD` (nunca hardcode).

### Tabelas e colunas (exatamente como existem no banco)

#### Tabela `Beneficiario`
| Coluna JPA | Coluna DB | Tipo |
|---|---|---|
| `id` | `id_beneficiario` | NUMBER (PK, sequence) |
| `cpf` | `cpf` | VARCHAR2 |
| `nomeCompleto` | `nome_completo` | VARCHAR2 |
| `dataNascimento` | `data_nascimento` | DATE |
| `telefone` | `telefone` | VARCHAR2 |
| `email` | `email` | VARCHAR2 |
| `idPedidoAjuda` | `id_pedido_ajuda` | NUMBER (FK) |
| `idProgramaSocial` | `id_programa_social` | NUMBER (FK) |
| `idEndereco` | `id_endereco` | NUMBER (FK) |

#### Tabela `Dentista`
| Coluna JPA | Coluna DB | Tipo |
|---|---|---|
| `id` | `id_dentista` | NUMBER (PK, sequence) |
| `croDentista` | `cro` | VARCHAR2 — formato `^[a-zA-Z]{2,}\d{2}$` |
| `cpf` | `cpf` | VARCHAR2 |
| `nomeCompleto` | `nome_completo` | VARCHAR2 |
| `sexo` | `sexo` | VARCHAR2 (enum name: MASCULINO/FEMININO) |
| `email` | `email` | VARCHAR2 |
| `telefone` | `telefone` | VARCHAR2 |
| `categoria` | `categoria` | VARCHAR2 (especialidade textual) |
| `disponivel` | `disponivel` | CHAR(1) — 'S' ou 'N' |
| `idEndereco` | `id_endereco` | NUMBER (FK) |

#### Tabela `Atendimento`
| Coluna JPA | Coluna DB | Tipo |
|---|---|---|
| `id` | `id_atendimento` | NUMBER (PK, sequence) |
| `prontuario` | `prontuario` | VARCHAR2 |
| `dataInicial` | `data_inicial` | DATE |
| `dataFinal` | `data_final` | DATE (nullable) |
| `idBeneficiario` | `id_beneficiario` | NUMBER (FK) |
| `idDentista` | `id_dentista` | NUMBER (FK) |
| `idColaborador` | `id_colaborador` | NUMBER (FK, nullable) |

#### Tabela `Pedido_Ajuda`
| Coluna JPA | Coluna DB | Tipo |
|---|---|---|
| `id` | `id_pedido` | NUMBER (PK, sequence) |
| `cpf` | `cpf` | VARCHAR2 |
| `nomeCompleto` | `nome_completo` | VARCHAR2 |
| `dataNascimento` | `data_nascimento` | DATE |
| `sexo` | `sexo` | VARCHAR2 (enum name) |
| `telefone` | `telefone` | VARCHAR2 |
| `email` | `email` | VARCHAR2 |
| `descricaoProblema` | `descricao_problema` | VARCHAR2 |
| `dataPedido` | `data_pedido` | DATE |
| `status` | `status_pedido` | VARCHAR2 (PENDENTE/APROVADO/REJEITADO) |
| `idEndereco` | `id_endereco` | NUMBER (FK) |
| `idDentista` | `id_dentista` | NUMBER (FK, nullable) |

#### Tabela `Endereco`
| Coluna JPA | Coluna DB | Tipo |
|---|---|---|
| `id` | `id_endereco` | NUMBER (PK, sequence) |
| `logradouro` | `logradouro` | VARCHAR2 |
| `cep` | `cep` | VARCHAR2 |
| `numero` | `numero` | VARCHAR2 |
| `bairro` | `bairro` | VARCHAR2 |
| `cidade` | `cidade` | VARCHAR2 |
| `estado` | `estado` | VARCHAR2 |
| `tipo` | `tipo_endereco` | VARCHAR2 (RESIDENCIAL/COMERCIAL) |

#### Tabela `Colaborador`
| Coluna JPA | Coluna DB | Tipo |
|---|---|---|
| `id` | `id_colaborador` | NUMBER (PK, sequence) |
| `cpf` | `cpf` | VARCHAR2 |
| `nomeCompleto` | `nome_completo` | VARCHAR2 |
| `dataNascimento` | `data_nascimento` | DATE |
| `dataContratacao` | `data_contratacao` | DATE |
| `email` | `email` | VARCHAR2 |

#### Tabela `Especialidade`
| Coluna JPA | Coluna DB | Tipo |
|---|---|---|
| `id` | `id_especialidade` | NUMBER (PK, sequence) |
| `nome` | `nome` | VARCHAR2 |

#### Tabela `Programa_Social` (alias ProgramaSocial)
| Coluna JPA | Coluna DB | Tipo |
|---|---|---|
| `id` | `id_programa_social` | NUMBER (PK, sequence) |
| `nome` | `nome` | VARCHAR2 |
| `descricao` | `descricao` | VARCHAR2 |

---

## Regras de Negócio da Sprint 3 (migrar para os Services)

### BeneficiarioService
- `adicionar(idPedido, idProgramaSocial)` — busca o `PedidoAjuda`; se status **não for APROVADO**, lança exceção; caso aprovado, cria `Beneficiario` copiando os dados do pedido.
- `listarPorCidade(cidade)` — filtra beneficiários por cidade do endereço (JOIN com Endereco).
- `listarPorPrograma(idPrograma)` — filtra por `id_programa_social`.

### DentistaService
- `validarCro(cro)` — regex: `^[a-zA-Z]{2,}\d{2}$`. Lança `ValidacaoException` se inválido.
- `listarDisponiveis()` — busca dentistas com `disponivel = 'S'` / `disponivel = true`.
- `listarPorCidade(cidade)` — JOIN com Endereco.

### PedidoAjudaService
- `processarPedido(id, novoStatus, idDentista)` — atualiza `status_pedido` e `id_dentista`; se `novoStatus == APROVADO`, chama `BeneficiarioService.adicionar(id, idPrograma)`.
- `listarPorData(data)` — filtra por `data_pedido`.
- `validarIdadePedido(dataNasc)` — retorna `true` se idade >= 18 anos (`Period.between`).

### AtendimentoService
- `criar(prontuario, idBeneficiario, idDentista)` — cria com `dataInicial = LocalDate.now()`.
- `encerrar(id, prontuario, dataFinal, idColaborador)` — atualiza `data_final`, `prontuario` e `id_colaborador`.

---

## Entidades existentes no projeto Quarkus

| Classe          | Papel principal                                       |
|-----------------|-------------------------------------------------------|
| Beneficiario    | Pessoa atendida pelos programas sociais               |
| PedidoAjuda     | Solicitação de auxílio do beneficiário                |
| ProgramaSocial  | Programa de assistência                               |
| Atendimento     | Consulta odontológica (campo `prontuario`, não `descricao`) |
| Dentista        | Profissional com CRO, categoria textual e flag `disponivel` |
| Especialidade   | Especialidade vinculável ao dentista                  |
| Colaborador     | Voluntário/funcionário                                |
| Endereco        | Endereço com `logradouro`, `cep`, `bairro`, `cidade`, `estado`, `tipo` |
| Sexo            | enum: MASCULINO / FEMININO                            |
| StatusPedido    | enum: PENDENTE / APROVADO / REJEITADO                 |
| TipoEndereco    | enum: RESIDENCIAL / COMERCIAL                        |

---

## Requisitos da Sprint 4

### 1. Conexão com o banco de dados (Oracle)
- Descomentar `quarkus-hibernate-orm-panache` e `quarkus-jdbc-oracle` no `pom.xml`.
- Anotar entidades com `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`, `@ManyToOne`.
- Usar os nomes de tabela/coluna **exatamente** como na seção "Banco de Dados Oracle" acima.
- `application.properties`:
  ```properties
  quarkus.datasource.db-kind=oracle
  quarkus.datasource.username=${DB_USER}
  quarkus.datasource.******
  quarkus.datasource.jdbc.url=${DB_URL:jdbc:oracle:thin:@//oracle.fiap.com.br:1521/orcl}
  quarkus.hibernate-orm.database.generation=update
  quarkus.swagger-ui.always-include=true
  ```

### 2. Repository (DAO) — estender `PanacheRepository<T>`
- Métodos extras com JPQL para buscas por CPF, cidade, programa, data etc.

### 3. Service (BO) — `@ApplicationScoped`, `@Inject`, `@Transactional`
- Implementar os 4 métodos de negócio listados acima.

### 4. Exceções — pacote `exception/`
- `RecursoNaoEncontradoException` → HTTP 404
- `ValidacaoException` → HTTP 422
- `RegraDeNegocioException` → HTTP 409
- `GlobalExceptionMapper` anotado com `@Provider`

### 5. Controllers REST — `@RequestScoped`, CRUD completo
- `GET /`, `POST /`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`
- Retornar `Response` com status correto
- Anotar com `@Operation` e `@APIResponse`

---

## Convenções de código

- **Java 21** — usar pattern matching e records onde fizer sentido.
- **Sem Lombok** — getters/setters/construtores explícitos.
- **Quarkus CDI**: `@ApplicationScoped` para services/repositories; `@RequestScoped` para controllers.
- **Transactions**: `@Transactional` em métodos de escrita no service.
- **Enums armazenados como String** (`@Enumerated(EnumType.STRING)`).
- **`disponivel` no Dentista**: boolean no Java, char 'S'/'N' no Oracle — usar `@Column(columnDefinition = "CHAR(1)")` com `@Convert` ou mapeamento manual via `@PostLoad`.

---

## Comandos de desenvolvimento

```bash
./mvnw quarkus:dev        # hot reload
./mvnw compile            # compilar
./mvnw test               # testes unitários
./mvnw package -DskipTests # JAR de produção
```

Swagger UI: `http://localhost:8080/q/swagger-ui`

---

## Checklist da entrega final (Sprint 4)

- [ ] ≥ 6 classes modelo com encapsulamento completo e anotações JPA
- [ ] Conexão Oracle configurada via variáveis de ambiente (URL do servidor FIAP)
- [ ] Repositories estendem `PanacheRepository`
- [ ] ≥ 4 métodos com lógica de negócio implementados nos Services
- [ ] Exceções customizadas + GlobalExceptionMapper
- [ ] CRUD completo em todos os controllers com `Response` tipado
- [ ] Endpoints documentados com OpenAPI/Swagger
- [ ] `import.sql` com dados de demonstração
- [ ] Código compila e roda sem erros (`./mvnw compile`)
