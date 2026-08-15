package br.com.MeuPlanner.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Gasto extends Movimentacao {

    private TipoGasto tipoGasto;

    public Gasto() {
        super();
    }

    public Gasto(String descricao, BigDecimal valor, LocalDate dataLancamento,
                 TipoGasto tipoGasto, TipoRecorrencia tipoRecorrencia,
                 Conta conta, Categoria categoria) {
        super(descricao, valor, dataLancamento, tipoRecorrencia, conta, categoria);
        this.tipoGasto = tipoGasto;
    }

    public TipoGasto getTipoGasto() { return tipoGasto; }
    public void setTipoGasto(TipoGasto tipoGasto) { this.tipoGasto = tipoGasto; }

    @Override
    public int sinalNoSaldo() {
        return -1;
    }

    @Override
    public String tipoMovimentacao() {
        return "Gasto";
    }
}