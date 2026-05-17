package br.com.MeuPlanner.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public class FechamentoMensal {

    private Long id;
    private YearMonth mesReferencia;
    private BigDecimal totalEntradas;
    private BigDecimal totalGastos;
    private BigDecimal saldoFinal;
    private StatusMes status;
    private LocalDate dataFechamento;

    public FechamentoMensal() {}

    public FechamentoMensal(YearMonth mesReferencia, BigDecimal totalEntradas,
                            BigDecimal totalGastos, BigDecimal saldoFinal) {
        this.mesReferencia = mesReferencia;
        this.totalEntradas = totalEntradas;
        this.totalGastos = totalGastos;
        this.saldoFinal = saldoFinal;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public YearMonth getMesReferencia() { return mesReferencia; }
    public void setMesReferencia(YearMonth mesReferencia) { this.mesReferencia = mesReferencia; }

    public BigDecimal getTotalEntradas() { return totalEntradas; }
    public void setTotalEntradas(BigDecimal totalEntradas) { this.totalEntradas = totalEntradas; }

    public BigDecimal getTotalGastos() { return totalGastos; }
    public void setTotalGastos(BigDecimal totalGastos) { this.totalGastos = totalGastos; }

    public BigDecimal getSaldoFinal() { return saldoFinal; }
    public void setSaldoFinal(BigDecimal saldoFinal) { this.saldoFinal = saldoFinal; }

    public StatusMes getStatus() { return status; }
    public void setStatus(StatusMes status) { this.status = status; }

    public LocalDate getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(LocalDate dataFechamento) { this.dataFechamento = dataFechamento; }
}