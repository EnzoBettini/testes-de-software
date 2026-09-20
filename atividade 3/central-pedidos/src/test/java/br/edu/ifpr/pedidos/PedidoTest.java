package br.edu.ifpr.pedidos;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PedidoTest {

    @Test
    void deveIgnorarItensInativosNoSubtotal() {
        ItemPedido ativo = new ItemPedido("A", 1_000, 2, 5, 500, false);
        ItemPedido inativo = new ItemPedido("B", 9_000, 0, 5, 500, false);
        Pedido pedido = new Pedido(List.of(ativo, inativo), "PR", false, null);

        assertEquals(2_000L, pedido.subtotalCentavos());
        assertEquals(1_000, pedido.pesoGramas());
    }

    @Test
    void deveDetectarFragilidadeSomenteEmItensAtivos() {
        ItemPedido fragilInativo = new ItemPedido("F", 1_000, 0, 1, 100, true);
        ItemPedido normal = new ItemPedido("N", 1_000, 1, 1, 100, false);
        Pedido pedido = new Pedido(List.of(fragilInativo, normal), "PR", false, null);

        assertFalse(pedido.temFragil());
    }

    @Test
    void deveInterromperVerificacaoDeEstoqueNoPrimeiroItemIndisponivel() {
        ItemPedido indisponivel = new ItemPedido("I", 1_000, 5, 1, 100, false);
        ItemPedido disponivel = new ItemPedido("D", 1_000, 1, 10, 100, false);
        Pedido pedido = new Pedido(List.of(indisponivel, disponivel), "PR", false, null);

        assertFalse(pedido.estoqueSuficiente());
    }

    @Test
    void deveCopiarListaDefensivamente() {
        List<ItemPedido> itens = new ArrayList<>();
        itens.add(new ItemPedido("A", 1_000, 1, 1, 100, false));
        Pedido pedido = new Pedido(itens, "SP", false, null);
        itens.clear();

        assertEquals(1_000L, pedido.subtotalCentavos());
    }

    @Test
    void deveRejeitarUfInvalida() {
        ItemPedido item = new ItemPedido("A", 1_000, 1, 1, 100, false);
        assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(item), "Parana", false, null));
    }

    @Test
    void deveRejeitarListaNula() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(null, "PR", false, null));
    }
}
