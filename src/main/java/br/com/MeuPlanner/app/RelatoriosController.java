package br.com.MeuPlanner.app;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.MeuPlanner.model.Gasto;
import br.com.MeuPlanner.repository.FechamentoMensalRepository;
import br.com.MeuPlanner.service.LancamentoService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;

public class RelatoriosController {

    @FXML private ComboBox<String> cmbMes;
    @FXML private PieChart graficoPizza;
    @FXML private LineChart<String, Number> graficoLinha;

    private final LancamentoService lancamentoService = new LancamentoService();
    private final FechamentoMensalRepository fechamentoRepo = new FechamentoMensalRepository();

    @FXML
    public void initialize() {
        YearMonth atual = YearMonth.now();
        List<String> meses = new ArrayList<>();
        for (int i = 5; i >= 0; i--) meses.add(atual.minusMonths(i).toString());
        cmbMes.setItems(FXCollections.observableArrayList(meses));
        cmbMes.setValue(atual.toString());
        carregar();
    }

    @FXML
    private void carregar() {
        YearMonth mes = YearMonth.parse(cmbMes.getValue());
        carregarPizza(mes);
        carregarLinha();
    }

    private void carregarPizza(YearMonth mes) {
        List<Gasto> gastos = lancamentoService.listarGastosDoMes(mes);
        Map<String, Double> porCategoria = gastos.stream()
                .collect(Collectors.groupingBy(
                        g -> g.getCategoria() != null ? g.getCategoria().getNome() : "Sem categoria",
                        Collectors.summingDouble(g -> g.getValor().doubleValue())
                ));
        graficoPizza.setData(FXCollections.observableArrayList(
                porCategoria.entrySet().stream()
                        .map(e -> new PieChart.Data(e.getKey(), e.getValue()))
                        .collect(Collectors.toList())
        ));
    }

    private void carregarLinha() {
        XYChart.Series<String, Number> serieEntradas = new XYChart.Series<>();
        serieEntradas.setName("Entradas");
        XYChart.Series<String, Number> serieGastos = new XYChart.Series<>();
        serieGastos.setName("Gastos");

        YearMonth atual = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth m = atual.minusMonths(i);
            String label = m.toString();
            serieEntradas.getData().add(new XYChart.Data<>(label, lancamentoService.totalEntradasDoMes(m)));
            serieGastos.getData().add(new XYChart.Data<>(label, lancamentoService.totalGastosDoMes(m)));
        }

        graficoLinha.getData().setAll(serieEntradas, serieGastos);
    }

    @FXML void irDashboard()      { SceneManager.navegarPara("dashboard"); }
    @FXML void irContas()         { SceneManager.navegarPara("contas"); }
    @FXML void irLancamentos()    { SceneManager.navegarPara("lancamentos"); }
    @FXML void irTransferencias() { SceneManager.navegarPara("transferencias"); }
    @FXML void irCategorias()     { SceneManager.navegarPara("categorias"); }
    @FXML void irMetas()          { SceneManager.navegarPara("metas"); }
    @FXML void irRelatorios()     { SceneManager.navegarPara("relatorios"); }
}
