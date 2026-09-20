# Relatório do grupo

Integrantes: Enzo Ayres Bettini — RA 241955272

## Grafos e complexidade

Modelo adotado: cada decisão (`if`, `switch`, `while`, `for`, `&&`, `||`) gera arestas para ramos verdadeiro/falso; exceções não viram branch no JaCoCo, mas foram testadas com `assertThrows`. Curto-circuito: quando o operando esquerdo de `&&`/`||` define o resultado, o direito não é avaliado (não há nó separado no bytecode para o operando direito não executado).

Grafo de chamadas principal de `PedidoService.fechar`:

`fechar` → `Pedido.subtotalCentavos` → `Pedido.estoqueSuficiente` → `PoliticaDesconto.calcular` → `CalculadoraFrete.calcular` → `AnaliseRisco.avaliar` → `PagamentoService.pagar` → `ProcessadorPagamento.autorizar`.

| Método | Nós | Arestas | V(G) | Caminhos independentes | Restrições de viabilidade |
| --- | --- | --- | --- | --- | --- |
| `PoliticaDesconto.calcular` | 14 | 18 | 6 | VIP / comum elegível / comum sem desconto; cupom vazio; BEMVINDO; EXTRA10; cupom inválido; teto 20% | Cupom inválido só após passar regra base |
| `CalculadoraFrete.calcular` | 16 | 20 | 6 | UF PR / SP-RJ / default; laço peso 0 / 1 / 2+ iterações; frete grátis; VIP; expresso; frágil | Combinações com frete zerado mantêm adicionais |
| `AnaliseRisco.avaliar` | 10 | 12 | 4 | bloqueado; novo + total alto; novo + expresso; recorrente alto não VIP; aprovado | Cliente bloqueado em `PedidoService` impede caminho `RECUSADO` no fechamento |
| `PagamentoService.pagar` | 9 | 11 | 4 | sucesso 1ª; recusa; indisponibilidade esgotada; sucesso após retry | Outras exceções não entram no `catch` |
| `PedidoService.fechar` | 15 | 19 | 6 | BLOQUEADO; SEM_ESTOQUE; REVISAO; PAGO; PAGAMENTO_RECUSADO; exceção subtotal zero | `BLOQUEADO` retorna antes de cupom e frete |

## Matriz de testes

| ID / método JUnit | Unidade | Entrada e estado do stub | Resultado esperado | Caminho / aresta | Critério atendido |
| --- | --- | --- | --- | --- | --- |
| `PoliticaDescontoTest.deveAplicarDezPorcentoParaClienteVip` | Desconto | VIP, subtotal 50_000 | 5_000 | ramo VIP | equivalência válida |
| `PoliticaDescontoTest.deveLimitarDescontoCombinadoAoTetoDeVintePorcento` | Desconto | VIP + EXTRA10 | teto 4_000 | combinação + teto | valor-limite |
| `CalculadoraFreteTest.deveAcrescentarAdicionalPorPesoExcedente` | Frete | 3_001 g | +600 sobre base PR | `while` 2x | iteração múltipla |
| `CalculadoraFreteTest.deveManterAdicionalDeExpressoMesmoComFreteGratis` | Frete | líquido 35_000, expresso | 2_700 | frete grátis + expresso | decisões independentes |
| `AnaliseRiscoTest.deveEnviarNovoClienteParaRevisaoQuandoExpresso` | Risco | compras 0, expresso | REVISAO | ramo novo cliente | classe inválida / revisão |
| `PagamentoServiceTest.deveRepetirAteLimiteQuandoProcessadorIndisponivel` | Pagamento | 3× IllegalStateException | false, 3 chamadas | `do/while` esgotado | laço |
| `PedidoServiceTest.deveRetornarBloqueadoSemCalcularValores` | Serviço | cliente bloqueado | BLOQUEADO, cobrança 0 | retorno antecipado | colaboração |
| `PedidoServiceTest.deveEnviarParaRevisaoSemCobrar` | Serviço | cliente novo + expresso | REVISAO, cobrança 0 | pós-frete, pré-pagamento | efeito colateral |
| `PedidoTest.deveIgnorarItensInativosNoSubtotal` | Pedido | quantidade 0 | subtotal ignora linha | `continue` no `for` | caixa-preta/estrutural |

## Evolução da cobertura

| Etapa | Testes executados | Linhas | Branches | Métodos | Classes | Lacunas e justificativas |
| --- | --- | --- | --- | --- | --- | --- |
| Inicial (exemplo) | 1 | ~35% | ~25% | parcial | parcial | Apenas `PedidoServiceTest` de exemplo |
| Unidades de domínio | 35 | ~85% | ~75% | ~90% | 100% | `Cliente`, `ItemPedido`, `Pedido` |
| Regras isoladas | 45 | ~95% | ~85% | 100% | 100% | Desconto, frete, risco, pagamento |
| Colaboração (`PedidoService`) | 52 | **99,1%** | **88,8%** | **100%** | **100%** | 1 linha e 13 branches não alcançados (UF SP/RJ agrupada parcialmente, ramos de switch redundantes) |

Valores obtidos com `mvn clean test` e relatório em `target/site/jacoco/index.html`.

## Análise crítica

- **Ramos cobertos ≠ caminhos completos:** em `CalculadoraFrete`, cobrir UF PR, MG e ramos de expresso/VIP não exercita todas as combinações simultâneas (por exemplo, VIP + frágil + peso excedente + frete grátis). A suíte priorizou caminhos independentes e limites de negócio.
- **Curto-circuito:** em `PoliticaDesconto`, clientes VIP não avaliam o ramo `subtotal >= 50_000` do `else if`; testes separados garantem cada operando.
- **Caminho inviável via serviço:** `AnaliseRisco` retorna `RECUSADO` para bloqueado, mas `PedidoService` encerra antes com `BLOQUEADO`. O ramo `RECUSADO` foi validado em teste unitário de `AnaliseRisco`, não no fechamento integrado.
- **Exceções:** `IllegalArgumentException` e `NullPointerException` foram testadas; o JaCoCo não contabiliza arestas de exceção — mesmo assim há testes dedicados (`deveRejeitarSubtotalNegativo`, `devePropagarExcecaoDiferenteDeIndisponibilidade`).
- **Mutação proposital:** alteramos temporariamente o limiar de frete grátis de `30_000` para `29_999` em `CalculadoraFrete`; `CalculadoraFreteTest.deveZerarFreteBaseQuandoLiquidoElegivelENaoExpresso` falhou; alteração revertida antes da entrega.

## Referências

- JUnit 5 User Guide
- JaCoCo counters e flow analysis (branches vs paths)
