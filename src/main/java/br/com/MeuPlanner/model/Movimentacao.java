package br.com.MeuPlanner.model;



import java.math.*;
import java.time.*;
import java.time.YearMonth;

public abstract class Movimentacao {

    protected Long id;
    protected String descricao;
    protected BigDecimal valor;
    protected LocalDate dataLancamento;
    protected YearMonth mesReferencia;


    protected Movimentacao(String descricao, BigDecimal valor, LocalDate dataLancamento){
        this.descricao = descricao;
        this.valor = valor;
        this.dataLancamento = dataLancamento;
        this.mesReferencia = YearMonth.from(dataLancamento);
    }

    public BigDecimal getValor() {
        return valor;
    }

    public YearMonth getMesReferencia(){
        return mesReferencia;
    }

    public String getDescricao(){
        return descricao;
    }
    public LocalDate getDataLancamento(){
        return  dataLancamento;
    }

}
