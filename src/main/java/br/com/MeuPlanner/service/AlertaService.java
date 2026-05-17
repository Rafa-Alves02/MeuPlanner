package br.com.MeuPlanner.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import br.com.MeuPlanner.model.Alerta;
import br.com.MeuPlanner.model.Categoria;
import br.com.MeuPlanner.repository.AlertaRepository;
import br.com.MeuPlanner.repository.GastoRepository;

public class AlertaService {

    private final AlertaRepository alertaRepo = new AlertaRepository();
    private final GastoRepository gastoRepo = new GastoRepository();

    public Alerta criarAlerta(String descricao, Categoria categoria,
                               BigDecimal valorLimite, YearMonth mes) {
        if (valorLimite == null || valorLimite.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Valor limite deve ser maior que zero!");

        Alerta alerta = new Alerta(descricao, categoria, valorLimite, mes);
        alertaRepo.salvar(alerta);
        return alerta;
    }

    public void deletar(Long id) {
        alertaRepo.deletar(id);
    }

    /**
     * Verifica todos os alertas do mês e retorna os que foram ultrapassados.
     * Marca automaticamente como disparados no banco.
     */
    public List<Alerta> verificarAlertas(YearMonth mes) {
        List<Alerta> alertasAtivos = alertaRepo.listarNaoDisparados(mes);
        List<Alerta> alertasDisparados = new ArrayList<>();

        for (Alerta alerta : alertasAtivos) {
            BigDecimal totalGasto;

            if (alerta.getCategoria() != null) {
                totalGasto = gastoRepo.listarPorCategoria(alerta.getCategoria().getId(), mes)
                        .stream()
                        .map(g -> g.getValor())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            } else {
                totalGasto = gastoRepo.listarPorMes(mes)
                        .stream()
                        .map(g -> g.getValor())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }

            if (totalGasto.compareTo(alerta.getValorLimite()) >= 0) {
                alertaRepo.marcarDisparado(alerta.getId());
                alerta.setDisparado(true);
                alertasDisparados.add(alerta);
            }
        }

        return alertasDisparados;
    }

    public List<Alerta> listarPorMes(YearMonth mes) {
        return alertaRepo.listarPorMes(mes);
    }
}