package br.com.MeuPlanner.pluggy;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PluggyTransacao(String id, String descricao, BigDecimal valor, LocalDate data) {
}
