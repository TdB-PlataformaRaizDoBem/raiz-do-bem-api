# 🌱 Raiz do Bem — API

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.34.5-4695EB?logo=quarkus&logoColor=white)](https://quarkus.io/)
[![Oracle](https://img.shields.io/badge/Oracle-Database-F80000?logo=oracle&logoColor=white)](https://www.oracle.com/database/)
[![JWT](https://img.shields.io/badge/SmallRye_JWT-RSA_256-000000?logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger_UI-85EA2D?logo=swagger&logoColor=black)](https://swagger.io/specification/)
[![Tests](https://img.shields.io/badge/Tests-285_Passing-2EA44F?logo=junit5&logoColor=white)](#5-testes-e-qualidade-de-software)

> **Backend RESTful Enterprise** desenvolvido em **Java 21 + Quarkus 3.34.5** para a plataforma **Raiz do Bem** (Challenge FIAP em parceria com a **ONG Turma do Bem**), centralizando a gestão de beneficiários, triagem de pedidos de ajuda, atendimentos odontológicos, dentistas voluntários, colaboradores, endereços georreferenciados e programas sociais.

---

## 📌 Sumário

1. [Visão Geral e Proposta de Valor](#1-visão-geral-e-proposta-de-valor)
2. [Arquitetura e Boas Práticas](#2-arquitetura-e-boas-práticas)
3. [Domínio e Match Geográfico](#3-domínio-e-match-geográfico)
4. [Tabela de Endpoints Atualizada](#4-tabela-de-endpoints-atualizada)
5. [Testes e Qualidade de Software](#5-testes-e-qualidade-de-software)
6. [Deploy, CI/CD e Execução Local](#6-deploy-cicd-e-execução-local)

---

## 1. Visão Geral e Proposta de Valor

### 🌍 Contexto Social e Acadêmico
O projeto **Raiz do Bem** nasceu no contexto do **Challenge FIAP** em parceria com a **ONG Turma do Bem**, a maior rede de voluntariado especializado do mundo voltada à saúde bucal de pessoas em situação de vulnerabilidade social — atuando principalmente por meio dos programas **Dentista do Bem** (jovens de baixa renda) e **Apolônias do Bem** (mulheres vítimas de violência).

### 🎯 Papel Centralizador da API
A **Raiz do Bem API** atua como o núcleo transacional e orquestrador de regras de negócio do ecossistema, substituindo processos manuais e descentralizados por uma arquitetura moderna, escalável e auditável:

- **Triagem e Pedidos de Ajuda**: Recebimento público de solicitações de auxílio odontológico com validação automatizada de elegibilidade e fluxo de aprovação.
- **Gestão de Beneficiários e Programas Sociais**: Conversão de pedidos aprovados em beneficiários ativos vinculados aos programas sociais da ONG.
- **Rede de Dentistas Voluntários e Especialidades**: Cadastro, qualificação por especialidades odontológicas e controle de disponibilidade de profissionais voluntários.
- **Orquestração de Atendimentos e Match Geográfico**: Vinculação inteligente entre beneficiários e dentistas voluntários com base na menor distância real de deslocamento via **Google Maps Routes API**.
- **Gestão de Colaboradores e Governança**: Controle de acesso baseado em papéis (`ADMIN` e `COLABORADOR`) e exportação de relatórios operacionais em formato **CSV**.

---

## 2. Arquitetura e Boas Práticas

A aplicação adota uma **Arquitetura em Camadas (Layered Architecture)** estrita, garantindo alta coesão, baixo acoplamento, separação clara entre o contrato HTTP (`DTOs`) e o modelo de persistência (`Entities`), além de isolamento completo de integrações externas.

```text
┌─────────────────────────────────────────────────────────────────────────┐
│                          CONSUMIDORES / FRONT-END                       │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ HTTPS / JSON (Bearer JWT)
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│ 1. RESOURCE LAYER (REST / JAX-RS)                    [@RequestScoped]   │
│    • Validação de entrada (@Valid), Paginação, Controle de Acesso RBAC  │
│    • Tratamento Global de Exceções (ExceptionsMapperGlobal)             │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ DTOs (Request / Response)
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│ 2. SERVICE LAYER (Regras de Negócio)             [@ApplicationScoped]   │
│    • Transações (@Transactional), Cache (@CacheResult/@CacheInvalidate) │
│    • Conversão segura via Mappers dedicados e Validações de Domínio     │
└──────────────────┬───────────────────────────────────┬──────────────────┘
                   │ Entidades JPA                     │ MicroProfile REST Client
                   ▼                                   ▼
┌─────────────────────────────────────┐ ┌─────────────────────────────────┐
│ 3. REPOSITORY LAYER (Panache ORM)   │ │ 4. EXTERNAL CLIENTS             │
│    • Consultas otimizadas e         │ │    • ViaCepClient (CEP)         │
│      Paginação (PanacheQuery/Page)  │ │    • GoogleMapsClient (Rotas)   │
└──────────────────┬──────────────────┘ └──────────────┬──────────────────┘
                   │ JDBC                              │ HTTPS
                   ▼                                   ▼
┌─────────────────────────────────────┐ ┌─────────────────────────────────┐
│ 5. ENTITY / DATABASE LAYER          │ │ APIs Externas                   │
│    • Oracle Database                │ │    • ViaCEP / Google Maps API   │
└─────────────────────────────────────┘ └─────────────────────────────────┘
```

### 🏆 Destaques Técnicos e Engenharia de Software

#### 💉 Injeção de Dependências e Escopos CDI (`Quarkus ArC`)
- **`@RequestScoped` na camada `Resource`**: Garante isolamento do contexto de requisição HTTP.
- **`@ApplicationScoped` nas camadas `Service` e `Repository`**: Instâncias singletons gerenciadas pelo container CDI com proxies otimizados em tempo de compilação pelo Quarkus, reduzindo overhead de memória e tempo de startup.

#### 🛡️ Tratamento Global de Exceções (`ExceptionsMapperGlobal`)
Toda a árvore de exceções da API é interceptada pelo provider global `ExceptionsMapperGlobal` (`ExceptionMapper<Exception>`), assegurando que nenhuma *stacktrace* ou detalhe sensível de infraestrutura vaze para o cliente e padronizando todas as respostas no record imutável `ErroResponse`:

```json
{
  "statusCode": 404,
  "mensagem": "Beneficiário não encontrado.",
  "timestamp": "2026-10-17T10:30:00"
}
```

| Exceção Capturada | Código HTTP | Semântica de Negócio |
| :--- | :---: | :--- |
| `RequisicaoInvalidaException` / `ConstraintViolationException` | **`400 Bad Request`** | Payload malformado, parâmetros ausentes ou violação de Bean Validation (`@Valid`). |
| `NaoEncontradoException` | **`404 Not Found`** | Recurso solicitado (CPF, ID, CEP ou cidade) inexistente na base ou serviço externo. |
| `RegraNegocioException` | **`409 Conflict`** | Violação de estado ou conflito de integridade negocial (ex.: duplicidade de registro). |
| `ValidacaoException` | **`422 Unprocessable Entity`** | Dados sintaticamente corretos, porém inválidos perante regras de domínio (ex.: CPF inválido). |
| `Exception` (Fallback Seguro) | **`500 Internal Server Error`** | Erro inesperado registrado via `JBoss Logger` interno com mensagem genérica ao consumidor. |

#### 🔐 Segurança Robusta (SmallRye JWT RSA + Bcrypt + RBAC)
- **Criptografia Assimétrica RSA**: Emissão e validação de tokens JWT assinados com chave privada RSA (`privateKey.pem`) e verificados via chave pública (`publicKey.pem`).
- **Fluxo de Access Token & Refresh Token**: Suporte nativo à renovação de sessão sem reenvio de credenciais (`/auth/tokenAcesso` e `/auth/refreshToken`).
- **Hashing de Senhas com Bcrypt**: Senhas de colaboradores jamais trafegam ou são persistidas em texto puro, utilizando `BcryptUtil.bcryptHash` (Elytron Security) e omissão total nos DTOs de saída.
- **Controle de Acesso Granular (RBAC)**: Proteção declarativa em nível de endpoint com `@RolesAllowed({"ADMIN", "COLABORADOR"})` para rotas operacionais, `@RolesAllowed("ADMIN")` para operações críticas/exclusões/exportações sensíveis e `@PermitAll` para rotas públicas (abertura de pedidos de ajuda, catálogo de programas sociais e especialidades).

#### ⚡ Cache Inteligente em Memória (`Quarkus Cache` / Caffeine)
- Consultas paginadas de alta frequência em `BeneficiarioService`, `DentistaService`, `AtendimentoService`, `PedidoAjudaService` e `ColaboradorService` são aceleradas com `@CacheResult` (caches `beneficiarios`, `dentistas`, `atendimento`, `pedido-ajuda`, `colaborador`).
- **Consistência Automática (Write-Through Invalidation)**: Todas as operações transacionais de escrita (`criar`, `atualizar`, `excluir`, `processarPedido`, `encerrarAtendimento`) possuem `@CacheInvalidateAll` acoplado, evitando *stale reads* (leitura de dados defasados).

#### 📄 Paginação Padronizada (`PageResponse<T>`)
- Todas as listagens principais operam diretamente na rota raiz do recurso (`GET /recurso?pagina=0&size=20`), integrando `PanacheQuery<E>` e `Page.of(pagina, tamanho)` ao envelope genérico `PageResponse<T>`:

```json
{
  "conteudo": [ ... ],
  "pagina": 0,
  "tamanho": 20,
  "totalElementos": 142,
  "totalPaginas": 8
}
```
> *Nota de Retrocompatibilidade*: As rotas não paginadas foram preservadas sob o sufixo `/legado` com `@Operation(hidden = true)` para não quebrar integrações existentes e manter os endpoints de exportação CSV íntegros.

#### 🌐 Integrações Externas Desacopladas (`MicroProfile REST Client`)
- **ViaCEP (`@RegisterRestClient(configKey = "via-cep-api")`)**: Enriquecimento e validação automática de logradouros, bairros, cidades e UFs a partir do CEP (`GET /{cep}/json/`).
- **Google Maps Routes API (`@RegisterRestClient(configKey = "google-maps-api")`)**: Consumo do endpoint `POST /distanceMatrix/v2:computeRouteMatrix` utilizando headers `X-Goog-Api-Key` e `X-Goog-FieldMask` para cálculo preciso de distância viária em metros.

---

## 3. Domínio e Match Geográfico

### 🧩 Entidades Principais do Domínio

| Entidade | Responsabilidade no Domínio |
| :--- | :--- |
| **`Beneficiario`** | Pessoa vulnerável aprovada na triagem e vinculada a um Programa Social. |
| **`PedidoAjuda`** | Solicitação inicial de auxílio odontológico com controle de `StatusPedido` (`ANALISE`, `APROVADO`, `REJEITADO`). |
| **`Dentista`** | Profissional voluntário cadastrado com CRO, especialidades, programas sociais e flag de disponibilidade. |
| **`Atendimento`** | Registro clínico que conecta um `Beneficiario` a um `Dentista`, contendo datas de abertura/encerramento e histórico. |
| **`Colaborador`** | Usuário interno da ONG autenticado via JWT, classificado por perfil (`ADMIN` ou `COLABORADOR`). |
| **`Endereco`** | Entidade georreferenciada (`RESIDENCIAL` ou `PROFISSIONAL`) validada junto ao ViaCEP. |
| **`ProgramaSocial`** | Programas mantidos pela ONG (*Dentista do Bem* e *Apolônias do Bem*). |
| **`Especialidade`** | Catálogo de especialidades odontológicas atribuíveis aos dentistas voluntários. |

### 📍 Algoritmo de Match Geográfico por Proximidade

Durante a abertura de um atendimento (`AtendimentoMatchService`), caso nenhum dentista específico seja designado manualmente, a API executa o **Match Geográfico Automático**:

```text
[Beneficiário] ──► Endereço Residencial (Origem)
                         │
                         ▼
             [AtendimentoMatchService] ◄── Lista Dentistas Disponíveis (Endereços Profissionais)
                         │
                         ▼
               [GoogleMapsService] ──────► Google Maps Routes API (computeRouteMatrix)
                         │
                         ▼
       Seleção do Dentista com Menor Distância (metros) ──► Criação do Atendimento
```

---

## 4. Tabela de Endpoints Atualizada

> Documentação interativa disponível via **Swagger UI** em: `http://localhost:8080/q/swagger-ui/`

### 🔑 Autenticação (`/auth`)

| Método | Endpoint | Query Params / Payload | Acesso | Descrição |
| :---: | :--- | :--- | :---: | :--- |
| `POST` | `/auth/tokenAcesso` | `LoginRequest` (JSON) | `@PermitAll` | Autentica o colaborador e emite o par `accessToken` + `refreshToken` (RSA JWT). |
| `POST` | `/auth/refreshToken` | `RefreshTokenRequest` (JSON) | `@PermitAll` | Valida o Refresh Token e emite um novo par de tokens JWT. |

### 👤 Beneficiários (`/beneficiario`)

| Método | Endpoint | Query Params | Acesso | Descrição |
| :---: | :--- | :--- | :---: | :--- |
| `GET` | `/beneficiario` | `pagina=0`, `size=20` | `ADMIN`, `COLABORADOR` | Lista beneficiários de forma paginada (`PageResponse`) com cache. |
| `POST` | `/beneficiario` | — | `ADMIN`, `COLABORADOR` | Cadastra um beneficiário a partir de um pedido de ajuda `APROVADO`. |
| `GET` | `/beneficiario/{cpf}` | — | `ADMIN`, `COLABORADOR` | Busca um beneficiário específico pelo CPF. |
| `GET` | `/beneficiario/cidade/{cidade}` | — | `ADMIN`, `COLABORADOR` | Filtra beneficiários por cidade. |
| `GET` | `/beneficiario/programa/{idProgramaSocial}` | — | `ADMIN`, `COLABORADOR` | Filtra beneficiários vinculados a um programa social. |
| `GET` | `/beneficiario/exportarCsv` | — | `ADMIN` | Exporta a base completa de beneficiários em arquivo `.csv` (`UTF-8`). |
| `PUT` | `/beneficiario/{cpf}` | — | `ADMIN`, `COLABORADOR` | Atualiza contato e endereço residencial do beneficiário. |
| `DELETE` | `/beneficiario/{cpf}` | — | `ADMIN` | Remove um beneficiário pelo CPF *(oculto no Swagger)*. |

### 🦷 Dentistas Voluntários (`/dentista`)

| Método | Endpoint | Query Params | Acesso | Descrição |
| :---: | :--- | :--- | :---: | :--- |
| `GET` | `/dentista` | `pagina=0`, `size=20` | `ADMIN`, `COLABORADOR` | Lista dentistas voluntários de forma paginada (`PageResponse`) com cache. |
| `POST` | `/dentista` | — | `ADMIN`, `COLABORADOR` | Cadastra um novo dentista voluntário, endereço profissional e especialidades. |
| `GET` | `/dentista/disponiveis` | — | `ADMIN`, `COLABORADOR` | Lista todos os dentistas com disponibilidade ativa para novos atendimentos. |
| `GET` | `/dentista/{cpf}` | — | `ADMIN`, `COLABORADOR` | Busca um dentista voluntário pelo CPF. |
| `GET` | `/dentista/cidade/{cidade}` | — | `ADMIN`, `COLABORADOR` | Lista dentistas voluntários que atendem em determinada cidade. |
| `GET` | `/dentista/exportarCsv` | — | `ADMIN`, `COLABORADOR` | Exporta a relação de dentistas voluntários em arquivo `.csv` (`UTF-8`). |
| `PUT` | `/dentista/{cpf}` | — | `ADMIN`, `COLABORADOR` | Atualiza dados cadastrais, disponibilidade e endereço do dentista. |
| `DELETE` | `/dentista/{cpf}` | — | `ADMIN` | Exclui o registro de um dentista pelo CPF *(oculto no Swagger)*. |

### 🏥 Atendimentos Odontológicos (`/atendimento`)

| Método | Endpoint | Query Params | Acesso | Descrição |
| :---: | :--- | :--- | :---: | :--- |
| `GET` | `/atendimento/` | `pagina=0`, `size=20` | `ADMIN`, `COLABORADOR` | Lista atendimentos odontológicos paginados (`PageResponse`) com cache. |
| `POST` | `/atendimento/` | — | `ADMIN`, `COLABORADOR` | Cria um atendimento (acionando o Match Geográfico quando aplicável). |
| `GET` | `/atendimento/{cpf}` | — | `ADMIN`, `COLABORADOR` | Consulta atendimento vinculado ao CPF informado. |
| `GET` | `/atendimento/exportarCsv` | — | `ADMIN` | Exporta o relatório completo de atendimentos em arquivo `.csv` (`UTF-8`). |
| `PUT` | `/atendimento/{cpf}` | — | `ADMIN`, `COLABORADOR` | Atualiza/finaliza o atendimento odontológico registrando observações e encerramento. |
| `DELETE` | `/atendimento/{id}` | — | `ADMIN` | Exclui um atendimento pelo ID *(oculto no Swagger)*. |

### 🤝 Pedidos de Ajuda (`/pedido-ajuda`)

| Método | Endpoint | Query Params | Acesso | Descrição |
| :---: | :--- | :--- | :---: | :--- |
| `POST` | `/pedido-ajuda` | — | `@PermitAll` | Registra publicamente um novo pedido de ajuda submetido à triagem. |
| `GET` | `/pedido-ajuda` | `pagina=0`, `size=20` | `ADMIN`, `COLABORADOR` | Lista pedidos de ajuda de forma paginada (`PageResponse`) com cache. |
| `GET` | `/pedido-ajuda/data/{data}` | — | `ADMIN`, `COLABORADOR` | Filtra pedidos de ajuda registrados em uma data específica (`YYYY-MM-DD`). |
| `PUT` | `/pedido-ajuda/{id}` | — | `ADMIN`, `COLABORADOR` | Processa a triagem do pedido (aprovação/rejeição e vínculo de colaborador). |
| `DELETE` | `/pedido-ajuda/{id}` | — | `ADMIN` | Remove um pedido de ajuda pelo ID *(oculto no Swagger)*. |

### 👨‍💼 Colaboradores (`/colaborador`)

| Método | Endpoint | Query Params | Acesso | Descrição |
| :---: | :--- | :--- | :---: | :--- |
| `GET` | `/colaborador` | `pagina=0`, `size=20` | `ADMIN`, `COLABORADOR` | Lista colaboradores de forma paginada (`PageResponse`) com cache. |
| `POST` | `/colaborador` | — | `ADMIN` | Cadastra novo colaborador com senha criptografada via Bcrypt. |
| `GET` | `/colaborador/{cpf}` | — | `ADMIN`, `COLABORADOR` | Busca dados de um colaborador específico pelo CPF (sem expor senha). |
| `PUT` | `/colaborador/{cpf}` | — | `ADMIN` | Atualiza o e-mail e informações do colaborador. |
| `DELETE` | `/colaborador/{cpf}` | — | `ADMIN` | Exclui um colaborador pelo CPF. |

### 🏠 Endereços, Especialidades e Programas Sociais

| Método | Endpoint | Acesso | Descrição |
| :---: | :--- | :---: | :--- |
| `POST` | `/endereco` | `ADMIN`, `COLABORADOR` | Cadastra novo endereço enriquecido via ViaCEP. |
| `GET` | `/endereco` | `ADMIN`, `COLABORADOR` | Lista todos os endereços cadastrados. |
| `GET` | `/endereco/{cidade}` | `ADMIN`, `COLABORADOR` | Filtra endereços por cidade. |
| `GET` | `/endereco/id/{id}` | `ADMIN`, `COLABORADOR` | Busca endereço específico por ID. |
| `GET` | `/endereco/viacep/{cep}` | `ADMIN`, `COLABORADOR` | Consulta dados de endereço diretamente na API do ViaCEP. |
| `PUT` | `/endereco/{id}` | `ADMIN`, `COLABORADOR` | Atualiza os dados de um endereço existente. |
| `DELETE` | `/endereco/{id}` | `ADMIN` | Exclui um endereço pelo ID. |
| `GET` | `/especialidades` | `@PermitAll` | Lista todas as especialidades odontológicas disponíveis. |
| `GET` | `/especialidades/{id}` | `@PermitAll` | Busca uma especialidade odontológica pelo ID. |
| `GET` | `/programas-sociais` | `@PermitAll` | Lista os programas sociais da ONG (*Dentista do Bem* e *Apolônias do Bem*). |
| `GET` | `/programas-sociais/{id}` | `@PermitAll` | Busca um programa social específico pelo ID. |

---

## 5. Testes e Qualidade de Software

A confiabilidade da **Raiz do Bem API** é assegurada por uma suíte abrangente de **285 testes unitários automatizados**, construída com **JUnit 5**, **Mockito 5.18**, **AssertJ 3.27** e **JaCoCo**, atingindo **100% de aproveitamento (0 falhas, 0 erros — Build Verde)**.

```text
src/test/java/br/com/raizdobem/api/
├── exception/   ──► Testes do ExceptionsMapperGlobal (HTTP 400, 404, 409, 422 e 500)
├── mapper/      ──► Testes de conversão fiel Entity <-> DTO e omissão de dados sensíveis
├── resource/    ──► Testes de contrato HTTP, paginação, exportação CSV e status codes
├── service/     ──► Testes de regras de negócio, autenticação JWT/Refresh, Cache e Match Geográfico
└── util/        ──► Testes de validação algorítmica de CPF e geração padronizada de arquivos CSV
```

### 🔍 Escopo da Cobertura de Testes
- **Camada de Serviços (`service/`)**: Validação exaustiva de fluxos felizes e cenários de exceção em `BeneficiarioServiceTest`, `DentistaServiceTest`, `AtendimentoServiceTest`, `AtendimentoMatchServiceTest`, `PedidoAjudaServiceTest`, `ColaboradorServiceTest`, `EnderecoServiceTest`, `EspecialidadeServiceTest`, `ProgramaServiceTest`, `LoginServiceTest` e `GoogleMapsServiceTest`.
- **Isolamento de Integrações com Mocks**: Simulação completa do `GoogleMapsClient` e `ViaCepClient` via Mockito, testando seleção por menor distância em metros, tratamento de rotas inválidas e indisponibilidade de serviços externos sem dependência de rede.
- **Camada REST (`resource/`)**: Verificação de todos os endpoints paginados (`PageResponse`), rotas legadas, cabeçalhos `Content-Disposition` de download CSV e respostas HTTP em todas as classes `*ResourceTest`.
- **Conversores e Utilitários (`mapper/`, `util/`, `exception/`)**: Garantia de integridade dos mapeadores estáticos, validação de dígitos verificadores de CPF (`CpfValidatorUtilTest`), formatação CSV (`CsvUtilTest`) e mapeamento de erros (`ExceptionsMapperGlobalTest`).

### ▶️ Executando a Suíte de Testes e Relatório JaCoCo

```bash
# Linux / macOS
./mvnw clean test

# Windows (PowerShell)
.\mvnw.cmd clean test
```
O relatório de cobertura do **JaCoCo** é gerado automaticamente em `target/site/jacoco/index.html`.

---

## 6. Deploy, CI/CD e Execução Local

### 🔧 Pré-requisitos e Variáveis de Ambiente

Certifique-se de possuir o **Java 21 (JDK 21)** instalado. Configure as seguintes variáveis de ambiente antes de iniciar a aplicação:

| Variável | Descrição |
| :--- | :--- |
| `DB_URL` | String JDBC de conexão com o banco Oracle (ex.: `jdbc:oracle:thin:@//host:1521/service`). |
| `DB_USUARIO` | Usuário de acesso ao banco de dados Oracle. |
| `DB_SENHA` | Senha de acesso ao banco de dados Oracle. |
| `GOOGLE_API_KEY` | Chave de API do Google Cloud com a *Routes API* habilitada. |

**No Linux / macOS:**
```bash
export DB_URL="jdbc:oracle:thin:@//localhost:1521/XEPDB1"
export DB_USUARIO="<seu_usuario>"
export DB_SENHA="<sua_senha>"
export GOOGLE_API_KEY="<sua_google_api_key>"
```

**No Windows (PowerShell):**
```powershell
$env:DB_URL="jdbc:oracle:thin:@//localhost:1521/XEPDB1"
$env:DB_USUARIO="<seu_usuario>"
$env:DB_SENHA="<sua_senha>"
$env:GOOGLE_API_KEY="<sua_google_api_key>"
```

---

### 💻 Execução Local (Modo Dev com Live Reload)

O projeto inclui o **Maven Wrapper** (`mvnw` / `mvnw.cmd`), dispensando instalação manual do Maven:

```bash
# Linux / macOS
./mvnw quarkus:dev

# Windows (PowerShell)
.\mvnw.cmd quarkus:dev
```
- **API Base URL**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/q/swagger-ui/`
- **Dev UI do Quarkus**: `http://localhost:8080/q/dev-ui/`

---

### 🐳 Build e Containerização com Docker (JVM & GraalVM Native)

O diretório `src/main/docker/` disponibiliza Dockerfiles otimizados em múltiplas camadas (`fast-jar` e binário nativo):

#### 1. Build JVM (Recomendado — `Dockerfile.jvm` com Red Hat UBI 9 OpenJDK 21)
```bash
# 1. Empacotar a aplicação (gera target/quarkus-app/)
./mvnw clean package -DskipTests

# 2. Construir a imagem Docker multi-layer
docker build -f src/main/docker/Dockerfile.jvm -t raiz-do-bem-api:jvm .

# 3. Executar o container
docker run -i --rm -p 8080:8080 \
  -e DB_URL="$DB_URL" \
  -e DB_USUARIO="$DB_USUARIO" \
  -e DB_SENHA="$DB_SENHA" \
  -e GOOGLE_API_KEY="$GOOGLE_API_KEY" \
  raiz-do-bem-api:jvm
```

#### 2. Build Nativo GraalVM (`Dockerfile.native` / `Dockerfile.native-micro`)
```bash
# 1. Compilar executável nativo via container builder
./mvnw clean package -Dnative -Dquarkus.native.container-build=true

# 2. Construir a imagem nativa enxuta
docker build -f src/main/docker/Dockerfile.native -t raiz-do-bem-api:native .

# 3. Executar o container nativo
docker run -i --rm -p 8080:8080 raiz-do-bem-api:native
```

---

### ☁️ Pipeline de CI/CD (Azure Pipelines & Azure Web App)

O projeto conta com entrega contínua configurada em [`azure-pipelines.yml`](azure-pipelines.yml), monitorando os branches `main` e `develop`:

```text
[Git Push: main / develop]
            │
            ▼
  [Azure Pipelines (Ubuntu Latest)]
            │
            ├──► 1. Setup JDK 21 + Cache de dependências Maven
            ├──► 2. Build & Testes Unitários (./mvnw clean package)
            ├──► 3. Empacotamento Fast-Jar (target/quarkus-app/)
            └──► 4. Deploy Automatizado via AzureWebApp@1
                        │
                        ▼
        [Azure App Service: raizdobem-backend (Linux / Java 21)]
```

---

## 📋 Metadados do Projeto

| Atributo | Especificação |
| :--- | :--- |
| **Nome do Projeto** | Raiz do Bem API |
| **Contexto** | Challenge FIAP — Parceria ONG Turma do Bem |
| **Group ID / Artifact ID** | `br.com.raizdobem` / `raiz-do-bem-api` |
| **Versão** | `1.0.0-SNAPSHOT` |
| **Runtime & Framework** | Java 21 LTS + Quarkus 3.34.5 |
| **Persistência & Cache** | Hibernate ORM with Panache (Oracle) + Quarkus Cache (Caffeine) |
| **Segurança** | SmallRye JWT (RSA-256) + Elytron Bcrypt + RBAC (`@RolesAllowed`) |
| **Qualidade** | 285 Testes Unitários (JUnit 5, Mockito, AssertJ, JaCoCo) |
| **Cloud & CI/CD** | Docker + Azure Pipelines + Azure Web App |
