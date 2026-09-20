# Atividade 2 — Teste funcional com Playwright

Entrega da **Avaliação Prática (item 2)**: testes automatizados para as páginas de **frete** e **senha**.

## Projeto

Todo o código está em [`exemplo-playwright/`](./exemplo-playwright/).

- Exemplos (gabarito de referência): `tests/login.spec.ts`, `tests/idade.spec.ts`
- **Entrega implementada**: `tests/frete.spec.ts`, `tests/senha.spec.ts`

## Como executar

```bash
cd exemplo-playwright
npm install
npm run browsers
npm test
```

Chromium com navegador visível:

```bash
npm run test:headed
```

Relatório HTML:

```bash
npm run report
```

## Publicação

Publique este repositório (ou a pasta do trabalho) no GitHub e envie o link no formulário da disciplina.
