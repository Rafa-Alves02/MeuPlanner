package br.com.MeuPlanner.repository;

import br.com.MeuPlanner.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.YearMonth;

public class EntradaRepository {
    private static final List<Entrada> entradas = new ArrayList<>();

    public static List <Entrada> buscarPorMes(YearMonth mes){
        return  entradas.stream()
                .filter(e-> e.getMesReferencia().equals(mes))
                .collect(Collectors.toList());
    }

    public static List<Entrada>listarTodas(){
        return entradas;
    }
}
