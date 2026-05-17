package br.com.MeuPlanner.app;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import br.com.MeuPlanner.model.Categoria;
import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.model.Entrada;
import br.com.MeuPlanner.model.Gasto;
import br.com.MeuPlanner.model.TipoGasto;
import br.com.MeuPlanner.model.TipoRecorrencia;
import br.com.MeuPlanner.service.CategoriaService;
import br.com.MeuPlanner.service.ContaService;
import br.com.MeuPlanner.service.LancamentoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class LancamentosController {

    @FXML private RadioButton rbEntrada;
    @FXML private RadioButton rbGasto;
    @FXML private ComboBox<String> cmbMes;
    @FXML private TextField txtDescricao;
    @FXML private TextField txtValor;
    @FXML private DatePicker dataPicker;
    @FXML private ComboBox<Conta> cmbConta;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private ComboBox<TipoRecorrencia> cmbRecorrencia;
    @FXML private TableView<Object> tabelaLancamentos;
    @FXML private TableColumn<Object, String> colTipo;
    @FXML private TableColumn<Object, String> colDescricao;
    @FXML private TableColumn<Object, String> colValor;
    @FXML private TableColumn<Object, String> colData;
    @FXML private TableColumn<Object, String> colConta;
    @FXML private TableColumn<Object, String> colCategoria;
    @FXML private TableColumn<Object, String> colAcoes;

    private final LancamentoService lancamentoService = new LancamentoService();
    private final ContaService contaService = new ContaService();
    private final CategoriaService categoriaService = new CategoriaService();

    @FXML
    public void initialize() {
        rbEntrada.setSelected(true);
        cmbRecorrencia.setItems(FXCollections.observableArrayList(TipoRecorrencia.values()));
        cmbConta.setItems(FXCollections.observableArrayList(contaService.listarContas()));
        cmbCategoria.setItems(FXCollections.observableArrayList(categoriaService.listarTodas()));

        YearMonth atual = YearMonth.now();
        List<String> meses = new ArrayList<>();
        for (int i = 5; i >= 0; i--) meses.add(atual.minusMonths(i).toString());
        cmbMes.setItems(FXCollections.observableArrayList(meses));
        cmbMes.setValue(atual.toString());
        cmbMes.setOnAction(e -> carregarTabela());

        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue() instanceof Entrada ? "Entrada" : "Gasto"));
        colDescricao.setCellValueFactory(c -> {
            if (c.getValue() instanceof Entrada e) return new SimpleStringProperty(e.getDescricao());
            if (c.getValue() instanceof Gasto g)  return new SimpleStringProperty(g.getDescricao());
            return new SimpleStringProperty("");
        });
        colValor.setCellValueFactory(c -> {
            if (c.getValue() instanceof Entrada e) return new SimpleStringProperty("R$ " + e.getValor());
            if (c.getValue() instanceof Gasto g)  return new SimpleStringProperty("R$ " + g.getValor());
            return new SimpleStringProperty("");
        });
        colData.setCellValueFactory(c -> {
            if (c.getValue() instanceof Entrada e) return new SimpleStringProperty(e.getDataLancamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            if (c.getValue() instanceof Gasto g)  return new SimpleStringProperty(g.getDataLancamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            return new SimpleStringProperty("");
        });
        colConta.setCellValueFactory(c -> {
            if (c.getValue() instanceof Entrada e) return new SimpleStringProperty(e.getConta().getNome());
            if (c.getValue() instanceof Gasto g)  return new SimpleStringProperty(g.getConta().getNome());
            return new SimpleStringProperty("");
        });
        colCategoria.setCellValueFactory(c -> {
            if (c.getValue() instanceof Entrada e) return new SimpleStringProperty(e.getCategoria() != null ? e.getCategoria().getNome() : "-");
            if (c.getValue() instanceof Gasto g)  return new SimpleStringProperty(g.getCategoria() != null ? g.getCategoria().getNome() : "-");
            return new SimpleStringProperty("");
        });

        carregarTabela();
    }

    @FXML
    private void lancar() {
        try {
            String descricao = txtDescricao.getText().trim();
            BigDecimal valor = new BigDecimal(txtValor.getText().trim().replace(",", "."));
            LocalDate data = dataPicker.getValue() != null ? dataPicker.getValue() : LocalDate.now();
            Conta conta = cmbConta.getValue();
            Categoria categoria = cmbCategoria.getValue();
            TipoRecorrencia recorrencia = cmbRecorrencia.getValue() != null ? cmbRecorrencia.getValue() : TipoRecorrencia.UNICA;

            if (rbEntrada.isSelected()) {
                lancamentoService.adicionarEntrada(descricao, valor, data, recorrencia, conta, categoria);
            } else {
                lancamentoService.adicionarGasto(descricao, valor, data, TipoGasto.VARIAVEL, recorrencia, conta, categoria);
            }
            limparFormulario();
            carregarTabela();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erro: " + ex.getMessage()).show();
        }
    }

    private void carregarTabela() {
        YearMonth mes = YearMonth.parse(cmbMes.getValue());
        List<Object> itens = new ArrayList<>();
        itens.addAll(lancamentoService.listarEntradasDoMes(mes));
        itens.addAll(lancamentoService.listarGastosDoMes(mes));
        tabelaLancamentos.setItems(FXCollections.observableArrayList(itens));
    }

    private void limparFormulario() {
        txtDescricao.clear(); txtValor.clear();
        dataPicker.setValue(null); cmbConta.setValue(null);
        cmbCategoria.setValue(null); cmbRecorrencia.setValue(null);
    }

    @FXML void irDashboard()      { SceneManager.navegarPara("dashboard"); }
    @FXML void irContas()         { SceneManager.navegarPara("contas"); }
    @FXML void irLancamentos()    { SceneManager.navegarPara("lancamentos"); }
    @FXML void irTransferencias() { SceneManager.navegarPara("transferencias"); }
    @FXML void irCategorias()     { SceneManager.navegarPara("categorias"); }
    @FXML void irMetas()          { SceneManager.navegarPara("metas"); }
    @FXML void irRelatorios()     { SceneManager.navegarPara("relatorios"); }
}
