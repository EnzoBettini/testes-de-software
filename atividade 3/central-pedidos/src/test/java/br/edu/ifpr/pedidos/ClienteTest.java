package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClienteTest {

    @Test
    void deveCriarClienteComHistoricoValido() {
        Cliente cliente = new Cliente(true, false, 3);

        assertEquals(true, cliente.vip());
        assertEquals(false, cliente.bloqueado());
        assertEquals(3, cliente.comprasAnteriores());
    }

    @Test
    void deveRejeitarHistoricoNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new Cliente(false, false, -1));
    }
}
