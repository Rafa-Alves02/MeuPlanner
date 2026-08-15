package br.com.MeuPlanner.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Parcela(int numero, int totalParcelas, LocalDate data, BigDecimal valor) {
}
