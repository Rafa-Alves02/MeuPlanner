package br.com.MeuPlanner.model;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.time.temporal.TemporalAccessor;

public class Entrada extends Movimentacao{

    private TipoRecorrencia tipoRecorrencia;
    private Integer totalParcelas;
    private Integer parcelaAtual;


    public Entrada(String descricao,
                   BigDecimal valor,
                   LocalDate dataLancamento,
                   TipoRecorrencia tipoRecorrencia,
                   Integer totalParcelas,
                   Integer parcelaAtual) {

        super(descricao, valor, dataLancamento);
        this.tipoRecorrencia = tipoRecorrencia;
        this.totalParcelas = totalParcelas;
        this.parcelaAtual = parcelaAtual;
    }

    public TipoRecorrencia getTipoRecorrencia(){
        return tipoRecorrencia;
    }

}

