import { test, expect } from '@playwright/test';

type CasoFrete = {
  cep: string;
  valor: string;
  aceito: boolean;
  mensagem: string;
  classe: string;
};

const casosValidos: CasoFrete[] = [
  {
    cep: '80000000',
    valor: '100',
    aceito: true,
    mensagem: 'Frete: R$ 15,00',
    classe: 'CEP iniciado por 8',
  },
  {
    cep: '89999999',
    valor: '50,00',
    aceito: true,
    mensagem: 'Frete: R$ 15,00',
    classe: 'limite superior do prefixo 8',
  },
  {
    cep: '12345678',
    valor: '100',
    aceito: true,
    mensagem: 'Frete: R$ 25,00',
    classe: 'demais CEPs',
  },
  {
    cep: '70000000',
    valor: '199,99',
    aceito: true,
    mensagem: 'Frete: R$ 25,00',
    classe: 'abaixo do frete grátis',
  },
  {
    cep: '80000000',
    valor: '200',
    aceito: true,
    mensagem: 'Frete grátis',
    classe: 'limite mínimo frete grátis',
  },
  {
    cep: '12345678',
    valor: '200,00',
    aceito: true,
    mensagem: 'Frete grátis',
    classe: 'frete grátis independente do CEP',
  },
  {
    cep: '81234567',
    valor: '250',
    aceito: true,
    mensagem: 'Frete grátis',
    classe: 'acima do limite frete grátis',
  },
];

const casosInvalidos: CasoFrete[] = [
  { cep: '1234567', valor: '100', aceito: false, mensagem: 'Dados inválidos', classe: 'CEP com 7 dígitos' },
  { cep: '123456789', valor: '100', aceito: false, mensagem: 'Dados inválidos', classe: 'CEP com 9 dígitos' },
  { cep: '12ab5678', valor: '100', aceito: false, mensagem: 'Dados inválidos', classe: 'CEP não numérico' },
  { cep: '80000000', valor: '0', aceito: false, mensagem: 'Dados inválidos', classe: 'valor zero' },
  { cep: '80000000', valor: '0,00', aceito: false, mensagem: 'Dados inválidos', classe: 'valor zero formatado' },
  { cep: '80000000', valor: '', aceito: false, mensagem: 'Dados inválidos', classe: 'valor vazio' },
  { cep: '80000000', valor: 'abc', aceito: false, mensagem: 'Dados inválidos', classe: 'valor não numérico' },
  { cep: '', valor: '100', aceito: false, mensagem: 'Dados inválidos', classe: 'CEP vazio' },
];

async function calcularFrete(page: import('@playwright/test').Page, cep: string, valor: string) {
  await page.goto('/frete');
  await page.getByLabel('CEP').fill(cep);
  await page.getByLabel('Valor do pedido').fill(valor);
  await page.getByRole('button', { name: 'Calcular frete' }).click();
}

for (const caso of [...casosValidos, ...casosInvalidos]) {
  test(`frete CEP ${caso.cep || '(vazio)'} valor ${caso.valor || '(vazio)'} — ${caso.classe}`, async ({
    page,
  }) => {
    await calcularFrete(page, caso.cep, caso.valor);

    const resultado = page.locator('#resultado');
    await expect(resultado).toBeVisible();
    await expect(resultado).toHaveText(caso.mensagem);
    await expect(resultado).toHaveAttribute('role', caso.aceito ? 'status' : 'alert');
    if (caso.aceito) {
      await expect(resultado).toHaveClass(/success/);
    }
  });
}
