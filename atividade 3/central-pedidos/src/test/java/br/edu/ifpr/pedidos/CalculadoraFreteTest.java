package br.edu.ifpr.pedidos;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculadoraFreteTest {

    private final CalculadoraFrete calculadora = new CalculadoraFrete();

    @Test
    void deveAplicarTarifaDoParanaSemExcedenteDePeso() {
        Pedido pedido = pedido("PR", false, item(1_000, 1, 2_000, false));
        Cliente comum = new Cliente(false, false, 1);

        assertEquals(1_200L, calculadora.calcular(pedido, comum, 10_000));
    }

    @Test
    void deveAplicarTarifaPadraoParaUfForaDoGrupoPrincipal() {
        Pedido pedido = pedido("MG", false, item(1_000, 1, 2_000, false));
        Cliente comum = new Cliente(false, false, 1);

        assertEquals(3_000L, calculadora.calcular(pedido, comum, 10_000));
    }

    @Test
    void deveAcrescentarAdicionalPorPesoExcedente() {
        Pedido pedido = pedido("PR", false, item(1_000, 1, 3_001, false));
        Cliente comum = new Cliente(false, false, 1);

        assertEquals(1_800L, calculadora.calcular(pedido, comum, 10_000));
    }

    @Test
    void deveZerarFreteBaseQuandoLiquidoElegivelENaoExpresso() {
        Pedido pedido = pedido("PR", false, item(1_000, 1, 5_000, false));
        Cliente comum = new Cliente(false, false, 1);

        assertEquals(0L, calculadora.calcular(pedido, comum, 30_000));
    }

    @Test
    void deveAplicarMetadeDoFreteParaClienteVip() {
        Pedido pedido = pedido("PR", false, item(1_000, 1, 2_000, false));
        Cliente vip = new Cliente(true, false, 1);

        assertEquals(600L, calculadora.calcular(pedido, vip, 10_000));
    }

    @Test
    void deveManterAdicionalDeExpressoMesmoComFreteGratis() {
        Pedido pedido = pedido("PR", true, item(1_000, 1, 2_000, false));
        Cliente comum = new Cliente(false, false, 1);

        assertEquals(2_700L, calculadora.calcular(pedido, comum, 35_000));
    }

    @Test
    void deveAcrescentarTaxaUnicaDeFragilidade() {
        Pedido pedido = pedido("PR", false, item(1_000, 1, 2_000, true));
        Cliente comum = new Cliente(false, false, 1);

        assertEquals(1_700L, calculadora.calcular(pedido, comum, 10_000));
    }

    @Test
    void deveRejeitarLiquidoNegativo() {
        Pedido pedido = pedido("PR", false, item(1_000, 1, 2_000, false));
        Cliente comum = new Cliente(false, false, 0);

        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(pedido, comum, -1));
    }

    private static Pedido pedido(String uf, boolean expresso, ItemPedido item) {
        return new Pedido(List.of(item), uf, expresso, null);
    }

    private static ItemPedido item(long preco, int quantidade, int pesoGramas, boolean fragil) {
        return new ItemPedido("SKU", preco, quantidade, 10, pesoGramas, fragil);
    }
}
