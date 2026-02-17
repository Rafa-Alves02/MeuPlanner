package br.com.MeuPlanner.repository;

import br.com.MeuPlanner.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.YearMonth;

public class FechamentoMensalRepository {

    private static final List<FechamentoMensal> fechamentos = new ArrayList<>();
    public static void salvar (FechamentoMensal fechamento){
        fechamentos.add(fechamento);
    }

    public static FechamentoMensal buscarPormes(YearMonth mes){
        return fechamentos.stream()
                .filter(f-> f.getMes().equals(mes))
                .findFirst()
                .orElse(null);

    }
   public static List<FechamentoMensal> listarTodos(){
        return fechamentos;
   }
}
