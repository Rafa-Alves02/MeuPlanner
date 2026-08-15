package br.com.MeuPlanner.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import br.com.MeuPlanner.model.TipoGasto;
import br.com.MeuPlanner.model.TipoRecorrencia;

class LancamentoServiceTest {

    private final LancamentoService lancamentoService = new LancamentoService();

    @Test
    void rejeitaGastoComValorZeroOuNegativo() {
        assertThrows(IllegalArgumentException.class, () -> lancamentoService.adicionarGasto(
                "Mercado", BigDecimal.ZERO, LocalDate.now(),
                TipoGasto.VARIAVEL, TipoRecorrencia.UNICA, null, null));
    }

    @Test
    void rejeitaGastoSemConta() {
        assertThrows(IllegalArgumentException.class, () -> lancamentoService.adicionarGasto(
                "Mercado", BigDecimal.TEN, LocalDate.now(),
                TipoGasto.VARIAVEL, TipoRecorrencia.UNICA, null, null));
    }

    @Test
    void rejeitaEntradaComValorZeroOuNegativo() {
        assertThrows(IllegalArgumentException.class, () -> lancamentoService.adicionarEntrada(
                "Salário", new BigDecimal("-1"), LocalDate.now(), TipoRecorrencia.UNICA, null, null));
    }

    @Test
    void rejeitaGastoParceladoComTotalDeParcelasInvalido() {
        assertThrows(IllegalArgumentException.class, () -> lancamentoService.adicionarGastoParcelado(
                "Notebook", new BigDecimal("3000"), LocalDate.now(),
                TipoGasto.CONSUMO, 0, null, null));
    }
}
