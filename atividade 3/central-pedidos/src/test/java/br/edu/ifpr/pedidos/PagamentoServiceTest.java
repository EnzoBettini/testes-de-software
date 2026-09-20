package br.edu.ifpr.pedidos;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PagamentoServiceTest {

    @Test
    void deveAprovarPagamentoNaPrimeiraTentativa() {
        PagamentoService service = new PagamentoService(total -> true);

        assertTrue(service.pagar(5_000, 3));
    }

    @Test
    void deveRetornarFalsoQuandoAutorizacaoNegada() {
        PagamentoService service = new PagamentoService(total -> false);

        assertFalse(service.pagar(5_000, 2));
    }

    @Test
    void deveRepetirAteLimiteQuandoProcessadorIndisponivel() {
        AtomicInteger tentativas = new AtomicInteger();
        PagamentoService service = new PagamentoService(total -> {
            tentativas.incrementAndGet();
            throw new IllegalStateException("indisponível");
        });

        assertFalse(service.pagar(5_000, 3));
        assertEquals(3, tentativas.get());
    }

    @Test
    void deveAprovarNaSegundaTentativaAposIndisponibilidade() {
        AtomicInteger tentativas = new AtomicInteger();
        PagamentoService service = new PagamentoService(total -> {
            if (tentativas.incrementAndGet() == 1) {
                throw new IllegalStateException("indisponível");
            }
            return true;
        });

        assertTrue(service.pagar(5_000, 3));
        assertEquals(2, tentativas.get());
    }

    @Test
    void deveRejeitarTotalInvalido() {
        PagamentoService service = new PagamentoService(total -> true);

        assertThrows(IllegalArgumentException.class, () -> service.pagar(0, 3));
    }

    @Test
    void deveRejeitarLimiteDeTentativasInvalido() {
        PagamentoService service = new PagamentoService(total -> true);

        assertThrows(IllegalArgumentException.class, () -> service.pagar(100, 4));
    }

    @Test
    void devePropagarExcecaoDiferenteDeIndisponibilidade() {
        PagamentoService service = new PagamentoService(total -> {
            throw new RuntimeException("falha inesperada");
        });

        assertThrows(RuntimeException.class, () -> service.pagar(100, 3));
    }
}
