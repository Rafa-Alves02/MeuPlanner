package br.com.MeuPlanner.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Meta {

    private Long id;
    private String descricao;
    private BigDecimal valorAlvo;
    private BigDecimal valorAtual;
    private LocalDate dataLimite;
    private Conta conta;
    private boolean concluida;

    public Meta() {}

    public Meta(String descricao, BigDecimal valorAlvo, LocalDate dataLimite, Conta conta) {
        this.descricao = descricao;
        this.valorAlvo = valorAlvo;
        this.valorAtual = BigDecimal.ZERO;
        this.dataLimite = dataLimite;
        this.conta = conta;
        this.concluida = false;
    }

    public double getProgresso() {
        if (valorAlvo == null || valorAlvo.compareTo(BigDecimal.ZERO) == 0) return 0;
        return valorAtual.divide(valorAlvo, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getValorAlvo() { return valorAlvo; }
    public void setValorAlvo(BigDecimal valorAlvo) { this.valorAlvo = valorAlvo; }

    public BigDecimal getValorAtual() { return valorAtual; }
    public void setValorAtual(BigDecimal valorAtual) { this.valorAtual = valorAtual; }

    public LocalDate getDataLimite() { return dataLimite; }
    public void setDataLimite(LocalDate dataLimite) { this.dataLimite = dataLimite; }

    public Conta getConta() { return conta; }
    public void setConta(Conta conta) { this.conta = conta; }

    public boolean isConcluida() { return concluida; }
    public void setConcluida(boolean concluida) { this.concluida = concluida; }
}