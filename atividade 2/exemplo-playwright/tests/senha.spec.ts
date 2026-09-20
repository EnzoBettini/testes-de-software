import { test, expect } from '@playwright/test';

type CasoSenha = {
  senha: string;
  confirmacao: string;
  aceito: boolean;
  mensagem: string;
  classe: string;
};

const casosFormatoInvalido: CasoSenha[] = [
  { senha: 'Abc123', confirmacao: 'Abc123', aceito: false, mensagem: 'Senha fora do padrão', classe: '7 caracteres' },
  { senha: 'Abcd123', confirmacao: 'Abcd123', aceito: false, mensagem: 'Senha fora do padrão', classe: 'limite inferior inválido' },
  {
    senha: 'Abcdefghij1234567890X',
    confirmacao: 'Abcdefghij1234567890X',
    aceito: false,
    mensagem: 'Senha fora do padrão',
    classe: '21 caracteres',
  },
  { senha: 'abcdefgh1', confirmacao: 'abcdefgh1', aceito: false, mensagem: 'Senha fora do padrão', classe: 'sem maiúscula' },
  { senha: 'ABCDEFGH1', confirmacao: 'ABCDEFGH1', aceito: false, mensagem: 'Senha fora do padrão', classe: 'sem minúscula' },
  { senha: 'Abcdefgh', confirmacao: 'Abcdefgh', aceito: false, mensagem: 'Senha fora do padrão', classe: 'sem número' },
  { senha: 'Abc 12345', confirmacao: 'Abc 12345', aceito: false, mensagem: 'Senha fora do padrão', classe: 'com espaço' },
  { senha: '', confirmacao: '', aceito: false, mensagem: 'Senha fora do padrão', classe: 'vazia' },
];

const casosValidos: CasoSenha[] = [
  {
    senha: 'Abcd1234',
    confirmacao: 'Abcd1234',
    aceito: true,
    mensagem: 'Senha cadastrada',
    classe: 'limite mínimo válido (8)',
  },
  {
    senha: 'Abcdefghij1234567890',
    confirmacao: 'Abcdefghij1234567890',
    aceito: true,
    mensagem: 'Senha cadastrada',
    classe: 'limite máximo válido (20)',
  },
  {
    senha: 'SenhaSegura123!',
    confirmacao: 'SenhaSegura123!',
    aceito: true,
    mensagem: 'Senha cadastrada',
    classe: 'senha forte válida',
  },
];

const casosConfirmacao: CasoSenha[] = [
  {
    senha: 'Abcd1234',
    confirmacao: 'Abcd1235',
    aceito: false,
    mensagem: 'As senhas não coincidem',
    classe: 'confirmação diferente',
  },
];

async function cadastrarSenha(page: import('@playwright/test').Page, senha: string, confirmacao: string) {
  await page.goto('/senha');
  await page.getByLabel('Nova senha').fill(senha);
  await page.getByLabel('Confirmar senha').fill(confirmacao);
  await page.getByRole('button', { name: 'Cadastrar senha' }).click();
}

for (const caso of [...casosFormatoInvalido, ...casosValidos, ...casosConfirmacao]) {
  test(`senha "${caso.senha || '(vazia)'}" — ${caso.classe}`, async ({ page }) => {
    await cadastrarSenha(page, caso.senha, caso.confirmacao);

    const resultado = page.locator('#resultado');
    await expect(resultado).toBeVisible();
    await expect(resultado).toHaveText(caso.mensagem);
    await expect(resultado).toHaveAttribute('role', caso.aceito ? 'status' : 'alert');

    if (caso.aceito) {
      await expect(resultado).toHaveClass(/success/);
      await expect(page.getByLabel('Nova senha')).toHaveValue('');
      await expect(page.getByLabel('Confirmar senha')).toHaveValue('');
    }
  });
}
