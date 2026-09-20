package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PoliticaDescontoTest {

    private final PoliticaDesconto politica = new PoliticaDesconto();

    @Test
    void deveAplicarDezPorcentoParaClienteVip() {
        Cliente vip = new Cliente(true, false, 2);

        assertEquals(5_000L, politica.calcular(vip, 50_000, null));
    }

    @Test
    void deveAplicarCincoPorcentoParaClienteComumComSubtotalMinimo() {
        Cliente comum = new Cliente(false, false, 1);

        assertEquals(2_500L, politica.calcular(comum, 50_000, null));
    }

    @Test
    void naoDeveConcederDescontoBaseParaSubtotalBaixo() {
        Cliente comum = new Cliente(false, false, 1);

        assertEquals(0L, politica.calcular(comum, 49_999, null));
    }

    @Test
    void deveSomarCupomBemvindoQuandoElegivel() {
        Cliente novo = new Cliente(false, false, 0);

        assertEquals(2_000L, politica.calcular(novo, 10_000, "BEMVINDO"));
    }

    @Test
    void deveIgnorarCupomBemvindoSemElegibilidade() {
        Cliente comHistorico = new Cliente(false, false, 1);

        assertEquals(0L, politica.calcular(comHistorico, 10_000, " bemvindo "));
    }

    @Test
    void deveSomarExtra10QuandoSubtotalElegivel() {
        Cliente comum = new Cliente(false, false, 1);

        assertEquals(2_000L, politica.calcular(comum, 20_000, "EXTRA10"));
    }

    @Test
    void deveRejeitarCupomDesconhecido() {
        Cliente comum = new Cliente(false, false, 0);

        assertThrows(IllegalArgumentException.class, () -> politica.calcular(comum, 20_000, "INVALIDO"));
    }

    @Test
    void deveLimitarDescontoCombinadoAoTetoDeVintePorcento() {
        Cliente vip = new Cliente(true, false, 0);

        assertEquals(4_000L, politica.calcular(vip, 20_000, "EXTRA10"));
    }

    @Test
    void deveRejeitarSubtotalNegativo() {
        Cliente comum = new Cliente(false, false, 0);

        assertThrows(IllegalArgumentException.class, () -> politica.calcular(comum, -1, null));
    }
}
