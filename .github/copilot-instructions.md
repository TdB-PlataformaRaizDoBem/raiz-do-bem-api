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

---

## Requisitos totais da entrega (Rubrica oficial — Domain Driven Design Using Java)

> Objetivo: entrega final do projeto Java, avaliando o desenvolvimento completo da aplicação (técnico + documental), com todos os elementos necessários para funcionamento integrado com front-end e documentação comprobatória.

### Entrega via GitHub (obrigatório)

O repositório deve conter:
- Código-fonte Java atualizado.
- Documentação final **em PDF**.

---

## Documentação (15 pontos) — itens obrigatórios

A documentação deve estar **clara**, **devidamente nomeada**, **organizada** e conter obrigatoriamente:

### 1) Capa
- Nome dos integrantes
- Nome da equipe
- Nome da solução

### 2) Objetivo e escopo do projeto
- Descrição objetiva e concisa da solução proposta
- Breve descrição das principais funcionalidades
- Destaque das funcionalidades implementadas (atualizadas)

### 3) Métodos com lógica (explicação + evidência)
- Explicação de **cada método lógico**
- Incluir **print** de cada um dos **4 métodos exigidos** (via Swagger/execução real)

### 4) Tabela de Endpoints (API RESTful)
- URIs (caminhos completos dos recursos)
- Verbos HTTP (GET, POST, PUT, DELETE etc.)
- Códigos de status esperados (200, 201, 204, 400, 404, 409, 422, 500 etc.)

### 5) Protótipo — prints das telas implementadas
- Capturas de tela reais do sistema
- Explicações resumidas

### 6) MER (Modelo Entidade-Relacionamento)
- Diagrama do banco com entidades, atributos, chaves e relacionamentos

### 7) Diagrama de Classes atualizado
- Classes com atributos, métodos e relações (associação, herança etc.)

### 8) Procedimentos para rodar a aplicação
- Instruções para executar (atualizadas em relação à Sprint anterior)

---

## Projeto Java finalizado (Código-fonte) — 85 pontos

### Camada Modelo (entities/beans) — 10 pontos
- Classes modelo corretamente estruturadas: atributos, construtores, getters/setters
- Mínimo **6 classes** com encapsulamento e boas práticas
- Deve representar corretamente o diagrama de classes e o modelo do banco

### Conexão com BD + Métodos com lógica de negócio — 30 pontos
- Utilização de dependências no `pom.xml`
- Uso de usuário/senha para conexão (via env vars, sem hardcode)
- Implementação de no mínimo **4 métodos relevantes**
- Métodos devem conter lógica / regras de negócio coerentes com a solução
- Complexidade e criatividade contam na avaliação

### Camadas Exceções, DAO e BO (Service) — 10 pontos
- Tratamento adequado de exceções
- Contém funcionalidades essenciais para o front-end
- CRUD funcionando completamente (Create, Read, Update, Delete)
- Validações e regras de negócio adequadas

### API RESTful — 35 pontos
- Implementação de todos os endpoints necessários para o front-end via Quarkus
- Seguir princípios REST (recursos, verbos HTTP, status de resposta etc.)

---

## Penalidades (podem derrubar bastante a nota)

### Penalidades gerais
- Documentação desorganizada: **-5 a -20 pontos**
- Documentação em formato diferente de PDF: **-10 a -20 pontos**
- Erro de nomenclatura no projeto: **-10 a -30 pontos** (dependendo da gravidade)
- Falta de organização no código: **-5 a -20 pontos**
- Baixa resolução das imagens na documentação: **-5 pontos por item**
- Sem link do GitHub: **-35 a -85 pontos**
- Entrega após o prazo: **não serão aceitos** (penalização total)

### Penalidades por ausência de itens obrigatórios
- Ausência de qualquer item obrigatório na documentação: **até -5 por item**, além da perda do conteúdo
- Entrega fora do padrão solicitado: **-10 pontos**
- Código que não compila/não executa corretamente: **-20 a -50 pontos**
- Falta de pelo menos um método com lógica de negócio: **-5 a -10 por método faltante**
- CRUD incompleto ou com falhas graves: **-10 por item**
- Entrega após o prazo: **não serão aceitos** (penalização total)

---

## Dica prática (para guiar implementações)

Sempre que implementar algo relevante para a rubrica, também:
- Garanta resposta REST com status correto + JSON de erro (via `ExceptionsMapperGlobal`)
- Gere evidência no Swagger (print) e atualize `docs/PROGRESSO_SPRINT4.md`
