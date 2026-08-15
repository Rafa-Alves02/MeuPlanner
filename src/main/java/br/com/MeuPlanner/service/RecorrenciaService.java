package br.com.MeuPlanner.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import br.com.MeuPlanner.model.Entrada;
import br.com.MeuPlanner.model.Gasto;
import br.com.MeuPlanner.model.TipoRecorrencia;
import br.com.MeuPlanner.repository.EntradaRepository;
import br.com.MeuPlanner.repository.GastoRepository;

public class RecorrenciaService {

    private final EntradaRepository entradaRepo = new EntradaRepository();
    private final GastoRepository gastoRepo = new GastoRepository();
    private final LancamentoService lancamentoService = new LancamentoService();

    /**
     * Chamado ao iniciar o app — verifica se já existem lançamentos
     * recorrentes do mês atual e os cria automaticamente se não existirem.
     */
    public void processarRecorrenciasDoMes() {
        YearMonth mesAtual = YearMonth.now();

        processarEntradasRecorrentes(mesAtual);
        processarGastosRecorrentes(mesAtual);
    }

    private void processarEntradasRecorrentes(YearMonth mesAtual) {
        List<Entrada> recorrentes = entradaRepo.listarRecorrentes();
        List<Entrada> lancadasNoMes = entradaRepo.listarPorMes(mesAtual);

        for (Entrada original : recorrentes) {
            // Verifica se já existe lançamento desse recorrente no mês atual
            boolean jaLancado = lancadasNoMes.stream()
                    .anyMatch(e -> e.getDescricao().equals(original.getDescricao())
                            && e.getValor().compareTo(original.getValor()) == 0
                            && e.getTipoRecorrencia() == TipoRecorrencia.RECORRENTE);

            if (!jaLancado) {
                lancamentoService.adicionarEntrada(
                        original.getDescricao(),
                        original.getValor(),
                        LocalDate.of(mesAtual.getYear(), mesAtual.getMonth(), 1),
                        TipoRecorrencia.RECORRENTE,
                        original.getConta(),
                        original.getCategoria()
                );
            }
        }
    }

    private void processarGastosRecorrentes(YearMonth mesAtual) {
        List<Gasto> recorrentes = gastoRepo.listarRecorrentes();
        List<Gasto> lancadosNoMes = gastoRepo.listarPorMes(mesAtual);

        for (Gasto original : recorrentes) {
            boolean jaLancado = lancadosNoMes.stream()
                    .anyMatch(g -> g.getDescricao().equals(original.getDescricao())
                            && g.getValor().compareTo(original.getValor()) == 0
                            && g.getTipoRecorrencia() == TipoRecorrencia.RECORRENTE);

            if (!jaLancado) {
                lancamentoService.adicionarGasto(
                        original.getDescricao(),
                        original.getValor(),
                        LocalDate.of(mesAtual.getYear(), mesAtual.getMonth(), 1),
                        original.getTipoGasto(),
                        TipoRecorrencia.RECORRENTE,
                        original.getConta(),
                        original.getCategoria()
                );
            }
        }
    }
}