package br.com.MeuPlanner.repository;

import br.com.MeuPlanner.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.YearMonth;

public class GastoRepository {

    private static final List<Gasto> gastos = new ArrayList<>();

    public static void salvar(Gasto gasto){
        gastos.add(gasto);
    }

    public static List<Gasto> buscarPorNomes(YearMonth mes){
        return gastos.stream()
                .filter(g-> g.getMesReferencia().equals(mes))
                .collect(Collectors.toList());
    }

    public static List<Gasto> listarTodos(){
        return gastos;
    }
}
