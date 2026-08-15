package br.com.MeuPlanner.app;

import java.time.YearMonth;

import br.com.MeuPlanner.model.Alerta;
import br.com.MeuPlanner.model.Meta;
import br.com.MeuPlanner.service.AlertaService;
import br.com.MeuPlanner.service.ContaService;
import br.com.MeuPlanner.service.FinanceiroService;
import br.com.MeuPlanner.service.MetaService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class DashboardController {

    @FXML private Label lblSaldoTotal;
    @FXML private Label lblEntradas;
    @FXML private Label lblGastos;
    @FXML private ListView<String> listaAlertas;
    @FXML private ListView<String> listaMetas;

    private final ContaService contaService = new ContaService();
    private final FinanceiroService financeiroService = new FinanceiroService();
    private final AlertaService alertaService = new AlertaService();
    private final MetaService metaService = new MetaService();

    @FXML
    public void initialize() {
        lblSaldoTotal.setText("R$ " + contaService.saldoTotalGeral());
        lblEntradas.setText("R$ " + financeiroService.totalEntradas());
        lblGastos.setText("R$ " + financeiroService.totalGastos());

        for (Alerta a : alertaService.verificarAlertas(YearMonth.now()))
            listaAlertas.getItems().add("⚠ " + a.getDescricao() + " — limite ultrapassado");

        for (Meta m : metaService.listarAtivas())
            listaMetas.getItems().add(m.getDescricao() + " — R$ " + m.getValorAtual() + " / R$ " + m.getValorAlvo());
    }
}