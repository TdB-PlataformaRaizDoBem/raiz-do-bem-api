---
name: raiz-do-bem-e2e
description: >-
  Use esta skill quando o usuário pedir para validar, testar ou fazer smoke test
  da plataforma Raiz do Bem (frontend React em https://raiz-do-bem.vercel.app/ e
  API Java/Quarkus deste repositório), verificar contratos frontend/backend,
  executar as suítes de teste existentes ou gerar relatório de QA com evidências.
  Aplica regras estritas de segurança: nenhuma escrita no ambiente publicado.
---

# Raiz do Bem — QA end-to-end seguro

Skill de QA que **reutiliza** o que o projeto já tem (Vitest no frontend, JUnit +
Mockito no backend). Não duplica o que está em `README.md`/`ARCHITECTURE.md` de
cada repositório; leia-os para detalhes.

## Layout dos repositórios (irmãos, Git separados)

- Backend: `raiz-do-bem-api/` (este repositório, Quarkus, Maven Wrapper).
- Frontend: `../Raiz-do-Bem/` (React 19 + Vite + TypeScript).
- Frontend publicado: https://raiz-do-bem.vercel.app/

## Regras de segurança (inegociáveis)

No ambiente publicado e em qualquer ambiente que use o Oracle compartilhado:

1. Não criar, editar nem excluir registros; não enviar formulários; não aprovar/rejeitar pedidos.
2. Não autenticar, não repetir tentativas de login, não alterar contas, senhas ou configurações.
3. Não fazer testes de carga nem chamadas HTTP repetitivas.
4. Não ler, imprimir, copiar nem registrar em relatório: `.env*`, `*.pem`, tokens JWT,
   senhas, chaves de API, strings de conexão. Cite apenas **nomes** de variáveis.
5. Não alterar código de produção, permissões, CORS, JWT ou chaves; não fazer commit, push ou deploy.
6. Não instalar dependências (inclui Playwright) sem aprovação explícita do usuário.
7. **Não inventar nem inferir a URL da API.** Ela só vale depois de confirmada pelo usuário
   (ver `references/matriz-contratos.md`, seção "URL da API").
8. Se uma página disparar mutação automática, interrompa e documente.

## Classificação de cenários

`NAV` navegação · `LEITURA` consulta comprovadamente sem efeito colateral ·
`MOCK` simulação local · `ESCRITA-ISOLADA` só em ambiente descartável com dados sintéticos ·
`BLOQUEADO` falta credencial, ambiente ou isolamento.

GET **não** é automaticamente seguro: confira o serviço no backend antes de classificar.
Hoje **não existe banco de teste isolado** (só Oracle; sem H2/Testcontainers/profile `%test`),
portanto todo cenário `ESCRITA-ISOLADA` está `BLOQUEADO`.

## Procedimento

1. **Ambientes.** Liste o que está disponível sem expor segredos: frontend local
   (`npm run dev`, porta 5173), backend local (`:8080`, exige Oracle), frontend publicado,
   URL da API (confirmada ou não). Só use ambientes marcados como autorizados.
2. **Matriz.** Leia `references/matriz-contratos.md` e confirme se o código ainda corresponde
   (rotas em `Raiz-do-Bem/src/Routes` e `src/App.tsx`; endpoints em
   `raiz-do-bem-api/src/main/java/br/com/raizdobem/api/resource`). Registre divergências como
   incompatibilidade **confirmada** (com arquivo e linha) ou **hipótese**.
3. **Testes locais com mocks** (ver tabela de comandos abaixo).
4. **Smoke do frontend publicado** apenas nas rotas da lista `NAV` da matriz, com o navegador
   embutido do Antigravity (`/browser`). Observe console, rede, título e status.
   Antes de abrir uma rota, confira em "Chamadas automáticas ao carregar" da matriz se ela
   consulta a API; se consultar e a URL da API não estiver autorizada, **não abra**.
5. **Consultas HTTP à API:** somente após o usuário confirmar a URL e autorizar, e apenas
   para endpoints marcados `LEITURA` (sem token).
6. **Relatório** no formato de `references/relatorio-template.md`. Separe **falha confirmada**
   (evidência reproduzível) de **hipótese**. Liste tudo que ficou `BLOQUEADO` e o motivo.
   Nunca marque como aprovado o que não foi executado.

## Comandos de teste

| Comando | Diretório | Pré-requisitos de segurança |
|---|---|---|
| `.\mvnw.cmd test` | `raiz-do-bem-api` | Verificado estaticamente: todas as classes de teste usam Mockito puro, sem `@QuarkusTest`, sem `src/test/resources`; o Quarkus não sobe, então não há conexão ao Oracle. Reconfirme que nenhum `@QuarkusTest` foi adicionado (`Select-String -Path src\test -Pattern QuarkusTest -Recurse`). Evite `clean` se não quiser apagar `target/` (gitignored). Baixa dependências do Maven. |
| `npm test` | `Raiz-do-Bem` | `vite.config.ts` força `API_BASE_URL=''` nos testes e os serviços usam `fetch` simulado (`src/test/http.ts`). Exige `node_modules` (`npm ci` instala a partir do lockfile; peça aprovação). Falha se a cobertura ficar abaixo de 92%. |
| `npm run lint` e `npm run build` | `Raiz-do-Bem` | Somente leitura de código; `build` grava `dist/` (gitignored). `build` lê `.env*` locais: não os exiba. |
| `npm run dev` | `Raiz-do-Bem` | Só com `API_BASE_URL` apontando para backend **local**. Nunca aponte para a API publicada com credenciais reais. |
| `.\mvnw.cmd quarkus:dev` | `raiz-do-bem-api` | **Bloqueado** por padrão: usa o `.env` local e pode conectar ao Oracle compartilhado. Só com banco descartável confirmado pelo usuário. |

Números de testes: o README do backend informa 285 testes, o usuário citou 358 e uma contagem
estática de anotações `@Test` deu 321. Trate todos como históricos até executar a suíte.

## Verificação

- Relatório contém comandos, saídas resumidas (contagem de testes/falhas) e evidências sem segredos.
- Nenhum arquivo de produção foi alterado: `git status --short` nos dois repositórios mostra só arquivos do agente/relatórios.
- Todo item sem execução real está rotulado `NÃO EXECUTADO` ou `BLOQUEADO`.
