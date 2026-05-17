package br.com.MeuPlanner.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public class Entrada {

    private Long id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate dataLancamento;
    private YearMonth mesReferencia;
    private TipoRecorrencia tipoRecorrencia;
    private Integer parcelaAtual;
    private Integer totalParcelas;
    private Conta conta;
    private Categoria categoria;

    public Entrada() {}

    public Entrada(String descricao, BigDecimal valor, LocalDate dataLancamento,
                   TipoRecorrencia tipoRecorrencia, Conta conta, Categoria categoria) {
        this.descricao = descricao;
        this.valor = valor;
        this.dataLancamento = dataLancamento;
        this.mesReferencia = YearMonth.from(dataLancamento);
        this.tipoRecorrencia = tipoRecorrencia;
        this.conta = conta;
        this.categoria = categoria;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getDataLancamento() { return dataLancamento; }
    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
        this.mesReferencia = YearMonth.from(dataLancamento);
    }

    public YearMonth getMesReferencia() { return mesReferencia; }
    public void setMesReferencia(YearMonth mesReferencia) { this.mesReferencia = mesReferencia; }

    public TipoRecorrencia getTipoRecorrencia() { return tipoRecorrencia; }
    public void setTipoRecorrencia(TipoRecorrencia tipoRecorrencia) { this.tipoRecorrencia = tipoRecorrencia; }

    public Integer getParcelaAtual() { return parcelaAtual; }
    public void setParcelaAtual(Integer parcelaAtual) { this.parcelaAtual = parcelaAtual; }

    public Integer getTotalParcelas() { return totalParcelas; }
    public void setTotalParcelas(Integer totalParcelas) { this.totalParcelas = totalParcelas; }

    public Conta getConta() { return conta; }
    public void setConta(Conta conta) { this.conta = conta; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}