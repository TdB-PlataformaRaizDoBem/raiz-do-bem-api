# 📊 STATUS DO PROJETO RAIZ DO BEM — Sprint 4

**Data:** 2 de maio de 2026  
**Prazo final:** 17 de maio de 2026 (15 dias)  
**Status global:** 🟡 **55/100 pts estimados**

---

## 📈 RESUMO EXECUTIVO

| Aspecto | Status | Pts | Observação |
|---|---|---|---|
| **Arquitetura & Estrutura** | ✅ 100% | 10/10 | Camada modelo perfeita (8 entidades + 4 enums) |
| **Conexão BD + Método Endpoint** | ✅ 100% | 20/30 | EnderecoService completo (create, validate, search) |
| **Exceções & DAO & CRUD** | 🔴 10% | 1/10 | EnderecoController tem Response.* · Outros vazios |
| **Métodos de Negócio** | 🔴 20% | 6/20 | EnderecoService = obrigatório ✅ · Faltam 3 críticos |
| **API REST Completa** | 🟡 40% | 14/35 | 8 controllers existem · POST/PUT/DELETE precisam ser reais |
| **Documentação PDF** | 🟡 30% | 5/15 | Base (Sprint 3) pronta · Precisa de atualização + print |
| **TOTAL** | 🟡 **55** | **56/100** | Possível chegar a **85-90** em 15 dias com foco |

---

## ✅ JÁ ESTÁ 100% PRONTO

### 1️⃣ Método de negócio: `EnderecoService` (15 pts)

```java
✅ criar(cep, numero, tipo)          — integra ViaCEP + BD
✅ validarCep(cep)                   — regex \d{8}
✅ listarTodos()                     — lista todos
✅ listarPorCidade(cidade)           — JOIN com Endereco
✅ buscaPorId(id)                    — busca específica
✅ excluir(id)                       — @Transactional
✅ buscarApiViaCep(cep)              — chamada HTTP externa (HttpClient)
✅ validarEndereco(cep, numero, tipo) — validação completa
```

**Comprovação:** 3 prints de sucesso no Swagger (`listando_enderecos.png`, `lista_endereco_id.png`, `lista_endereco_cidade.png`)

### 2️⃣ Controller com Response tipado: `EnderecoController` (5 pts)

```java
✅ GET /enderecoDTO              → Response.ok(List)
✅ GET /enderecoDTO/{id}        → Response.ok(Endereco)
✅ GET /enderecoDTO/cidade/{} → Response.ok(List filtered)
✅ GET /enderecoDTO/viacep/{} → Response com status 200/400
✅ POST /enderecoDTO              → Response.status(201).entity()
✅ PUT /enderecoDTO/{id}         → Response.noContent() (204)
✅ DELETE /enderecoDTO/{id}      → Response.noContent() (204)
```

### 3️⃣ Entidades & Mapeamento JPA (10 pts)

```
✅ Beneficiario       → id_beneficiario, cpf, nome, etc
✅ PedidoAjuda        → id_pedido, status_pedido (enum), id_endereco (FK)
✅ Atendimento        → id_atendimento, prontuario, dataInicial, dataFinal
✅ Dentista           → id_dentista, cro, sexo (enum), disponivel
✅ Colaborador        → id_colaborador, cpf, nome, dataNascimento
✅ Endereco           → id_endereco, logradouro, cep, cidade, estado, tipo
✅ ProgramaSocial     → id_programa_social, nome, descricao
✅ Especialidade      → id_especialidade, nome

✅ Enums: Sexo, StatusPedido, TipoEndereco (+ Categoria textual)
```

### 4️⃣ Stack & Configuração (10 pts)

```
✅ Java 21 + Quarkus 3.34.5
✅ Panache + Hibernate ORM
✅ Oracle JDBC
✅ SmallRye OpenAPI (Swagger UI em :8080/q/swagger-ui)
✅ Jackson + Gson
✅ pom.xml configurado
✅ application.properties OK
```

### 5️⃣ Estrutura do Projeto (10 pts)

```
✅ DDD em camadas: controller → service → repository → model
✅ Organizção clara
✅ README.md atualizado
✅ Diagramas: Classes + Fluxo Central
✅ Prints de teste: 3 GET de Endereco
```

---

## 🔴 O QUE FALTA PARA 85+ PTS (CRÍTICO — 15 DIAS)

### 🚨 BLOQUEADOR: Pasta `exception/` (10 pts)

**Precisa:**
```
exception/
  ├── RecursoNaoEncontradoException.java
  ├── ValidacaoException.java
  ├── RegraDeNegocioException.java
  └── GlobalExceptionMapper.java (@Provider)
```

**Impacto:** Sem isso, os 3 métodos não podem lançar exceções corretas (HTTP 422, 404, 409).

**Tempo estimado:** 1-2 horas

---

### 📋 3 MÉTODOS OBRIGATÓRIOS DE NEGÓCIO (20 pts)

| # | Service | Método | Complexidade | Tempo | Bloqueador |
|---|---|---|---|---|---|
| 1 | `BeneficiarioService` | `adicionar(idPedido, idPrograma)` | 🟥 Alta | 3-4h | ✅ exception/ |
| 2 | `DentistaService` | `validarCro(cro)` | 🟨 Média | 1h | ❌ Nenhum |
| 3 | `PedidoAjudaService` | `processarPedido(id, status, idDent)` | 🟥 Alta | 3-4h | ✅ Método 1 |
| **Bônus** | `AtendimentoService` | `encerrar(id, prontuario, dataFinal, idColab)` | 🟨 Média | 2-3h | ❌ Nenhum |

**Total:** ~10-13 horas

---

### 🎮 CRUD COMPLETO EM CONTROLLERS (10 pts)

**O que falta:** Implementar POST, PUT, DELETE REAIS em (falta 5 controllers):

```
✅ EnderecoController       ← JÁ FEITO
❌ BeneficiarioController  ← POST, PUT, DELETE vazios
❌ DentistaController      ← POST, PUT, DELETE vazios
❌ PedidoAjudaController   ← POST, PUT, DELETE vazios
❌ AtendimentoController   ← POST, PUT, DELETE vazios
❌ ColaboradorController   ← POST, PUT, DELETE vazios
(EspecialidadeController, ProgramaSocialController também)
```

**Padrão a seguir (EnderecoController):**
```java
@POST
@Consumes(MediaType.APPLICATION_JSON)
public Response criar(Beneficiario b) {
    Beneficiario criado = service.fazer_logica(b);
    return Response.status(Response.Status.CREATED).entity(criado).build();
}
```

**Total:** ~5-7 horas (copy-paste + adaptação)

---

### 📄 DOCUMENTAÇÃO PDF (15 pts — mas só 5 pts faltam)

**Já tem:**
- ✅ Capa (Sprint 3 — copie e mude para Sprint 4)
- ✅ Diagrama de Classes
- ✅ Fluxo Central
- ✅ 3 prints simples de GET

**Falta adicionar:**
- ❌ **Descrição dos 4 métodos** (1 pág cada) com print do Swagger mostrando execução
- ❌ **Tabela de endpoints** (todas as rotas)
- ❌ **Prints de POST/PUT/DELETE** (uma vez implementados)
- ❌ **Instruções para rodar** (`./mvnw quarkus:dev`)

**Total:** ~4-5 horas

---

## 📅 PLANO DE AÇÃO — 15 DIAS REALISTA

### **SEMANA 1 (2-8 de maio)**

#### **Dia 1 (segunda-feira)** — Fundação
- ⏱️ 2 horas
```
[ ] 1. Criar exception/ com 4 classes
[ ] 2. Testar GlobalExceptionMapper com @Provider
[ ] 3. Commit: "feat: add custom exceptions & global exception mapper"
```

#### **Dia 2 (terça-feira)** — Primeiro método crítico
- ⏱️ 3-4 horas
```
[ ] 4. Implementar BeneficiarioService.adicionar(idPedido, idPrograma)
   [ ] Buscar PedidoAjuda por id
   [ ] Validar status == APROVADO (senão throw RegraDeNegocioException)
   [ ] Copiar dados: cpf, nome, dataNasc, telefone, email, enderecoDTO
   [ ] Copiar idPrograma social
   [ ] Salvar no BD
[ ] 5. Testes no Swagger (print de sucesso + erro)
[ ] 6. Commit: "feat: implement BeneficiarioService.adicionar()"
```

#### **Dia 3 (quarta-feira)** — Segundo método
- ⏱️ 1-2 horas
```
[ ] 7. DentistaService.validarCro(cro)
   [ ] Regex: ^[a-zA-Z]{2,}\d{2}$
   [ ] Throw ValidacaoException se inválido
[ ] 8. Testes Swagger
[ ] 9. Commit: "feat: implement DentistaService.validarCro()"
```

#### **Dia 4 (quinta-feira)** — Terceiro método
- ⏱️ 3-4 horas
```
[ ] 10. PedidoAjudaService.processarPedido(id, status, idDent)
    [ ] Buscar PedidoAjuda por id
    [ ] Validar status válido
    [ ] Atualizar status_pedido, id_dentista
    [ ] Se status == APROVADO, chamar BeneficiarioService.adicionar()
    [ ] @Transactional
[ ] 11. Testes Swagger (3 casos)
[ ] 12. Commit: "feat: implement PedidoAjudaService.processarPedido()"
```

#### **Dia 5 (sexta-feira)** — Bônus + Controllers comece
- ⏱️ 2-3 horas
```
[ ] 13. AtendimentoService.encerrar(id, prontuario, dataFinal, idColab)
[ ] 14. Começar BeneficiarioController POST/PUT/DELETE (2 controladores)
[ ] 15. Commit: "feat: implement AtendimentoService.encerrar() + start BeneficiarioController"
```

### **SEMANA 2 (9-15 de maio)**

#### **Dia 6 (segunda-feira)** — CRUD Controllers
- ⏱️ 3-4 horas
```
[ ] 16. DentistaController POST/PUT/DELETE real
[ ] 17. PedidoAjudaController POST/PUT/DELETE real
[ ] 18. ColaboradorController POST/PUT/DELETE real (copy-paste fácil)
[ ] 19. Testes: ~10 prints GET/POST/PUT/DELETE de cada
[ ] 20. Commit: "feat: implement full CRUD controllers"
```

#### **Dia 7 (terça-feira)** — Últimos Controllers + Teste
- ⏱️ 2-3 horas
```
[ ] 21. AtendimentoController POST/PUT/DELETE
[ ] 22. EspecialidadeController POST/PUT/DELETE
[ ] 23. ProgramaSocialController POST/PUT/DELETE
[ ] 24. ./mvnw compile ← Garantir que compila 100%
[ ] 25. Commit: "feat: complete all CRUD operations"
```

#### **Dia 8 (quarta-feira)** — Documentação Parte 1
- ⏱️ 3-4 horas
```
[ ] 26. Copiar Sprint03Java.pdf → Raiz_do_Bem_Documentacao_Sprint4.pdf
[ ] 27. Atualizar capa: Sprint 4 (2026)
[ ] 28. Adicionar: "Métodos de Negócio Implementados"
    [ ] BeneficiarioService.adicionar() + print Swagger
    [ ] DentistaService.validarCro() + print Swagger
    [ ] PedidoAjudaService.processarPedido() + print Swagger
    [ ] AtendimentoService.encerrar() + print Swagger
[ ] 29. Adicionar tabela de endpoints (copiar de README + expand)
```

#### **Dia 9 (quinta-feira)** — Documentação Parte 2 + Finalizações
- ⏱️ 2-3 horas
```
[ ] 30. Adicionar prints de POST/PUT/DELETE (mínimo 3 operações)
[ ] 31. Adicionar "Como rodar" (./mvnw quarkus:dev)
[ ] 32. Revisar MER (banco de dados)
[ ] 33. Revisar Diagrama de Classes (ajustar se necessário)
[ ] 34. Salvar PDF final: docs/Raiz_do_Bem_Documentacao_Sprint4.pdf
```

#### **Dia 10 (sexta-feira)** — BUFFER + QA Final
- ⏱️ 2-3 horas
```
[ ] 35. Testar todos os 4 métodos 1x mais (screenshots finais)
[ ] 36. Verificar se compila: ./mvnw compile
[ ] 37. Revisar README.md (está atualizado?)
[ ] 38. Revisar structure de docs/ (está tudo lá?)
[ ] 39. Último commit: "chore: finalize Sprint 4 documentation & testing"
```

### **SEMANA 3 (16-17 de maio)**

#### **Dia 11-12 (segunda-terça)** — Revisão Final
- ⏱️ 2 horas
```
[ ] 40. Code review (SEM ERROS DE COMPILAÇÃO)
[ ] 41. Validar: todos os endpoints retornam Response correto
[ ] 42. Validar: exceções são lançadas (HTTP 404, 422, 409)
[ ] 43. Preparar para entrega
```

#### **Dia 13 (quarta)** — ENTREGA
- ⏱️ Criar release no GitHub
```
[ ] 44. Push final: git push origin main
[ ] 45. Create Release v1.0.0-SPRINT4
[ ] 46. Anexar PDF na release
[ ] 47. Enviar link para professora
```

---

## 📊 ESTIMATIVA P/ 85+ PTS

| Item | Pts | Status | Tempo |
|---|---|---|---|
| Arquitetura | 10 | ✅ Pronto | 0h |
| Entidades | 10 | ✅ Pronto | 0h |
| BD + EnderecoService | 20 | ✅ Pronto | 0h |
| **Exceptions (NOVO)** | 10 | 🔴 Fazer | **1-2h** |
| **3 Métodos críticos (NOVO)** | 20 | 🔴 Fazer | **10-12h** |
| **CRUD Controllers (atualizar)** | 10 | 🟡 Parcial | **5-7h** |
| **API REST Endpoints** | 15 | 🟡 Parcial | **Incluído acima** |
| **Documentação PDF** | 15 | 🟡 Parcial | **4-5h** |
| **TOTAL** | **100** | 🟡 **~55** | **20-26h de trabalho** |

**Meta realista em 15 dias:** 🎯 **85-90 pts**

---

## ⚠️ RISCOS & MITIGAÇÃO

| Risco | Probabilidade | Impacto | Mitigação |
|---|---|---|---|
| **BD Oracle offline** | 🟡 Média | 🔴 Alto | Testar conexão 1x por dia · Ter dados de teste local |
| **Métodos com bugs** | 🟡 Média | 🟡 Médio | Teste cada método 3x antes de mover adiante |
| **Falta tempo docs** | 🟢 Baixa | 🔴 Alto | **Começar docs no dia 8** (não deixar para última hora) |
| **Compilação quebra** | 🟢 Baixa | 🔴 Alto | Fazer `./mvnw compile` DIARIAMENTE · Fixar erros no mesmo dia |
| **PDF perde pontos** | 🟡 Média | 🟡 Médio | Seguir checklist dos 8 itens obrigatórios |

---

## ✅ CHECKLIST FINAL ANTES DA ENTREGA

```
CÓDIGO:
[ ] ./mvnw compile ← SEM ERROS
[ ] Swagger UI abre em http://localhost:8080/q/swagger-ui
[ ] Todos os 8 controllers listam (GET /)
[ ] Os 4 métodos executam sem erro
[ ] Exceptions retornam HTTP correto (404, 422, 409)

DOCUMENTAÇÃO (Sprint 4 PDF):
[ ] Capa com nomes e data 2026
[ ] Objetivo do projeto (1 pág)
[ ] 4 métodos explicados + print Swagger cada
[ ] Tabela de endpoints (≥20 linhas)
[ ] Protótipos/diagramas (Classes + Fluxo)
[ ] MER do banco
[ ] "Como rodar" (./mvnw quarkus:dev explicado)
[ ] Referências (Quarkus, Oracle, etc)

GITHUB:
[ ] README.md atualizado com docs/
[ ] docs/ pasta tem: PDF + diagramas + prints
[ ] pom.xml sem erros
[ ] 0 erros no `./mvnw clean compile`
[ ] README.md aponta para PDF correto

FINAL:
[ ] Tag release v1.0.0-SPRINT4 criada
[ ] PDF anexado na release
[ ] Link compartilhado com equipe
```

---

## 🚀 PRÓXIMOS PASSOS IMEDIATOS (HOJE)

1. **Crie o arquivo** `exception/RecursoNaoEncontradoException.java`
2. **Crie o arquivo** `exception/ValidacaoException.java`
3. **Crie o arquivo** `exception/RegraDeNegocioException.java`
4. **Crie o arquivo** `exception/GlobalExceptionMapper.java`
5. **Faça commit:** `git add -A && git commit -m "feat: add exception layer"`
6. **Teste:** `./mvnw compile`

**Tempo:** ~1 hora  
**Ganho:** +10 pts bloqueador para os métodos

---

## 📞 RESUMO EM UMA LINHA

> **Você está a 55% da nota. Faltam 3 métodos + exceptions + docs. Com 15 dias + 20-26 horas focadas, chega facilmente em 85-90%.**

**Comece HOJE pelas exceptions. Dia 2 começar método 1. Até dia 10, tudo pronto. Dias 11-12, revisão. Dia 13, entrega.**

Quer que eu comece implementando as exceptions agora? 🚀

