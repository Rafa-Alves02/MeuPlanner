package br.com.MeuPlanner.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Divide o valor total em N parcelas iguais. A ÚLTIMA parcela absorve o
 * resto do arredondamento, garantindo que a soma bata exatamente com o total.
 */
public class ParcelasIguaisStrategy implements ParcelamentoStrategy {

    @Override
    public List<Parcela> gerarParcelas(BigDecimal valorTotal, LocalDate dataInicio, int totalParcelas) {
        if (totalParcelas <= 0) {
            throw new IllegalArgumentException("Total de parcelas deve ser maior que zero!");
        }

        BigDecimal valorParcela = valorTotal.divide(BigDecimal.valueOf(totalParcelas), 2, RoundingMode.HALF_UP);
        BigDecimal somaParcial = valorParcela.multiply(BigDecimal.valueOf(totalParcelas - 1L));
        BigDecimal ultimaParcela = valorTotal.subtract(somaParcial);

        List<Parcela> parcelas = new ArrayList<>(totalParcelas);
        for (int i = 1; i <= totalParcelas; i++) {
            LocalDate data = dataInicio.plusMonths(i - 1L);
            BigDecimal valor = (i == totalParcelas) ? ultimaParcela : valorParcela;
            parcelas.add(new Parcela(i, totalParcelas, data, valor));
        }
        return parcelas;
    }
}