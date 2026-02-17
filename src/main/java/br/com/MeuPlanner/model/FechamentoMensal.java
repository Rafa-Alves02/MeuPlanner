package br.com.MeuPlanner.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public class FechamentoMensal {

    private Long id;
    private YearMonth mes;
    private BigDecimal totalEntradas;
    private BigDecimal totalGastos;
    private BigDecimal saldoFinal;
    private StatusMes status;
    private LocalDate dataFechamento;

    public FechamentoMensal(YearMonth mes,
                            BigDecimal totalEntradas,
                            BigDecimal totalGastos,
                            BigDecimal saldoFinal) {

        this.mes = mes;
        this.totalEntradas = totalEntradas;
        this.totalGastos = totalGastos;
        this.saldoFinal = saldoFinal;
        this.status = saldoFinal.signum() >= 0
                ? StatusMes.POSITIVO
                : StatusMes.NEGATIVO;
        this.dataFechamento = LocalDate.now();
    }

    public YearMonth getMes() {
        return mes;
    }

    public BigDecimal getTotalEntradas() {
        return totalEntradas;
    }

    public BigDecimal getTotalGastos() {
        return totalGastos;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public StatusMes getStatus() {
        return status;
    }

    public LocalDate getDataFechamento() {
        return dataFechamento;
    }
}
