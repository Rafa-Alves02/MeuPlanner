package br.com.MeuPlanner.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ParcelamentoStrategy {
    List<Parcela> gerarParcelas(BigDecimal valorTotal, LocalDate dataInicio, int totalParcelas);
}
