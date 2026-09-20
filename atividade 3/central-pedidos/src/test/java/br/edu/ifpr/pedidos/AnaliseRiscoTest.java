package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AnaliseRiscoTest {

    private final AnaliseRisco analise = new AnaliseRisco();

    @Test
    void deveRecusarClienteBloqueado() {
        Cliente bloqueado = new Cliente(false, true, 2);

        assertEquals("RECUSADO", analise.avaliar(bloqueado, 10_000, false));
    }

    @Test
    void deveEnviarNovoClienteParaRevisaoQuandoTotalAlto() {
        Cliente novo = new Cliente(false, false, 0);

        assertEquals("REVISAO", analise.avaliar(novo, 100_001, false));
    }

    @Test
    void deveEnviarNovoClienteParaRevisaoQuandoExpresso() {
        Cliente novo = new Cliente(false, false, 0);

        assertEquals("REVISAO", analise.avaliar(novo, 10_000, true));
    }

    @Test
    void deveAprovarNovoClienteDentroDoLimite() {
        Cliente novo = new Cliente(false, false, 0);

        assertEquals("APROVADO", analise.avaliar(novo, 100_000, false));
    }

    @Test
    void deveEnviarClienteComumRecorrenteParaRevisaoEmTotalAlto() {
        Cliente comum = new Cliente(false, false, 3);

        assertEquals("REVISAO", analise.avaliar(comum, 500_001, false));
    }

    @Test
    void deveAprovarClienteVipMesmoComTotalAlto() {
        Cliente vip = new Cliente(true, false, 3);

        assertEquals("APROVADO", analise.avaliar(vip, 600_000, false));
    }

    @Test
    void deveRejeitarTotalNegativo() {
        Cliente comum = new Cliente(false, false, 0);

        assertThrows(IllegalArgumentException.class, () -> analise.avaliar(comum, -1, false));
    }
}
