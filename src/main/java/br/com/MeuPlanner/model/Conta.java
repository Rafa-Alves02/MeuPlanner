package br.com.MeuPlanner.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Conta {
    public enum TipoConta {
        CORRENTE, POUPANCA, CARTEIRA, INVESTIMENTO
    }

    private Long id;
    private String nome;
    private TipoConta tipo;
    private String banco;
    private BigDecimal saldoInicial;
    private BigDecimal saldoAtual;
    private LocalDate criadoEm;
    private String pluggyItemId;
    private String pluggyAccountId;

    public Conta() {
    }

    public Conta(String nome, TipoConta tipo, String banco, BigDecimal saldoInicial) {
        this.nome = nome;
        this.tipo = tipo;
        this.banco = banco;
        this.saldoInicial = saldoInicial;
        this.saldoAtual = saldoInicial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoConta getTipo() {
        return tipo;
    }

    public void setTipo(TipoConta tipo) {
        this.tipo = tipo;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
    }

    public LocalDate getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDate criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getPluggyItemId() {
        return pluggyItemId;
    }

    public void setPluggyItemId(String pluggyItemId) {
        this.pluggyItemId = pluggyItemId;
    }

    public String getPluggyAccountId() {
        return pluggyAccountId;
    }

    public void setPluggyAccountId(String pluggyAccountId) {
        this.pluggyAccountId = pluggyAccountId;
    }

    public boolean isConectadaAoOpenFinance() {
        return pluggyAccountId != null && !pluggyAccountId.isBlank();
    }

    @Override
    public String toString() {
        return nome + "(" + tipo + ") - R$ " + saldoAtual;
    }

}