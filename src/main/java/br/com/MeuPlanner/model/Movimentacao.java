package br.com.MeuPlanner.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Superclasse de toda movimentação financeira (Entrada ou Gasto).
 * Concentra os campos e comportamentos comuns — quem antes era duplicado
 * entre Entrada e Gasto agora vive aqui uma vez só (reaproveitamento por herança).
 */
public abstract class Movimentacao {

    protected Long id;
    protected String descricao;
    protected BigDecimal valor;
    protected LocalDate dataLancamento;
    protected YearMonth mesReferencia;
    protected TipoRecorrencia tipoRecorrencia;
    protected Integer parcelaAtual;
    protected Integer totalParcelas;
    protected Conta conta;
    protected Categoria categoria;

    protected Movimentacao() {}

    protected Movimentacao(String descricao, BigDecimal valor, LocalDate dataLancamento,
                            TipoRecorrencia tipoRecorrencia, Conta conta, Categoria categoria) {
        this.descricao = descricao;
        this.valor = valor;
        this.dataLancamento = dataLancamento;
        this.mesReferencia = dataLancamento != null ? YearMonth.from(dataLancamento) : null;
        this.tipoRecorrencia = tipoRecorrencia;
        this.conta = conta;
        this.categoria = categoria;
    }

    /** Sinal do impacto no saldo da conta: +1 para Entrada, -1 para Gasto. */
    public abstract int sinalNoSaldo();

    /** Rótulo usado em telas/relatórios ("Entrada" ou "Gasto"). */
    public abstract String tipoMovimentacao();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getDataLancamento() { return dataLancamento; }
    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
        this.mesReferencia = dataLancamento != null ? YearMonth.from(dataLancamento) : null;
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
