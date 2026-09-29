# 🌱 Raiz do Bem — API

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.34.5-4695EB?logo=quarkus&logoColor=white)](https://quarkus.io/)
[![Oracle](https://img.shields.io/badge/Oracle-Database-F80000?logo=oracle&logoColor=white)](https://www.oracle.com/database/)
[![JWT](https://img.shields.io/badge/Auth-JWT-000000?logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger-85EA2D?logo=swagger&logoColor=black)](https://swagger.io/specification/)

> API REST desenvolvida em **Java 21 + Quarkus** para suporte à plataforma **Raiz do Bem**, contemplando gestão de beneficiários, pedidos de ajuda, atendimentos odontológicos, dentistas, colaboradores, endereços e programas sociais.

---

## 📌 Sumário

- [Visão Geral](#-visão-geral)
- [Proposta de Valor](#-proposta-de-valor)
- [Principais Funcionalidades](#-principais-funcionalidades)
- [Arquitetura](#-arquitetura)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Domínio da Aplicação](#-domínio-da-aplicação)
- [Match Geográfico](#-match-geográfico)
- [Integrações Externas](#-integrações-externas)
- [Autenticação e Autorização](#-autenticação-e-autorização)
- [Endpoints](#-endpoints)
- [Formato de Erros](#-formato-de-erros)
- [Tecnologias](#-tecnologias)
- [Testes](#-testes)
- [Execução Local](#-execução-local)
- [Variáveis de Ambiente](#-variáveis-de-ambiente)
- [Docker](#-docker)
- [CI/CD](#-cicd)
- [Status e Débitos Conhecidos](#-status-e-débitos-conhecidos)
- [Documentação](#-documentação)
- [Evolução](#-evolução)

---

## 🌱 Visão Geral

O **Raiz do Bem** é uma plataforma voltada à gestão de ações sociais e atendimentos odontológicos.

A API concentra as principais operações de negócio relacionadas a:

- Beneficiários;
- Pedidos de ajuda;
- Atendimentos odontológicos;
- Dentistas;
- Colaboradores;
- Endereços;
- Especialidades;
- Programas sociais.

Além das operações tradicionais de cadastro e consulta, o backend possui uma regra de **match geográfico** para auxiliar na seleção de dentistas considerando a distância entre o beneficiário e os profissionais disponíveis.

A aplicação foi estruturada utilizando **Quarkus**, **Hibernate ORM/Panache**, **Oracle Database**, **JWT**, **MicroProfile REST Client** e **OpenAPI/Swagger**.

---

## 🎯 Proposta de Valor

A API fornece uma camada centralizada para gerenciamento dos dados e regras necessárias à operação da plataforma.

Entre os principais fluxos estão:

1. Cadastro e gerenciamento de beneficiários;
2. Cadastro e gerenciamento de dentistas;
3. Registro de pedidos de ajuda;
4. Gerenciamento de atendimentos odontológicos;
5. Associação entre beneficiários e dentistas;
6. Consulta de endereços;
7. Integração com o ViaCEP;
8. Cálculo de distância utilizando a Google Maps Routes API;
9. Autenticação baseada em JWT;
10. Controle de acesso baseado em papéis;
11. Exportação de dados em CSV;
12. Paginação de consultas;
13. Padronização global das respostas de erro.

---

## ⚙️ Principais Funcionalidades

### 👤 Beneficiários

- Cadastro;
- Consulta;
- Consulta por CPF;
- Consulta por cidade;
- Consulta por programa social;
- Atualização;
- Remoção;
- Paginação;
- Exportação CSV.

### 🦷 Dentistas

- Cadastro;
- Consulta;
- Consulta por CPF;
- Consulta por cidade;
- Consulta de profissionais disponíveis;
- Atualização;
- Remoção;
- Exportação CSV.

### 🏥 Atendimentos

- Cadastro;
- Consulta;
- Consulta por CPF;
- Paginação;
- Atualização;
- Exportação CSV;
- Associação com dentistas e beneficiários.

### 🤝 Pedidos de Ajuda

- Criação;
- Consulta;
- Consulta por data;
- Atualização;
- Remoção;
- Controle de status.

### 🏠 Endereços

- Cadastro;
- Consulta;
- Consulta por cidade;
- Consulta por ID;
- Busca de endereço por CEP;
- Atualização;
- Remoção.

### 🔐 Segurança

- Autenticação por JWT;
- Bearer Token;
- Controle de acesso com `@RolesAllowed`;
- Perfis `ADMIN` e `COLABORADOR`;
- Endpoints públicos utilizando `@PermitAll`.

---

# 🏗️ Arquitetura

A aplicação segue uma arquitetura organizada em camadas, separando responsabilidades entre recursos HTTP, regras de negócio, persistência e entidades.

```text
                         ┌─────────────────────┐
                         │      Front-end      │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │       Resource      │
                         │     REST / HTTP     │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │       Service       │
                         │   Regras de negócio │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │     Repository      │
                         │ Persistência / ORM  │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │       Entity        │
                         │     Domínio/JPA     │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │   Oracle Database   │
                         └─────────────────────┘

```

## Fluxo complementar de integrações

```text

Resource
   │
   ▼
Service
   │
   ├──────────────► Repository ───────────► Oracle
   │
   ├──────────────► ViaCepClient ─────────► ViaCEP
   │
   └──────────────► GoogleMapsService
                          │
                          ▼
                    GoogleMapsClient
                          │
                          ▼
                Google Maps Routes API

```
# 📂 Estrutura do Projeto

```text

raiz-do-bem-api/
├── .github/
│   ├── copilot-instructions.md
│   ├── prompts/
│   └── workflows/
│
├── .mvn/
│   └── wrapper/
│
├── docs/
│   ├── banco/
│   └── documentacao-java/
│
├── src/
│   ├── main/
│   │   ├── docker/
│   │   │   ├── Dockerfile.jvm
│   │   │   ├── Dockerfile.legacy-jar
│   │   │   ├── Dockerfile.native
│   │   │   └── Dockerfile.native-micro
│   │   │
│   │   ├── java/
│   │   │   └── br/com/raizdobem/api/
│   │   │       ├── client/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── exception/
│   │   │       ├── mapper/
│   │   │       ├── repository/
│   │   │       ├── resource/
│   │   │       ├── service/
│   │   │       └── util/
│   │   │
│   │   └── resources/
│   │
│   └── test/
│       └── java/
│           └── br/com/raizdobem/api/
│               ├── exception/
│               ├── mapper/
│               ├── resource/
│               ├── service/
│               └── util/
│
├── azure-pipelines.yml
├── mvnw
├── mvnw.cmd
├── pom.xml
└── .dockerignore

```

## 🧩 Domínio da Aplicação

As principais entidades presentes no domínio são:

| Entidade         | Responsabilidade                                         |
| ---------------- | -------------------------------------------------------- |
| `Beneficiario`   | Representa a pessoa atendida pela plataforma             |
| `PedidoAjuda`    | Representa uma solicitação de ajuda                      |
| `ProgramaSocial` | Representa programas sociais disponíveis                 |
| `Atendimento`    | Representa um atendimento odontológico                   |
| `Dentista`       | Representa profissionais responsáveis pelos atendimentos |
| `Especialidade`  | Representa especialidades odontológicas                  |
| `Colaborador`    | Representa usuários colaboradores da plataforma          |
| `Endereco`       | Representa endereços associados às entidades             |

Também existem enums relacionados ao domínio, incluindo:

```
Sexo
StatusPedido
TipoEndereco
```

## 📍 Match Geográfico

Um dos fluxos de negócio implementados é o match geográfico entre beneficiários e dentistas.

O processo utiliza os endereços envolvidos no atendimento e consulta a Google Maps Routes API para obter informações de distância.

### Fluxo
```
Beneficiário
     │
     ▼
Endereço do beneficiário
     │
     ▼
AtendimentoMatchService
     │
     ▼
GoogleMapsService
     │
     ▼
GoogleMapsClient
     │
     ▼
Google Maps Routes API
     │
     ▼
Distâncias calculadas
     │
     ▼
Seleção do dentista
```

O `GoogleMapsService` recebe os dados necessários para cálculo das rotas e trabalha com a distância retornada pela API externa.

Os testes do serviço utilizam mocks para a integração externa e verificam a seleção do dentista considerando a menor distância calculada.

## 🌐 Integrações Externas
### 📮 ViaCEP

A aplicação utiliza o ViaCEP para consulta de informações de endereço a partir do CEP.

A integração é implementada utilizando MicroProfile REST Client.

O cliente é registrado utilizando:
```java
@RegisterRestClient(configKey = "via-cep-api")
```
A consulta utiliza o endpoint:
```
GET /{cep}/json/
```
O serviço de endereço utiliza o cliente para obter informações relacionadas ao CEP informado.

### 🗺️ Google Maps Routes API

A aplicação também possui integração com a Google Maps Routes API para cálculo de distâncias.

O cliente utiliza:
```java
@RegisterRestClient(configKey = "google-maps-api")
```
A operação utilizada é:
```
POST /distanceMatrix/v2:computeRouteMatrix
```
A requisição utiliza informações como:

- Origem;
- Destino;
- Modo de transporte;
- Preferência de roteamento;
- API Key;
- Field Mask.

Os headers utilizados incluem:
```
X-Goog-Api-Key
X-Goog-FieldMask
```
O retorno contém informações como:

- Índice da origem;
- Índice do destino;
- Distância em metros;
- Condição da rota.

---
## 🔐 Autenticação e Autorização

A API utiliza JWT Bearer Token para autenticação.

O contrato OpenAPI está configurado utilizando o esquema:
```
HTTP Bearer
Bearer Format: JWT
```
🔑 Obtenção do Token
```http
POST /auth/tokenAcesso
```
Este endpoint utiliza:
```java
@PermitAll
```
Após autenticação, o token JWT deve ser enviado nas requisições protegidas utilizando:
```http
Authorization: Bearer <token>
```

---

## 👥 Perfis

A aplicação trabalha principalmente com os seguintes papéis:
| Role          | Descrição             |
| ------------- | --------------------- |
| `ADMIN`       | Acesso administrativo |
| `COLABORADOR` | Acesso operacional    |

Os recursos utilizam ```@RolesAllowed``` para restringir operações.

Endpoints públicos utilizam ```@PermitAll```.

---

## 📡 Endpoints

### 🔑 Autenticação

| Método | Endpoint            | Acesso      |
| ------ | ------------------- | ----------- |
| `POST` | `/auth/tokenAcesso` | `PermitAll` |

### 👤 Beneficiário

| Método   | Endpoint                                    | Acesso                 |
| -------- | ------------------------------------------- | ---------------------- |
| `GET`    | `/beneficiario`                             | `ADMIN`, `COLABORADOR` |
| `GET`    | `/beneficiario/paginacao`                   | `ADMIN`, `COLABORADOR` |
| `POST`   | `/beneficiario`                             | `ADMIN`, `COLABORADOR` |
| `GET`    | `/beneficiario/{cpf}`                       | `ADMIN`, `COLABORADOR` |
| `GET`    | `/beneficiario/cidade/{cidade}`             | `ADMIN`, `COLABORADOR` |
| `GET`    | `/beneficiario/programa/{idProgramaSocial}` | `ADMIN`, `COLABORADOR` |
| `GET`    | `/beneficiario/exportarCsv`                 | `ADMIN`                |
| `PUT`    | `/beneficiario/{cpf}`                       | `ADMIN`, `COLABORADOR` |
| `DELETE` | `/beneficiario/{cpf}`                       | `ADMIN`                |

O endpoint de `DELETE` está implementado, porém utiliza `@Hidden` e, portanto, não é exibido na documentação Swagger.

### 🦷 Dentista

| Método   | Endpoint                    | Acesso                 |
| -------- | --------------------------- | ---------------------- |
| `POST`   | `/dentista`                 | `ADMIN`, `COLABORADOR` |
| `GET`    | `/dentista`                 | `ADMIN`, `COLABORADOR` |
| `GET`    | `/dentista/disponiveis`     | `ADMIN`, `COLABORADOR` |
| `GET`    | `/dentista/{cpf}`           | `ADMIN`, `COLABORADOR` |
| `GET`    | `/dentista/cidade/{cidade}` | `ADMIN`, `COLABORADOR` |
| `GET`    | `/dentista/exportarCsv`     | `ADMIN`, `COLABORADOR` |
| `PUT`    | `/dentista/{cpf}`           | `ADMIN`, `COLABORADOR` |
| `DELETE` | `/dentista/{cpf}`           | `ADMIN`                |

O endpoint de `DELETE` está implementado, porém utiliza `@Hidden`.

### 🏥 Atendimento
| Método   | Endpoint                   | Acesso                 |
| -------- | -------------------------- | ---------------------- |
| `GET`    | `/atendimento/`            | `ADMIN`, `COLABORADOR` |
| `GET`    | `/atendimento/paginacao`   | `ADMIN`, `COLABORADOR` |
| `POST`   | `/atendimento/`            | `ADMIN`, `COLABORADOR` |
| `GET`    | `/atendimento/{cpf}`       | `ADMIN`, `COLABORADOR` |
| `GET`    | `/atendimento/exportarCsv` | `ADMIN`                |
| `PUT`    | `/atendimento/{cpf}`       | `ADMIN`, `COLABORADOR` |
| `DELETE` | `/atendimento/{id}`        | `ADMIN`                |

O endpoint de `DELETE` está implementado, porém utiliza `@Hidden`.

A paginação possui os seguintes valores padrão:
```
pagina = 0
size   = 20
```

### 🤝 Pedido de Ajuda

| Método   | Endpoint                    | Acesso                 |
| -------- | --------------------------- | ---------------------- |
| `GET`    | `/pedido-ajuda`             | `ADMIN`, `COLABORADOR` |
| `GET`    | `/pedido-ajuda/data/{data}` | `ADMIN`, `COLABORADOR` |
| `POST`   | `/pedido-ajuda`             | `PermitAll`            |
| `PUT`    | `/pedido-ajuda/{id}`        | `ADMIN`, `COLABORADOR` |
| `DELETE` | `/pedido-ajuda/{id}`        | `ADMIN`                |

O endpoint de `DELETE` utiliza `@Hidden`.

### 👨‍💼 Colaborador

| Método   | Endpoint             | Acesso                 |
| -------- | -------------------- | ---------------------- |
| `POST`   | `/colaborador`       | `ADMIN`                |
| `GET`    | `/colaborador`       | `ADMIN`, `COLABORADOR` |
| `GET`    | `/colaborador/{cpf}` | `ADMIN`, `COLABORADOR` |
| `PUT`    | `/colaborador/{cpf}` | `ADMIN`                |
| `DELETE` | `/colaborador/{cpf}` | `ADMIN`                |

### 🏠 Endereço
| Método   | Endpoint                 | Acesso                 |
| -------- | ------------------------ | ---------------------- |
| `POST`   | `/endereco`              | `ADMIN`, `COLABORADOR` |
| `GET`    | `/endereco`              | `ADMIN`, `COLABORADOR` |
| `GET`    | `/endereco/{cidade}`     | `ADMIN`, `COLABORADOR` |
| `GET`    | `/endereco/id/{id}`      | `ADMIN`, `COLABORADOR` |
| `GET`    | `/endereco/viacep/{cep}` | `ADMIN`, `COLABORADOR` |
| `PUT`    | `/endereco/{id}`         | `ADMIN`, `COLABORADOR` |
| `DELETE` | `/endereco/{id}`         | `ADMIN`                |

### 📚 Especialidades
| Método | Endpoint               | Acesso      |
| ------ | ---------------------- | ----------- |
| `GET`  | `/especialidades`      | `PermitAll` |
| `GET`  | `/especialidades/{id}` | `PermitAll` |

### 🌱 Programas Sociais
| Método | Endpoint                  | Acesso      |
| ------ | ------------------------- | ----------- |
| `GET`  | `/programas-sociais`      | `PermitAll` |
| `GET`  | `/programas-sociais/{id}` | `PermitAll` |

---

## ❌ Formato de Erros

As exceções da aplicação são centralizadas pelo:
```java
ExceptionsMapperGlobal
```
que implementa:
```java
ExceptionMapper<Exception>
```
As respostas utilizam o DTO:
```java
ErroResponse
```
com os campos:
```
statusCode
mensagem
timestamp
```
Exemplo
```json
{
  "statusCode": 404,
  "mensagem": "Recurso não encontrado.",
  "timestamp": "2026-09-28T23:59:59"
}
```

### Mapeamento

| Exceção                       |                        HTTP |
| ----------------------------- | --------------------------: |
| `NaoEncontradoException`      |             `404 Not Found` |
| `RequisicaoInvalidaException` |           `400 Bad Request` |
| `ValidacaoException`          |  `422 Unprocessable Entity` |
| `RegraNegocioException`       |              `409 Conflict` |
| Exceção genérica              | `500 Internal Server Error` |

Em erros `500`, a exceção original é registrada nos logs e a mensagem interna não é exposta diretamente ao consumidor da API.

---

## 🛠️ Tecnologias

| Tecnologia                           | Utilização                            |
| ------------------------------------ | ------------------------------------- |
| **Java 21**                          | Linguagem principal                   |
| **Quarkus 3.34.5**                   | Framework backend                     |
| **Maven**                            | Build e gerenciamento de dependências |
| **Hibernate ORM / Panache**          | Persistência                          |
| **Oracle Database**                  | Banco de dados                        |
| **RESTEasy Reactive / Quarkus REST** | API REST                              |
| **SmallRye JWT**                     | Autenticação JWT                      |
| **SmallRye OpenAPI**                 | Especificação OpenAPI                 |
| **Swagger UI**                       | Documentação interativa               |
| **MicroProfile REST Client**         | Integrações externas                  |
| **JUnit**                            | Testes                                |
| **Mockito**                          | Mocks                                 |
| **AssertJ**                          | Assertions                            |
| **REST Assured**                     | Testes de endpoints                   |
| **Docker**                           | Containerização                       |
| **Azure Pipelines**                  | CI/CD                                 |

---

## 🧪 Testes

O projeto possui uma estrutura de testes organizada por responsabilidade:

```
src/test/java/br/com/raizdobem/api/
├── exception/
├── mapper/
├── resource/
├── service/
└── util/
```

Entre os cenários cobertos estão:
- Resources REST;
- Services;
- Mappers;
- Exceptions;
- Validação de CPF;
- CSV;
- Autenticação;
- Match geográfico;
- Integração com Google Maps isolada por mocks.

### Match geográfico
O `GoogleMapsServiceTest` utiliza Mockito para simular a integração externa e validar a seleção do dentista considerando a menor distância.

O `AtendimentoMatchService` também possui testes utilizando mocks para isolar:

```
DentistaService
GoogleMapsService
```

Isso permite validar a regra de negócio sem depender diretamente da API externa durante os testes.

--- 

## 🚀 Execução Local
Pré-requisitos
- Java 21;
- Maven Wrapper;
- Oracle Database configurado;
- Credenciais/configurações das integrações externas utilizadas pela aplicação.

O projeto já possui o Maven Wrapper:
```
mvnw
mvnw.cmd
```

## ▶️ Executando a aplicação

### Linux / macOS
```bash
./mvnw quarkus:dev
```bash
```powershell
### Windows
```
.\mvnw.cmd quarkus:dev

O modo `quarkus:dev` permite executar a aplicação em ambiente de desenvolvimento.

---

## 📦 Build

```bash
./mvnw clean package
```

No Windows:

```powershell
.\mvnw.cmd clean package
```
O artefato Quarkus é gerado no diretório:
```
target/quarkus-app/
```

---

## 🔧 Variáveis de Ambiente

A configuração da aplicação utiliza variáveis para informações de conexão com infraestrutura e serviços externos.

As variáveis utilizadas pela configuração/documentação do projeto incluem:
```
DB_USUARIO
DB_SENHA
DB_URL
GOOGLE_API_KEY
```

Exemplo
```bash
export DB_USUARIO="<usuario>"
export DB_SENHA="<senha>"
export DB_URL="<url-de-conexao>"
export GOOGLE_API_KEY="<api-key>"
```
No Windows PowerShell:
```powershell
$env:DB_USUARIO="<usuario>"
$env:DB_SENHA="<senha>"
$env:DB_URL="<url-de-conexao>"
$env:GOOGLE_API_KEY="<api-key>"
```
Nunca versionar credenciais, senhas ou API Keys no repositório.

---

## 🐳 Docker

O projeto possui diferentes Dockerfiles em:

```
src/main/docker/
```

Disponíveis:
```
Dockerfile.jvm
Dockerfile.legacy-jar
Dockerfile.native
Dockerfile.native-micro
```

---

## ☕ JVM

O Dockerfile.jvm utiliza runtime baseado em:

```
registry.access.redhat.com/ubi9/openjdk-21-runtime
```

A aplicação é disponibilizada na porta:
```
8080
```

Exemplo de build:
```bash
./mvnw clean package
```

Build da imagem:
```bash
docker build \
  -f src/main/docker/Dockerfile.jvm \
  -t raiz-do-bem-api:jvm .
```

Execução:
```bash
docker run -p 8080:8080 raiz-do-bem-api:jvm
```

---
## ⚡ Native

O projeto também possui suporte a build nativo através do:
```
Dockerfile.native
```
e:
```
Dockerfile.native-micro
```
A versão native utiliza uma imagem base UBI minimal/micro, reduzindo o runtime necessário para execução da aplicação compilada nativamente.

---

## ☁️ CI/CD

O projeto possui pipeline Azure DevOps definido em:
```
azure-pipelines.yml
```
A pipeline contempla os branches:

```
main
develop
```

O fluxo inclui:
```
Git
 │
 ▼
Azure Pipelines
 │
 ├── Maven Build
 │
 ├── Geração do artefato
 │
 └── Publicação
        │
        ▼
Azure Web App
        │
        ▼
raizdobem-backend
```
O artefato principal utilizado no processo é:
```
target/quarkus-app/
```

---

## ⚠️ Status e Débitos Conhecidos

A documentação técnica interna do projeto registra algumas inconsistências e pontos de evolução.

Os itens abaixo representam pendências e comportamentos documentados no estado atual do código, e não alterações inferidas externamente.

### 📋 Inconsistências de domínio
```
Sexo
```

Existe divergência entre os valores utilizados no código e os valores descritos na documentação:
```
Código:       M / F / O
Documentação: MASCULINO / FEMININO
```
```
TipoEndereco
```

Também existe divergência entre:
```
Código:       PROFISSIONAL
Documentação: COMERCIAL
```

### 🪪 Validação de CPF

A validação atual verifica o formato/tamanho do CPF, mas não realiza a validação completa dos dígitos verificadores.

Como consequência, valores como:
```
00000000000
```
podem passar pela validação de formato.

### 🧱 Tipagem de AtendimentoResponse

O DTO AtendimentoResponse possui campos tipados como:
```java
Object
```
para determinadas informações relacionadas ao beneficiário, dentista e data de encerramento.

Isso reduz a segurança de tipos e pode aumentar riscos relacionados à serialização e manutenção do contrato da API.

### 🔐 Exposição de dados de colaboradores

A aplicação possui mapper que remove a senha da resposta.

Entretanto, a documentação técnica registra uma inconsistência em relação a recursos que podem trabalhar diretamente com a entidade.

O comportamento esperado é que respostas externas nunca exponham informações sensíveis, especialmente credenciais.

### 🗄️ Atualização em repositories

A documentação registra problemas nos métodos de atualização:

```java
PedidoAjudaRepository.atualizar
AtendimentoRepository.atualizar
```
No caso de PedidoAjudaRepository, existe uma consulta cujo resultado é descartado.

No AtendimentoRepository, o método de atualização encontra-se sem implementação efetiva.

### 🌱 Dados iniciais

As entidades:
```
ProgramaSocial
Especialidade
```
não utilizam geração IDENTITY para seus IDs.

Além disso, o import.sql não possui todos os dados funcionais necessários para determinadas operações.

Fluxos dependentes dessas entidades podem exigir registros previamente existentes no banco.

### 🦷 IDs fixos em DentistaService

O processo de criação de dentistas utiliza IDs fixos:
```
1
2
```
para determinados programas sociais.

O serviço também não realiza a verificação de existência desses registros antes de utilizá-los.

### 📄 Exportação CSV

Existem diferenças nos delimitadores utilizados pelos recursos de exportação.

Beneficiários e dentistas utilizam:
```
,
```
Enquanto atendimentos utilizam:
```
|
```
A padronização desse comportamento pode facilitar o consumo dos arquivos por sistemas externos.

### 🌐 CORS

A configuração atual utiliza:
```
*
```
para CORS.

Esse comportamento é conveniente durante desenvolvimento, mas a documentação técnica aponta a necessidade de uma política mais restritiva em ambientes produtivos.

### 📋 Regra de elegibilidade de pedidos

Existe uma regra de negócio documentada relacionada à elegibilidade de pedidos de ajuda.

Beneficiários do sexo masculino com idade superior a 18 anos podem ter o pedido automaticamente rejeitado.

A documentação também registra que, nesse cenário, o status pode ser persistido como:
```
REJEITADO
```
enquanto a operação HTTP ainda retorna:
```
201 Created
```
Esse comportamento merece revisão para manter consistência entre resultado da regra de negócio e semântica HTTP.

### 📦 Coleções vazias

Alguns services lançam:
```
NaoEncontradoException
```
quando uma consulta não retorna registros.

Isso pode resultar em:
```http
404 Not Found
```
para consultas de coleção vazias, em vez de uma resposta convencional:
```http
200 OK
```
com:
```json
[]
```
---

## 📚 Documentação

O projeto utiliza:

- OpenAPI;
- Swagger UI;
- Documentação técnica interna;
- Documentação relacionada ao banco;
- Prompts e instruções auxiliares no diretório .github.

A documentação OpenAPI é disponibilizada pelo Quarkus através das extensões:
```
quarkus-smallrye-openapi
quarkus-swagger-ui
```
### 🔎 Swagger UI

Com a aplicação em execução localmente:

👉 **[Abrir Swagger UI](http://localhost:8080/q/swagger-ui/)**

Ou acesse diretamente:

```text
http://localhost:8080/q/swagger-ui/
```

Durante a execução da aplicação, a interface Swagger UI pode ser utilizada para explorar os contratos REST disponibilizados pelo projeto.

## 🔎 Convenções Técnicas

A organização do projeto busca manter responsabilidades separadas:
```
Resource
   ↓
Service
   ↓
Repository
   ↓
Entity
```

Integrações externas são isoladas através de clients específicos:

```
Service
   ↓
External Service
   ↓
REST Client
```
DTOs e mappers são utilizados para controlar os objetos expostos pela API e separar o contrato HTTP das entidades de persistência.

Exceções de negócio são centralizadas através de um mapper global.

---

## 🚧 Evolução Recomendada

Com base nos débitos registrados na documentação técnica, os principais pontos de evolução são:

- Padronizar os valores dos enums entre código e documentação;
- Implementar validação completa de CPF;
- Fortalecer a tipagem dos DTOs;
- Garantir que senhas nunca sejam expostas por resources;
- Corrigir métodos de atualização dos repositories;
- Melhorar o processo de seed das entidades de referência;
- Remover dependências de IDs fixos;
- Padronizar delimitadores dos arquivos CSV;
- Restringir CORS para ambientes produtivos;
- Revisar a semântica HTTP de pedidos rejeitados por regra de negócio;
- Padronizar o tratamento de coleções vazias;
- Melhorar a consistência geral entre documentação, contratos e implementação.

---

## 📊 Visão Técnica
```
┌─────────────────────────────────────────────────────────────┐
│                     RAIZ DO BEM API                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  REST Resources                                             │
│       │                                                     │
│       ▼                                                     │
│  Business Services                                          │
│       │                                                     │
│       ├───────────────┐                                     │
│       │               │                                     │
│       ▼               ▼                                     │
│  Repositories     External Clients                          │
│       │               │                                     │
│       ▼               ├──────────► ViaCEP                   │
│  Oracle Database      │                                     │
│                       └──────────► Google Maps              │
│                                                             │
├─────────────────────────────────────────────────────────────┤
│ Authentication: JWT                                         │
│ Documentation: OpenAPI / Swagger                            │
│ Build: Maven                                                │
│ Runtime: Quarkus / Java 21                                  │
│ Container: Docker                                           │
│ CI/CD: Azure Pipelines                                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 📌 Informações do Projeto

| Item              | Informação         |
| ----------------- | ------------------ |
| Nome              | Raiz do Bem API    |
| Group ID          | `br.com.raizdobem` |
| Artifact ID       | `raiz-do-bem-api`  |
| Versão            | `1.0.0-SNAPSHOT`   |
| Java              | `21`               |
| Quarkus           | `3.34.5`           |
| Banco             | Oracle Database    |
| Autenticação      | JWT                |
| API Documentation | OpenAPI / Swagger  |
| Build             | Maven              |
| Containerização   | Docker             |
| CI/CD             | Azure Pipelines    |

---

## 🌱 Raiz do Bem

A API concentra a camada backend da plataforma, fornecendo os recursos necessários para gerenciamento dos beneficiários, pedidos de ajuda e atendimentos odontológicos, além das entidades de suporte e integrações externas.

A arquitetura foi organizada para separar responsabilidades entre exposição HTTP, regras de negócio, persistência, integrações e domínio, mantendo espaço para evolução dos fluxos de negócio e dos contratos da API.

