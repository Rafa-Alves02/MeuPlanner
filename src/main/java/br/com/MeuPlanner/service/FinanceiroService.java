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



    public void verificarFechamentoAutomatico(){

       YearMonth mesAtual = YearMonth.now();
       YearMonth mesAberto = YearMonth.now();

        if (ultimoMesProcessado == null) {
            ultimoMesProcessado = mesAtual;
            return;
        }

        if(!mesAtual.equals(mesAberto)){
            fecharMes(mesAberto);
            mesAberto = mesAtual;
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



        FechamentoMensal fechamento = new FechamentoMensal(
                mes,
                totalEntradas,
                totalGastos,
                saldo
        );

        FinanceiroRepository.salvarFechamento(fechamento);
    }


    public BigDecimal totalEntradas() {
        return FinanceiroRespository.getEntradasPorMes().stream()
                .map(Entrada::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalGastos() {
        return FinanceiroRespository.getGastosPorMes().stream()
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



}
