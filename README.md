# 🌱 Raiz do Bem — API

> Backend REST da plataforma Raiz do Bem, voltada à gestão de beneficiários, atendimentos odontológicos, colaboradores, pedidos de ajuda e programas sociais.

[![Java](https://img.shields.io/badge/Java-21-orange?logo=java)](https://openjdk.org/projects/jdk/21/)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.34.5-blue?logo=quarkus)](https://quarkus.io/)
[![Oracle DB](https://img.shields.io/badge/Oracle-DB-red?logo=oracle)](https://www.oracle.com/database/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger_UI-green?logo=swagger)](http://localhost:8080/q/swagger-ui)

## Visão geral

A **Raiz do Bem API** foi desenvolvida como entrega acadêmica do **FIAP Challenge 2025-26** para a disciplina *Domain Driven Design Using Java* — **Sprint 4 (entrega final)**.

O projeto evoluiu de uma solução Java/JDBC da Sprint 3 para uma API em **Quarkus 3**, com **Hibernate ORM + Panache**, **Oracle DB**, **OpenAPI/Swagger** e tratamento centralizado de exceções. O objetivo é entregar uma base robusta, documentada e pronta para integração com front-end.

### Repositório e histórico de evolução

- Repositório principal no Azure DevOps: [raiz-do-bem-backend](https://dev.azure.com/Tdb-Raiz-do-Bem/raiz-do-bem-backend/_git/raiz-do-bem-backend)
- Base histórica da Sprint 3: backend Java/JDBC migrado para a stack atual em Quarkus

### Diferenciais da solução

- Arquitetura em camadas com organização por responsabilidade.
- Persistência com Panache, facilitando CRUD e consultas JPQL.
- Integração com Oracle e mapeamento de tabelas já existentes.
- Regras de negócio concentradas em `service/`, não nos recursos REST.
- Integração com ViaCEP para acelerar o cadastro de endereços.
- Documentação de API disponível via Swagger UI.

## Arquitetura do projeto

O código-fonte está organizado no pacote raiz `br.com.raizdobem.api`:

```text
src/main/java/br/com/raizdobem/api/
├── dto/         Objetos de entrada e saída da API
├── entity/      Entidades JPA e enums do domínio
├── exception/   Exceções customizadas e mapper global
├── repository/  Acesso a dados com PanacheRepository
├── resource/    Endpoints REST (JAX-RS)
└── service/     Regras de negócio e validações
```

### Domínios principais

| Entidade | Finalidade |
|---|---|
| `Beneficiario` | Pessoa atendida pela ONG |
| `PedidoAjuda` | Solicitação de auxílio e triagem |
| `ProgramaSocial` | Programa de assistência social |
| `Atendimento` | Consulta odontológica / prontuário |
| `Dentista` | Profissional com CRO, categoria e disponibilidade |
| `Especialidade` | Especialidade odontológica |
| `Colaborador` | Voluntário ou funcionário |
| `Endereco` | Endereço com integração ViaCEP |

### Enums do projeto

- `Sexo` → `MASCULINO`, `FEMININO`
- `StatusPedido` → `PENDENTE`, `APROVADO`, `REJEITADO`
- `TipoEndereco` → `RESIDENCIAL`, `COMERCIAL`

## Tecnologias utilizadas

| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem principal |
| Quarkus 3.34.5 | Framework da API |
| Hibernate ORM + Panache | Persistência e repositórios |
| Oracle JDBC | Conexão com banco Oracle |
| SmallRye OpenAPI | Documentação Swagger |
| Jackson | Serialização JSON |
| Gson | Consumo da API ViaCEP |
| Jakarta REST | Exposição dos endpoints |

## API REST

Base URL local: `http://localhost:8080`  
Swagger UI: `http://localhost:8080/q/swagger-ui`

### Resumo dos recursos expostos

| Recurso | Rotas principais |
|---|---|
| `Beneficiario` | `GET /beneficiario`, `POST /beneficiario`, `GET /beneficiario/{cpf}`, `PUT /beneficiario/{cpf}`, `DELETE /beneficiario/{cpf}` |
| `Dentista` | `GET /dentista`, `POST /dentista`, `GET /dentista/disponiveis`, `GET /dentista/cidade/{cidade}`, `PUT /dentista/{cpf}`, `DELETE /dentista/{cpf}` |
| `Atendimento` | `GET /atendimento`, `POST /atendimento`, `GET /atendimento/{cpf}`, `PUT /atendimento/{id}`, `DELETE /atendimento/{id}` |
| `PedidoAjuda` | `GET /pedido-ajuda`, `POST /pedido-ajuda`, `GET /pedido-ajuda/data/{data}`, `PUT /pedido-ajuda/{id}`, `DELETE /pedido-ajuda/{id}` |
| `Colaborador` | `GET /colaborador`, `POST /colaborador`, `PUT /colaborador/{cpf}`, `DELETE /colaborador/{cpf}` |
| `Endereco` | `GET /endereco`, `GET /endereco/id/{id}`, `GET /endereco/{cidade}`, `GET /endereco/viacep/{cep}`, `POST /endereco`, `PUT /endereco/{id}`, `DELETE /endereco/{id}` |
| `Especialidades` | `GET /especialidades`, `GET /especialidades/{id}` |
| `Programas Sociais` | `GET /programas-sociais`, `GET /programas-sociais/{id}` |

### Códigos de resposta esperados

- `200 OK` — leitura ou atualização concluída com sucesso
- `201 Created` — criação de recurso
- `204 No Content` — exclusão bem-sucedida
- `404 Not Found` — recurso inexistente
- `409 Conflict` — violação de regra de negócio
- `422 Unprocessable Entity` — validação inválida
- `500 Internal Server Error` — falha inesperada

## Regras de negócio concentradas em `service/`

Os serviços da aplicação centralizam as regras mais importantes da solução:

- `BeneficiarioService.adicionar(idPedido, idProgramaSocial)`
- `BeneficiarioService.listarPorCidade(cidade)`
- `BeneficiarioService.listarPorPrograma(idPrograma)`
- `DentistaService.validarCro(cro)`
- `DentistaService.listarDisponiveis()`
- `DentistaService.listarPorCidade(cidade)`
- `PedidoAjudaService.processarPedido(id, novoStatus, idDentista)`
- `PedidoAjudaService.listarPorData(data)`
- `PedidoAjudaService.validarIdadePedido(dataNasc)`
- `AtendimentoService.criar(prontuario, idBeneficiario, idDentista)`
- `AtendimentoService.encerrar(id, prontuario, dataFinal, idColaborador)`

Esses métodos dão valor à API porque automatizam validações e reduzem a lógica espalhada pelos recursos REST.

## Banco de dados

A aplicação usa Oracle e mantém o mapeamento compatível com o schema já existente.

### Configuração atual de desenvolvimento

O arquivo `src/main/resources/application.properties` define a fonte de dados Oracle para o ambiente local de desenvolvimento.

> Para ambientes compartilhados ou de entrega, a recomendação é manter usuário, senha e URL fora do código e sobrescrever a configuração conforme o ambiente.

### Tabelas principais mapeadas

| Tabela Oracle | Entidade Java | Chave primária |
|---|---|---|
| `Beneficiario` | `Beneficiario` | `id_beneficiario` |
| `Dentista` | `Dentista` | `id_dentista` |
| `Atendimento` | `Atendimento` | `id_atendimento` |
| `Pedido_Ajuda` | `PedidoAjuda` | `id_pedido` |
| `Endereco` | `Endereco` | `id_endereco` |
| `Colaborador` | `Colaborador` | `id_colaborador` |
| `Especialidade` | `Especialidade` | `id_especialidade` |
| `Programa_Social` | `ProgramaSocial` | `id_programa_social` |

## Como executar no Windows

### Pré-requisitos

- Java 21
- Maven Wrapper do projeto (`mvnw.cmd`)
- Banco Oracle acessível para leitura e escrita

### 1) Configurar variáveis de ambiente

```powershell
$env:DB_USER="seu_usuario"
$env:DB_PASSWORD="sua_senha"
$env:DB_URL="jdbc:oracle:thin:@//oracle.fiap.com.br:1521/orcl"
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

### 5) Gerar build de produção

```powershell
.\mvnw.cmd package -DskipTests
```

### 6) Acessar a aplicação

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/q/swagger-ui`

---

## Integração com ViaCEP

O endpoint `GET /endereco/viacep/{cep}` consulta a API pública [ViaCEP](https://viacep.com.br) e retorna os dados do CEP informado.

Exemplo:

```powershell
curl http://localhost:8080/endereco/viacep/01310100
```

## Documentação e evidências

A pasta `docs/` concentra os materiais de apoio do projeto:

- `docs/diagrams/` — diagramas do sistema
- `docs/prints_swagger/` — evidências dos endpoints no Swagger
- `docs/banco/` — materiais do banco e base SQL da Sprint 3
- `docs/documentacao-java/` — material de apoio documental

### Itens que valorizam a entrega final

- documentação técnica organizada
- prints de execução real no Swagger
- diagrama de classes atualizado
- MER do banco e mapeamento das entidades
- instruções claras de execução e build

## Estrutura resumida do projeto

```text
raiz-do-bem-api/
├── src/main/java/br/com/raizdobem/api/
│   ├── dto/
│   ├── entity/
│   ├── exception/
│   ├── repository/
│   ├── resource/
│   └── service/
├── src/main/resources/
│   ├── application.properties
│   └── import.sql
├── docs/
│   ├── banco/
│   ├── diagrams/
│   ├── documentacao-java/
│   └── prints_swagger/
├── pom.xml
├── mvnw
└── mvnw.cmd
```

## Contexto acadêmico

| Campo | Valor |
|---|---|
| Instituição | FIAP |
| Programa | Challenge 2025-26 |
| Disciplina | Domain Driven Design Using Java |
| Sprint | 4 — entrega final |
| Solução | Raiz do Bem |

## Referências úteis

- [Quarkus — documentação oficial](https://quarkus.io/guides/)
- [Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache)
- [Quarkus REST](https://quarkus.io/guides/rest)
- [Oracle Database](https://www.oracle.com/database/)
- [Swagger / OpenAPI](https://quarkus.io/guides/openapi-swaggerui)
- [ViaCEP](https://viacep.com.br/)
