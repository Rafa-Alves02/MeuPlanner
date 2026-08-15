package br.com.MeuPlanner.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class MetaServiceTest {

    private final MetaService metaService = new MetaService();

    @Test
    void rejeitaValorAlvoZeroOuNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> metaService.criarMeta("Viagem", BigDecimal.ZERO, LocalDate.now().plusMonths(1), null));
    }

    @Test
    void rejeitaDataLimiteNoPassado() {
        assertThrows(IllegalArgumentException.class,
                () -> metaService.criarMeta("Viagem", BigDecimal.TEN, LocalDate.now().minusDays(1), null));
    }

    @Test
    void rejeitaDataLimiteNula() {
        assertThrows(IllegalArgumentException.class,
                () -> metaService.criarMeta("Viagem", BigDecimal.TEN, null, null));
    }
}
