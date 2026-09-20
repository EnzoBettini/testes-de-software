package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemPedidoTest {

    @Test
    void deveCalcularTotalEstoqueDisponivel() {
        ItemPedido item = new ItemPedido("SKU-1", 2_500, 2, 5, 500, false);

        assertEquals(5_000L, item.totalCentavos());
        assertTrue(item.disponivel());
    }

    @Test
    void deveIndicarIndisponibilidadeQuandoQuantidadeExcedeEstoque() {
        ItemPedido item = new ItemPedido("SKU-2", 1_000, 3, 2, 300, true);

        assertFalse(item.disponivel());
    }

    @Test
    void deveRejeitarSkuInvalido() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("  ", 100, 1, 1, 100, false));
    }

    @Test
    void deveRejeitarPrecoInvalido() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 0, 1, 1, 100, false));
    }

    @Test
    void deveRejeitarQuantidadeInvalida() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 100, 101, 200, 100, false));
    }

    @Test
    void deveRejeitarPesoInvalido() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 100, 1, 1, 0, false));
    }
}
