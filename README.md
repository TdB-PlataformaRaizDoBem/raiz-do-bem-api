# 🌱 Raiz do Bem — API

> Plataforma de gestão de beneficiários, atendimentos odontológicos, colaboradores e programas sociais para uma ONG de saúde bucal.

[![Java](https://img.shields.io/badge/Java-21-orange?logo=java)](https://openjdk.org/projects/jdk/21/)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.34.5-blue?logo=quarkus)](https://quarkus.io/)
[![Oracle DB](https://img.shields.io/badge/Oracle-DB-red?logo=oracle)](https://www.oracle.com/database/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger_UI-green?logo=swagger)](http://localhost:8080/q/swagger-ui)

---

## 📋 Sobre o projeto

A **Raiz do Bem API** é o backend REST da plataforma Raiz do Bem, desenvolvida como projeto acadêmico para o **FIAP Challenge 2025-26**, na disciplina *Domain Driven Design Using Java* — **Sprint 4 (entrega final)**.

O projeto migrou de um backend Java puro com JDBC (Sprint 3, disponível em [`raiz-do-bem-backend-java`](https://github.com/TdB-PlataformaRaizDoBem/raiz-do-bem-backend-java)) para **Quarkus com Panache**, mantendo todas as regras de negócio e o schema Oracle já existentes.

---

## ��️ Arquitetura — Domain Driven Design

A aplicação segue uma estrutura em camadas baseada em DDD:

```
src/main/java/br/com/raizdobem/api/
├── controller/     Recursos REST (JAX-RS) — camada de apresentação
├── service/        Regras de negócio (BO) — camada de domínio
├── repository/     Acesso a dados com Panache (DAO) — camada de infraestrutura
├── model/          Entidades e enums — camada de domínio
└── exception/      Exceções customizadas — camada transversal
```

---

## 🗂️ Entidades do domínio

| Classe | Tipo | Descrição |
|---|---|---|
| `Beneficiario` | Entidade | Pessoa atendida pelos programas sociais |
| `PedidoAjuda` | Entidade | Solicitação de auxílio do beneficiário |
| `ProgramaSocial` | Entidade | Programa de assistência vinculado ao beneficiário |
| `Atendimento` | Entidade | Consulta odontológica (campo `prontuario`) |
| `Dentista` | Entidade | Profissional com CRO, categoria e disponibilidade |
| `Especialidade` | Entidade | Especialidade odontológica |
| `Colaborador` | Entidade | Voluntário / funcionário da ONG |
| `Endereco` | Entidade | Endereço com integração ViaCEP |
| `Sexo` | Enum | `MASCULINO` / `FEMININO` |
| `StatusPedido` | Enum | `PENDENTE` / `APROVADO` / `REJEITADO` |
| `TipoEndereco` | Enum | `RESIDENCIAL` / `PROFISSIONAL` |
| `Categoria` | Enum | `COORDENADOR` / `CLINICO` |

---

## 🌐 Endpoints REST

Base URL: `http://localhost:8080`  
Swagger UI: `http://localhost:8080/q/swagger-ui`

### Beneficiário — `/beneficiario`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/beneficiario` | Lista todos os beneficiários |
| `POST` | `/beneficiario` | Cria um novo beneficiário |
| `GET` | `/beneficiario/{cpf}` | Busca beneficiário por CPF |
| `PUT` | `/beneficiario/{cpf}` | Atualiza dados do beneficiário |
| `DELETE` | `/beneficiario/{cpf}` | Remove um beneficiário |

### Dentista — `/dentista`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/dentista` | Lista todos os dentistas |
| `POST` | `/dentista` | Cadastra um novo dentista |
| `PUT` | `/dentista/{cpf}` | Atualiza dados do dentista |
| `DELETE` | `/dentista/{cpf}` | Remove um dentista |

### Atendimento — `/atendimento`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/atendimento` | Lista todos os atendimentos |
| `POST` | `/atendimento` | Cria um novo atendimento |
| `GET` | `/atendimento/{cpf}` | Busca atendimento pelo CPF do beneficiário |
| `PUT` | `/atendimento/{id}` | Encerra / atualiza atendimento |
| `DELETE` | `/atendimento/{id}` | Remove um atendimento |

### Pedido de Ajuda — `/pedido-ajuda`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/pedido-ajuda` | Lista todos os pedidos |
| `POST` | `/pedido-ajuda` | Registra um novo pedido |
| `PUT` | `/pedido-ajuda/{id}` | Processa pedido (aprova / rejeita) |
| `DELETE` | `/pedido-ajuda/{id}` | Remove um pedido |

### Colaborador — `/colaborador`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/colaborador` | Lista todos os colaboradores |
| `POST` | `/colaborador` | Cadastra um novo colaborador |
| `PUT` | `/colaborador/{cpf}` | Atualiza dados do colaborador |
| `DELETE` | `/colaborador/{cpf}` | Remove um colaborador |

### Endereço — `/endereco`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/endereco` | Lista todos os endereços |
| `GET` | `/endereco/cidade/{cidade}` | Filtra endereços por cidade |
| `POST` | `/endereco` | Cria um novo endereço |
| `GET` | `/endereco/viacep/{cep}` | Consulta endereço na API ViaCEP 🔗 |
| `PUT` | `/endereco/{id}` | Atualiza um endereço |
| `DELETE` | `/endereco/{id}` | Remove um endereço |

---

## 🛠️ Stack tecnológica

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem principal |
| Quarkus | 3.34.5 | Framework Java nativo em nuvem |
| Hibernate ORM + Panache | via Quarkus BOM | Persistência e repositories |
| Oracle JDBC | via Quarkus BOM | Driver de banco de dados |
| SmallRye OpenAPI | via Quarkus BOM | Documentação Swagger UI |
| Jackson | via Quarkus BOM | Serialização JSON |
| Gson | 2.13.2 | Parsing JSON na integração ViaCEP |
| ViaCEP | API pública | Consulta de endereços por CEP |

---

## ⚙️ Pré-requisitos

- **Java 21** ([Temurin](https://adoptium.net/) recomendado)
- **Maven 3.9+** (ou use o wrapper `./mvnw` incluso no projeto)
- Acesso à rede do **servidor Oracle FIAP** para conectar ao banco em produção

---

## 🚀 Como executar

### 1. Clonar o repositório

```bash
git clone https://github.com/TdB-PlataformaRaizDoBem/raiz-do-bem-api.git
cd raiz-do-bem-api
```

### 2. Configurar variáveis de ambiente

```bash
export DB_USER=seu_usuario
export DB_PASSWORD=sua_senha
# DB_URL padrão: jdbc:oracle:thin:@//oracle.fiap.com.br:1521/orcl
```

### 3. Modo desenvolvimento (hot reload)

```bash
./mvnw quarkus:dev
```

- Aplicação: `http://localhost:8080`
- Dev UI (Quarkus): `http://localhost:8080/q/dev/`
- Swagger UI: `http://localhost:8080/q/swagger-ui`

### 4. Compilar

```bash
./mvnw compile
```

### 5. Executar testes

```bash
./mvnw test
```

### 6. Build para produção

```bash
# JAR padrão
./mvnw package -DskipTests
java -jar target/quarkus-app/quarkus-run.jar

# Über-JAR (JAR único auto-contido)
./mvnw package -Dquarkus.package.jar.type=uber-jar -DskipTests
java -jar target/*-runner.jar
```

### 7. Executável nativo (GraalVM)

```bash
# Com GraalVM instalado localmente
./mvnw package -Dnative -DskipTests

# Sem GraalVM — build via container Docker
./mvnw package -Dnative -Dquarkus.native.container-build=true -DskipTests

# Executar
./target/raiz-do-bem-api-1.0.0-SNAPSHOT-runner
```

---

## 🐳 Docker

Imagens disponíveis em `src/main/docker/`:

| Arquivo | Descrição |
|---|---|
| `Dockerfile.jvm` | Imagem JVM padrão (recomendada) |
| `Dockerfile.legacy-jar` | Imagem com über-JAR |
| `Dockerfile.native` | Imagem com executável nativo |
| `Dockerfile.native-micro` | Imagem nativa minimalista |

```bash
# Build da imagem JVM
docker build -f src/main/docker/Dockerfile.jvm -t raiz-do-bem-api:latest .

# Executar o container
docker run -p 8080:8080 \
  -e DB_USER=seu_usuario \
  -e DB_PASSWORD=sua_senha \
  raiz-do-bem-api:latest
```

---

## 🗄️ Banco de dados

A aplicação conecta ao banco Oracle do servidor FIAP:

```
URL: jdbc:oracle:thin:@//oracle.fiap.com.br:1521/orcl
```

As credenciais são fornecidas **exclusivamente via variáveis de ambiente** — nunca hardcoded no código-fonte.

### Tabelas principais

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

---

## 🔗 Integração ViaCEP

O endpoint `GET /endereco/viacep/{cep}` consulta a API pública [ViaCEP](https://viacep.com.br) e retorna os dados de endereço correspondentes ao CEP informado (8 dígitos, somente números).

```bash
curl http://localhost:8080/endereco/viacep/01310100
```

---

## 📁 Estrutura do projeto

```
raiz-do-bem-api/
├── src/
│   ├── main/
│   │   ├── docker/                       Dockerfiles (jvm, native, legacy-jar)
│   │   ├── java/br/com/raizdobem/api/
│   │   │   ├── controller/               8 controllers REST
│   │   │   ├── model/                    10 entidades + 4 enums
│   │   │   ├── repository/               7 repositories (Panache)
│   │   │   └── service/                  7 services
│   │   └── resources/
│   │       └── application.properties    Configuração do Quarkus
│   └── test/
├── .github/
│   ├── copilot-instructions.md           Instruções para GitHub Copilot
│   └── prompts/                          Prompts de desenvolvimento assistido
├── pom.xml
└── README.md
```

---

## 🎓 Contexto acadêmico

| Campo | Valor |
|---|---|
| Instituição | FIAP |
| Programa | Challenge 2025-26 |
| Disciplina | Domain Driven Design Using Java |
| Sprint | 4 — Entrega final |
| Base Sprint 3 | [`raiz-do-bem-backend-java`](https://github.com/TdB-PlataformaRaizDoBem/raiz-do-bem-backend-java) |

---

## 📚 Referências

- [Quarkus — Guia oficial](https://quarkus.io/guides/)
- [Hibernate ORM com Panache](https://quarkus.io/guides/hibernate-orm-panache)
- [Quarkus REST (Jakarta REST)](https://quarkus.io/guides/rest)
- [JDBC Driver — Oracle](https://quarkus.io/guides/datasource)
- [SmallRye OpenAPI / Swagger UI](https://quarkus.io/guides/openapi-swaggerui)
- [API ViaCEP](https://viacep.com.br/)
