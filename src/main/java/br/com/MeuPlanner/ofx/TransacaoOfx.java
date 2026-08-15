package br.com.MeuPlanner.ofx;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoOfx(String fitid, LocalDate data, BigDecimal valor, String descricao) {

    public boolean isEntrada() {
        return valor.signum() >= 0;
    }
}
