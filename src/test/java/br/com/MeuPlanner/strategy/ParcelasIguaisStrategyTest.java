package br.com.MeuPlanner.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class ParcelasIguaisStrategyTest {

    private final ParcelasIguaisStrategy strategy = new ParcelasIguaisStrategy();

    @Test
    void dividePartesIguaisQuandoValorDivideExatamente() {
        List<Parcela> parcelas = strategy.gerarParcelas(
                new BigDecimal("300.00"), LocalDate.of(2026, 1, 10), 3);

        assertEquals(3, parcelas.size());
        for (Parcela parcela : parcelas) {
            assertEquals(new BigDecimal("100.00"), parcela.valor());
        }
    }

    @Test
    void ultimaParcelaAbsorveRestoDoArredondamento() {
        List<Parcela> parcelas = strategy.gerarParcelas(
                new BigDecimal("100.00"), LocalDate.of(2026, 1, 10), 3);

        BigDecimal soma = parcelas.stream()
                .map(Parcela::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(new BigDecimal("100.00"), soma);
        assertEquals(new BigDecimal("33.34"), parcelas.get(2).valor());
    }

    @Test
    void datasAvancamUmMesPorParcela() {
        List<Parcela> parcelas = strategy.gerarParcelas(
                new BigDecimal("300.00"), LocalDate.of(2026, 1, 31), 3);

        assertEquals(LocalDate.of(2026, 1, 31), parcelas.get(0).data());
        assertEquals(LocalDate.of(2026, 2, 28), parcelas.get(1).data());
        assertEquals(LocalDate.of(2026, 3, 31), parcelas.get(2).data());
    }

    @Test
    void rejeitaTotalDeParcelasMenorOuIgualAZero() {
        assertThrows(IllegalArgumentException.class,
                () -> strategy.gerarParcelas(BigDecimal.TEN, LocalDate.now(), 0));
    }
}
