public interface ParcelamentoStrategy {
    java.util.List<Parcela> gerarParcelas(java.math.BigDecimal valorTotal, java.time.LocalDate dataInicio,
            int totalParcelas);
}