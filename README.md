# 🌱 Raiz do Bem — API

> Backend REST da plataforma Raiz do Bem, voltada à gestão de beneficiários, atendimentos odontológicos, colaboradores, pedidos de ajuda, endereços e programas sociais.

[![Java](https://img.shields.io/badge/Java-21-orange?logo=java)](https://openjdk.org/projects/jdk/21/)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.34.5-blue?logo=quarkus)](https://quarkus.io/)
[![Oracle DB](https://img.shields.io/badge/Oracle-DB-red?logo=oracle)](https://www.oracle.com/database/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger_UI-green?logo=swagger)](http://localhost:8080/q/swagger-ui)
[![Azure DevOps](https://img.shields.io/badge/Azure-DevOps-0078D4?logo=azure-devops)](https://dev.azure.com/Tdb-Raiz-do-Bem/raiz-do-bem-backend/_git/raiz-do-bem-backend)
[![Azure App Service](https://img.shields.io/badge/Azure-App%20Service-0078D4?logo=microsoft-azure)](https://raizdobem-backend.azurewebsites.net/)

## Visão geral

A **Raiz do Bem API** é a entrega final do **FIAP Challenge 2025-26** para a disciplina *Domain Driven Design Using Java* — **Sprint 4**.

O projeto evoluiu de uma base Java/JDBC da Sprint 3 para uma API em **Quarkus 3**, com **Hibernate ORM + Panache**, **Oracle DB**, **OpenAPI/Swagger UI**, **DTOs**, **mapeadores**, **clientes REST externos** e **tratamento centralizado de exceções**.

O objetivo desta versão é manter o backend pronto para integração com front-end, com documentação clara e alinhada ao código atual exposto no Swagger.

### Repositório e Hospedagem

- **Repositório:** [Raiz-do-Bem-backend](https://dev.azure.com/Tdb-Raiz-do-Bem/raiz-do-bem-backend/_git/raiz-do-bem-backend)
- **Hospedagem / Deploy:** [raizdobem-backend](https://raizdobem-backend-g9fjaqejfygzc7h8.canadacentral-01.azurewebsites.net/)
- **Documentação Swagger:** [http://localhost:8080/q/swagger-ui](http://localhost:8080/q/swagger-ui) (desenvolvimento local)

---

## Tecnologias utilizadas

- Java 21
- Quarkus 3.34.5
- Hibernate ORM + Panache
- Oracle JDBC
- REST/JSON com Quarkus REST
- OpenAPI / Swagger UI
- REST Client com Jackson
- Gson
- JWT do Quarkus

---

## Estrutura do projeto

O pacote raiz é `br.com.raizdobem.api`.

### Camadas principais

- `resource/` → endpoints REST
- `service/` → regras de negócio
- `repository/` → acesso a dados com `PanacheRepository`
- `entity/` → entidades JPA e enums do domínio
- `dto/` → objetos de entrada, saída e integrações externas
- `exception/` → exceções customizadas e mapper global
- `mapper/` → conversão entre entidades e DTOs
- `client/` → clientes REST externos (`ViaCepClient`, `GoogleMapsClient`)
- `util/` → utilitários auxiliares

---

## Entidades e enums do domínio

### Entidades principais

- `Beneficiario`
- `PedidoAjuda`
- `ProgramaSocial`
- `Atendimento`
- `Dentista`
- `Especialidade`
- `Colaborador`
- `Endereco`

### Enums principais

- `Sexo` → `MASCULINO`, `FEMININO`
- `StatusPedido` → `PENDENTE`, `APROVADO`, `REJEITADO`
- `TipoEndereco` → `RESIDENCIAL`, `COMERCIAL`

### Observações de mapeamento

- As entidades usam os nomes reais das tabelas e colunas do Oracle.
- Os relacionamentos são mapeados com `@ManyToOne` quando há chaves estrangeiras.
- Os enums são persistidos como String com `@Enumerated(EnumType.STRING)`.
- O campo `disponivel` do `Dentista` é tratado como boolean no Java e persistido no Oracle como `CHAR(1)` (`S` / `N`).

---

## Regras de negócio implementadas nos Services

As regras mais importantes da aplicação foram concentradas na camada `service/`:

### `BeneficiarioService`

- `adicionar(idPedido, idProgramaSocial)`
  - Converte um `PedidoAjuda` aprovado em `Beneficiario`.
  - Copia os dados do pedido para o beneficiário.
  - Valida o status antes de efetivar a criação.
- `listarPorCidade(cidade)`
  - Filtra beneficiários pelo endereço.
- `listarPorPrograma(idPrograma)`
  - Filtra beneficiários por programa social.

### `DentistaService`

- `validarCro(cro)`
  - Valida o CRO com a regex `^[a-zA-Z]{2,}\d{2}$`.
- `listarDisponiveis()`
  - Busca dentistas com disponibilidade ativa.
- `listarPorCidade(cidade)`
  - Filtra por cidade do endereço.

### `PedidoAjudaService`

- `processarPedido(id, novoStatus, idDentista)`
  - Atualiza o status do pedido e o dentista responsável.
  - Quando o pedido é aprovado, aciona a criação do beneficiário.
- `listarPorData(data)`
  - Filtra pedidos por data de solicitação.
- `validarIdadePedido(dataNasc)`
  - Valida se o solicitante possui 18 anos ou mais.

### `AtendimentoService`

- `criar(prontuario, idBeneficiario, idDentista)`
  - Cria um atendimento com `dataInicial = LocalDate.now()`.
- `encerrar(id, prontuario, dataFinal, idColaborador)`
  - Atualiza `prontuario`, `dataFinal` e `idColaborador`.

### Serviços de apoio

- `EnderecoService`
- `ColaboradorService`
- `EspecialidadeService`
- `ProgramaService`
- `GoogleMapsService`
- `AtendimentoMatchService`
- `ValidacaoService`

---

## Repositórios

Os repositórios estendem `PanacheRepository<T>` e concentram o acesso ao banco com consultas por:

- CPF
- cidade
- data
- programa social
- dentistas disponíveis
- identificação por `id`

---

## Exceções e tratamento global

O projeto possui tratamento centralizado de erros em `exception/`.

### Exceções customizadas

- `NaoEncontradoException` → HTTP 404
- `ValidacaoException` → HTTP 422
- `RegraNegocioException` → HTTP 409
- `RequisicaoInvalidaException` → HTTP 400

### Mapper global

O arquivo `ExceptionsMapperGlobal.java` transforma exceções em respostas JSON padronizadas por meio de `@Provider`.

### Status HTTP documentados no projeto

| Status | Significado | Uso típico no projeto |
|---|---|---|
| 200 OK | Requisição concluída com sucesso | GET e PUT bem-sucedidos |
| 201 Created | Recurso criado | POST com persistência concluída |
| 204 No Content | Operação concluída sem corpo | DELETE bem-sucedido |
| 400 Bad Request | Requisição inválida | entrada inconsistente / dados inválidos |
| 404 Not Found | Recurso não encontrado | consulta por id/CPF inexistente |
| 409 Conflict | Conflito com regra de negócio | pedido não aprovado, violação de fluxo |
| 422 Unprocessable Entity | Validação inválida | CRO inválido, idade inválida etc. |
| 500 Internal Server Error | Erro inesperado | falhas não tratadas pelo domínio |

---

## Endpoints REST documentados no Swagger

**Base local:** `http://localhost:8080`  
**Swagger UI:** `http://localhost:8080/q/swagger-ui`

> Observação: a lista abaixo segue os endpoints atualmente expostos no Swagger do projeto.

### Atendimento

- `GET /atendimento` — listar todos
- `POST /atendimento` — criar
- `GET /atendimento/exportarCsv` — exportar atendimentos em CSV
- `GET /atendimento/{cpf}` — buscar por CPF
- `PUT /atendimento/{cpf}` — atualizar
- `DELETE /atendimento/{id}` — excluir atendimento

### Beneficiario

- `GET /beneficiario` — listar beneficiários
- `POST /beneficiario` — criar beneficiário
- `GET /beneficiario/cidade/{cidade}` — listar por cidade
- `GET /beneficiario/exportarCsv` — exportar beneficiários em CSV
- `GET /beneficiario/programa/{idProgramaSocial}` — listar por programa social
- `GET /beneficiario/{cpf}` — buscar por CPF
- `PUT /beneficiario/{cpf}` — atualizar
- `DELETE /beneficiario/{cpf}` — remover

### Colaborador

- `GET /colaborador` — listar colaboradores
- `POST /colaborador` — criar
- `GET /colaborador/{cpf}` — buscar por CPF
- `PUT /colaborador/{cpf}` — atualizar
- `DELETE /colaborador/{cpf}` — remover

### Dentista

- `GET /dentista` — listar dentistas
- `POST /dentista` — criar dentista
- `GET /dentista/cidade/{cidade}` — listar por cidade
- `GET /dentista/disponiveis` — listar disponíveis
- `GET /dentista/exportarCsv` — exportar dentistas em CSV
- `GET /dentista/{cpf}` — buscar por CPF
- `PUT /dentista/{cpf}` — atualizar
- `DELETE /dentista/{cpf}` — remover

### Endereco

- `GET /endereco` — listar endereços
- `POST /endereco` — criar endereço
- `GET /endereco/id/{id}` — buscar por ID
- `GET /endereco/viacep/{cep}` — consultar ViaCEP
- `GET /endereco/{cidade}` — listar por cidade
- `PUT /endereco/{id}` — atualizar
- `DELETE /endereco/{id}` — remover

### Especialidades

- `GET /especialidades` — listar especialidades
- `GET /especialidades/{id}` — buscar por ID

### Pedido Ajuda

- `GET /pedido-ajuda` — listar pedidos
- `POST /pedido-ajuda` — criar
- `GET /pedido-ajuda/data/{data}` — filtrar por data
- `PUT /pedido-ajuda/{id}` — atualizar / processar status
- `DELETE /pedido-ajuda/{id}` — remover

### Programas

- `GET /programas-sociais` — listar programas sociais
- `GET /programas-sociais/{id}` — buscar por ID

---

## Como executar no Windows PowerShell

### 1) Configurar variáveis de ambiente

```powershell
$env:DB_USUARIO = "seu_usuario"
$env:DB_SENHA = "sua_senha"
$env:DB_URL = "jdbc:oracle:thin:@//oracle.fiap.com.br:1521/orcl"
$env:GOOGLE_API_KEY = "sua_chave_google"
```

### 2) Executar em modo desenvolvimento

```powershell
.\mvnw.cmd quarkus:dev
```

### 3) Compilar o projeto

```powershell
.\mvnw.cmd compile
```

### 4) Executar testes

```powershell
.\mvnw.cmd test
```

### 5) Gerar pacote de produção

```powershell
.\mvnw.cmd package -DskipTests
```

---

## Documentação e evidências

A pasta `docs/` reúne os materiais de apoio do projeto:

- `docs/diagrams/` — diagramas do sistema
- `docs/prints_swagger/` — evidências dos endpoints no Swagger
- `docs/banco/` — materiais e script da Sprint 3
- `docs/documentacao-java/` — documentação complementar

### Materiais úteis para a entrega final

- prints de execução real no Swagger
- diagrama de classes atualizado
- MER e mapeamento das entidades
- instruções de execução e build
- tabela de endpoints e códigos HTTP

---

## Checklist da Sprint 4

- [ ] ≥ 6 classes modelo com encapsulamento completo e anotações JPA
- [ ] Conexão Oracle configurada via variáveis de ambiente
- [ ] Repositories estendem `PanacheRepository`
- [ ] ≥ 4 métodos com lógica de negócio implementados nos Services
- [ ] Exceções customizadas + `ExceptionsMapperGlobal`
- [ ] CRUD completo nos controllers com `Response` tipado
- [ ] Endpoints documentados com OpenAPI/Swagger
- [ ] `import.sql` com dados de demonstração
- [ ] Código compila e roda sem erros (`.\mvnw.cmd compile`)

---

## Evolução do projeto

Resumo das principais melhorias da Sprint 3 para a Sprint 4:

- migração para Quarkus 3;
- adoção de Hibernate ORM + Panache;
- centralização das regras de negócio em `service/`;
- mapeamento JPA compatível com o Oracle existente;
- padronização de erros com `ExceptionsMapperGlobal`;
- documentação OpenAPI/Swagger;
- integração com ViaCEP e clientes REST externos;
- organização de evidências e documentação em `docs/`.

---

## Observações finais

- O Swagger deve ser considerado a fonte mais atual dos endpoints.
- Os nomes de variáveis de ambiente seguem o arquivo `application.properties`.
- Recursos como `Especialidade` e `ProgramaSocial` são de leitura na API atual.
- Antes da entrega, vale validar os testes do projeto e abrir `http://localhost:8080/q/swagger-ui` para conferir a lista final de rotas.

