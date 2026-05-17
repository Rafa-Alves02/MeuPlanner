package br.com.MeuPlanner.model;

import java.math.BigDecimal;
import java.time.YearMonth;

public class Alerta {

    private Long id;
    private String descricao;
    private Categoria categoria;
    private BigDecimal valorLimite;
    private YearMonth mesReferencia;
    private boolean disparado;

    public Alerta() {}

    public Alerta(String descricao, Categoria categoria,
                  BigDecimal valorLimite, YearMonth mesReferencia) {
        this.descricao = descricao;
        this.categoria = categoria;
        this.valorLimite = valorLimite;
        this.mesReferencia = mesReferencia;
        this.disparado = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public BigDecimal getValorLimite() { return valorLimite; }
    public void setValorLimite(BigDecimal valorLimite) { this.valorLimite = valorLimite; }

    public YearMonth getMesReferencia() { return mesReferencia; }
    public void setMesReferencia(YearMonth mesReferencia) { this.mesReferencia = mesReferencia; }

    public boolean isDisparado() { return disparado; }
    public void setDisparado(boolean disparado) { this.disparado = disparado; }
}