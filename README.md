# Projeto, Implementação e Teste de Software — Entregas

Repositório de entregas da **Avaliação Prática (Prova 01)** — Engenharia de Software, 2026.

**Aluno:** Enzo Ayres Bettini  
**RA:** 241955272

Material de referência da disciplina: [teste_software_2026](https://github.com/JoaoChoma/teste_software_2026) (repositório oficial do professor).

---

## Estrutura do repositório

| Pasta | Avaliação prática | Conteúdo |
|-------|-------------------|----------|
| [`atividade 1/`](./atividade%201/) | Item 1 — Plano e casos de teste | Documentos Word (sistema de reserva de salas) |
| [`atividade 2/`](./atividade%202/) | Item 2 — Teste funcional (Playwright) | Testes `frete.spec.ts` e `senha.spec.ts` |
| [`atividade 3/`](./atividade%203/) | Item 3 — Teste estrutural (JUnit) | Projeto `central-pedidos` + `RELATORIO.md` + JaCoCo |
| [`atividade 4/`](./atividade%204/) | Item 4 — GFC (revisão) | Respostas dos exercícios de grafo de fluxo de controle |

Cada pasta possui um `README.md` com detalhes da entrega.

---

## Atividade 1 — Documentação de testes

- `Casos de Teste - Sistema de Reserva de Salas.docx`
- `Plano de Teste - Sistema de Reserva de Salas.docx`

Escopo: requisitos funcionais e não funcionais do produto de reserva de salas (RF-01 a RF-08, RNF-01 a RNF-03).

---

## Atividade 2 — Playwright

```bash
cd "atividade 2/exemplo-playwright"
npm install
npm run browsers
npm test
```

Dependências pesadas (`node_modules/`, `.browsers/`) **não** devem ser versionadas — já constam no `.gitignore` do projeto.

---

## Atividade 3 — JUnit e JaCoCo

```bash
cd "atividade 3/central-pedidos"
mvn clean test
open target/site/jacoco/index.html
```

Requisitos: JDK 17+ e Maven 3.9+. A pasta `target/` não entra no Git.

---

## Atividade 4 — Grafos de fluxo de controle

Respostas em Markdown e Word, com CFG, complexidade ciclomática, caminhos independentes e dados de teste para os dois exercícios da SEMANA 07.

---

## Entrega à disciplina

Enviar a **URL completa deste repositório** no formulário indicado pelo professor:

https://forms.gle/kgyPs1E2J9cUQ1Ve7

Confirme que o repositório está **público** (ou acessível ao avaliador) antes de submeter.
