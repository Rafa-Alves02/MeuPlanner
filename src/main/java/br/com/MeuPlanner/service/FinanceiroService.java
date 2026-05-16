package br.com.MeuPlanner.service;

import br.com.MeuPlanner.model.Entrada;
import br.com.MeuPlanner.model.FechamentoMensal;
import br.com.MeuPlanner.model.Gasto;
import br.com.MeuPlanner.repository.FinanceiroRepository;


import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;


public class FinanceiroService {

    private static YearMonth ultimoMesProcessado = null;



    public void verificarFechamentoAutomatico() {

        YearMonth mesAtual = YearMonth.now();

        if (ultimoMesProcessado == null) {
            ultimoMesProcessado = mesAtual;
            return;
        }

        if (!mesAtual.equals(ultimoMesProcessado)) {
            fecharMes(ultimoMesProcessado);
            ultimoMesProcessado = mesAtual;
        }
    }


    private void fecharMes(YearMonth mes){

        List<Entrada> entradas = FinanceiroRepository.getEntradasPorMes(mes);
        List<Gasto> gastos = FinanceiroRepository.getGastosPorMes(mes);

        BigDecimal totalEntradas = entradas.stream()
                .map(Entrada::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGastos = gastos.stream()
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal saldo = totalEntradas.subtract(totalGastos);

        FechamentoMensal fechamento = new FechamentoMensal(
                mes,
                totalEntradas,
                totalGastos,
                saldo
        );

        FinanceiroRepository.salvarFechamento(fechamento);
    }


    public BigDecimal totalEntradas() {
        YearMonth mesAtual = YearMonth.now();
        return FinanceiroRepository.getEntradasPorMes(mesAtual).stream()
                .map(Entrada::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalGastos() {
        YearMonth mesAtual = YearMonth.now();
        return FinanceiroRepository.getGastosPorMes(mesAtual).stream()
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



}
