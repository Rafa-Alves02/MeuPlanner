package br.com.MeuPlanner.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Entrada extends Movimentacao {

    public Entrada() {
        super();
    }

    public Entrada(String descricao, BigDecimal valor, LocalDate dataLancamento,
                   TipoRecorrencia tipoRecorrencia, Conta conta, Categoria categoria) {
        super(descricao, valor, dataLancamento, tipoRecorrencia, conta, categoria);
    }

    @Override
    public int sinalNoSaldo() {
        return 1;
    }

    @Override
    public String tipoMovimentacao() {
        return "Entrada";
    }
}