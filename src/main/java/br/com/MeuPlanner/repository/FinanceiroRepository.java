package br.com.MeuPlanner.repository;


import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.MeuPlanner.model.Entrada;
import br.com.MeuPlanner.model.FechamentoMensal;
import br.com.MeuPlanner.model.Gasto;


public class FinanceiroRepository {

    private static final List<Entrada> entradas = new ArrayList<>();
    private static final List<Gasto> gastos = new ArrayList<>();
    private static final List<FechamentoMensal> fechamentos = new ArrayList<>();

    public static void adicionarEntrada(Entrada entrada){
        entradas.add(entrada);
    }

    public static void adicionarGasto(Gasto gasto){

        gastos.add(gasto);
    }


    public static List<Entrada> getEntradasPorMes(YearMonth mes) {
        return entradas.stream()
                .filter(entrada -> YearMonth.from(entrada.getDataLancamento()).equals(mes))
                .collect(Collectors.toList());
    }

    public static List<Gasto> getGastosPorMes(YearMonth mes){
        return gastos.stream()
                .filter(gasto -> YearMonth.from(gasto.getDataLancamento()).equals(mes) )
                .collect(Collectors.toList());
    }


    public static void salvarFechamento(FechamentoMensal fechamento){
        fechamentos.add(fechamento);
    }

    public static List<FechamentoMensal> getFechamentos(){
        return fechamentos;
    }

}

