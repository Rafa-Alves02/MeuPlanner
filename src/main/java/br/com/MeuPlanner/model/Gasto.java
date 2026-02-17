package br.com.MeuPlanner.model;

import java.time.LocalDate;
import java.math.BigDecimal;

public class Gasto extends Movimentacao{

    private TipoGasto tipoGasto;
    private TipoRecorrencia tipoRecorrencia;
    private Integer totalParcelas;
    private Integer parcelaAtual;

    public Gasto(String descricao,
                 BigDecimal valor,
                 TipoGasto tipoGasto,
                 LocalDate dataLancamento,
                 TipoRecorrencia tipoRecorrencia,
                 Integer totalParcelas,
                 Integer parcelaAtual) {

        super(descricao, valor, dataLancamento);
        this.tipoGasto = tipoGasto;
        this.tipoRecorrencia = tipoRecorrencia;
        this.totalParcelas = totalParcelas;
        this.parcelaAtual = parcelaAtual;
    }

    public TipoGasto getTipoGasto(){
        return tipoGasto;
    }

    public TipoRecorrencia getTipoRecorrencia(){
        return tipoRecorrencia;
    }
}