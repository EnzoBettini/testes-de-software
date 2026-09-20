package br.edu.ifpr.pedidos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PedidoServiceTest {

    @Test
    void deveFecharPedidoDeClienteComumComFreteDoParanaEPagamentoAprovado() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("PAGO", resultado.status()),
            () -> assertEquals(10_000L, resultado.subtotalCentavos()),
            () -> assertEquals(0L, resultado.descontoCentavos()),
            () -> assertEquals(1_200L, resultado.freteCentavos()),
            () -> assertEquals(11_200L, resultado.totalCentavos()),
            () -> assertEquals(List.of(11_200L), cobrancas)
        );
    }

    @Test
    void deveRetornarBloqueadoSemCalcularValores() {
        Cliente bloqueado = new Cliente(false, true, 0);
        Pedido pedido = pedidoSimples();
        AtomicInteger cobrancas = new AtomicInteger();
        PedidoService service = new PedidoService(total -> {
            cobrancas.incrementAndGet();
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, bloqueado);

        assertAll(
            () -> assertEquals("BLOQUEADO", resultado.status()),
            () -> assertEquals(0L, resultado.totalCentavos()),
            () -> assertEquals(0, cobrancas.get())
        );
    }

    @Test
    void deveRetornarSemEstoqueSemAplicarCupom() {
        Cliente cliente = new Cliente(false, false, 0);
        ItemPedido item = new ItemPedido("SKU", 10_000, 3, 1, 500, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, "BEMVINDO");
        AtomicInteger cobrancas = new AtomicInteger();
        PedidoService service = new PedidoService(total -> {
            cobrancas.incrementAndGet();
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("SEM_ESTOQUE", resultado.status()),
            () -> assertEquals(0L, resultado.descontoCentavos()),
            () -> assertEquals(0, cobrancas.get())
        );
    }

    @Test
    void deveEnviarParaRevisaoSemCobrar() {
        Cliente novo = new Cliente(false, false, 0);
        ItemPedido item = new ItemPedido("SKU", 50_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", true, null);
        AtomicInteger cobrancas = new AtomicInteger();
        PedidoService service = new PedidoService(total -> {
            cobrancas.incrementAndGet();
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, novo);

        assertAll(
            () -> assertEquals("REVISAO", resultado.status()),
            () -> assertEquals(50_000L, resultado.subtotalCentavos()),
            () -> assertEquals(0, cobrancas.get())
        );
    }

    @Test
    void deveRegistrarPagamentoRecusadoComValoresCalculados() {
        Cliente cliente = new Cliente(false, false, 2);
        Pedido pedido = pedidoSimples();
        PedidoService service = new PedidoService(total -> false);

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("PAGAMENTO_RECUSADO", resultado.status()),
            () -> assertEquals(11_200L, resultado.totalCentavos())
        );
    }

    @Test
    void deveRejeitarPedidoSemItensAtivos() {
        Cliente cliente = new Cliente(false, false, 0);
        ItemPedido inativo = new ItemPedido("SKU", 1_000, 0, 5, 100, false);
        Pedido pedido = new Pedido(List.of(inativo), "PR", false, null);
        PedidoService service = new PedidoService(total -> true);

        assertThrows(IllegalArgumentException.class, () -> service.fechar(pedido, cliente));
    }

    @Test
    void deveExigirReferenciasNaoNulas() {
        PedidoService service = new PedidoService(total -> true);
        Cliente cliente = new Cliente(false, false, 0);
        Pedido pedido = pedidoSimples();

        assertAll(
            () -> assertThrows(NullPointerException.class, () -> service.fechar(null, cliente)),
            () -> assertThrows(NullPointerException.class, () -> service.fechar(pedido, null))
        );
    }

    private static Pedido pedidoSimples() {
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        return new Pedido(List.of(item), "PR", false, null);
    }
}
