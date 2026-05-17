package br.com.MeuPlanner.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import br.com.MeuPlanner.model.Entrada;
import br.com.MeuPlanner.model.FechamentoMensal;
import br.com.MeuPlanner.model.Gasto;
import br.com.MeuPlanner.model.StatusMes;
import br.com.MeuPlanner.repository.EntradaRepository;
import br.com.MeuPlanner.repository.FechamentoMensalRepository;
import br.com.MeuPlanner.repository.GastoRepository;

public class FinanceiroService {

    private final EntradaRepository entradaRepo = new EntradaRepository();
    private final GastoRepository gastoRepo = new GastoRepository();
    private final FechamentoMensalRepository fechamentoRepo = new FechamentoMensalRepository();

    public void verificarFechamentoAutomatico() {
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        boolean jaFechado = fechamentoRepo.buscarPorMes(mesAnterior).isPresent();
        if (!jaFechado) {
            fecharMes(mesAnterior);
        }
    }

    public void fecharMes(YearMonth mes) {
        List<Entrada> entradas = entradaRepo.listarPorMes(mes);
        List<Gasto> gastos = gastoRepo.listarPorMes(mes);

        BigDecimal totalEntradas = entradas.stream()
                .map(Entrada::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGastos = gastos.stream()
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totalEntradas.subtract(totalGastos);

        FechamentoMensal fechamento = new FechamentoMensal(mes, totalEntradas, totalGastos, saldo);
        fechamento.setStatus(saldo.compareTo(BigDecimal.ZERO) >= 0 ? StatusMes.POSITIVO : StatusMes.NEGATIVO);
        fechamento.setDataFechamento(LocalDate.now());

        fechamentoRepo.salvar(fechamento);
    }

    public BigDecimal totalEntradas() {
        return entradaRepo.listarPorMes(YearMonth.now()).stream()
                .map(Entrada::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalGastos() {
        return gastoRepo.listarPorMes(YearMonth.now()).stream()
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}