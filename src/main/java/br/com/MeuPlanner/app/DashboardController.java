package br.com.MeuPlanner.app;

import java.text.NumberFormat;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.MeuPlanner.model.Alerta;
import br.com.MeuPlanner.model.Gasto;
import br.com.MeuPlanner.model.Meta;
import br.com.MeuPlanner.service.AlertaService;
import br.com.MeuPlanner.service.ContaService;
import br.com.MeuPlanner.service.FinanceiroService;
import br.com.MeuPlanner.service.LancamentoService;
import br.com.MeuPlanner.service.MetaService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class DashboardController {

    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @FXML private Label lblSaldoTotal;
    @FXML private Label lblEntradas;
    @FXML private Label lblGastos;
    @FXML private PieChart graficoGastosCategoria;
    @FXML private BarChart<String, Number> graficoSaldoMensal;
    @FXML private ListView<String> listaAlertas;
    @FXML private ListView<String> listaMetas;

    private final ContaService contaService = new ContaService();
    private final FinanceiroService financeiroService = new FinanceiroService();
    private final AlertaService alertaService = new AlertaService();
    private final MetaService metaService = new MetaService();
    private final LancamentoService lancamentoService = new LancamentoService();

    @FXML
    public void initialize() {
        lblSaldoTotal.setText(MOEDA.format(contaService.saldoTotalGeral()));
        lblEntradas.setText(MOEDA.format(financeiroService.totalEntradas()));
        lblGastos.setText(MOEDA.format(financeiroService.totalGastos()));

        for (Alerta a : alertaService.verificarAlertas(YearMonth.now()))
            listaAlertas.getItems().add("⚠ " + a.getDescricao() + " — limite ultrapassado");

        for (Meta m : metaService.listarAtivas())
            listaMetas.getItems().add(m.getDescricao() + " — " + MOEDA.format(m.getValorAtual())
                    + " / " + MOEDA.format(m.getValorAlvo()));

        carregarGraficoGastosCategoria();
        carregarGraficoSaldoMensal();
    }

    private void carregarGraficoGastosCategoria() {
        List<Gasto> gastos = lancamentoService.listarGastosDoMes(YearMonth.now());
        Map<String, Double> porCategoria = gastos.stream()
                .collect(Collectors.groupingBy(
                        g -> g.getCategoria() != null ? g.getCategoria().getNome() : "Sem categoria",
                        Collectors.summingDouble(g -> g.getValor().doubleValue())
                ));

        graficoGastosCategoria.setData(FXCollections.observableArrayList(
                porCategoria.entrySet().stream()
                        .map(e -> new PieChart.Data(e.getKey(), e.getValue()))
                        .collect(Collectors.toList())
        ));
    }

    private void carregarGraficoSaldoMensal() {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Saldo");

        YearMonth atual = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth mes = atual.minusMonths(i);
            double saldo = lancamentoService.totalEntradasDoMes(mes).doubleValue()
                    - lancamentoService.totalGastosDoMes(mes).doubleValue();
            serie.getData().add(new XYChart.Data<>(mes.toString(), saldo));
        }

        graficoSaldoMensal.getData().setAll(serie);
    }
}
