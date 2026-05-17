package br.com.MeuPlanner.app;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.model.Meta;
import br.com.MeuPlanner.service.ContaService;
import br.com.MeuPlanner.service.MetaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class MetasController {

    @FXML private TextField txtDescricao;
    @FXML private TextField txtValorAlvo;
    @FXML private DatePicker dataLimite;
    @FXML private ComboBox<Conta> cmbConta;
    @FXML private TableView<Meta> tabelaMetas;
    @FXML private TableColumn<Meta, String> colDescricao;
    @FXML private TableColumn<Meta, String> colAlvo;
    @FXML private TableColumn<Meta, String> colAtual;
    @FXML private TableColumn<Meta, String> colProgresso;
    @FXML private TableColumn<Meta, String> colLimite;
    @FXML private TableColumn<Meta, String> colAcoes;

    private final MetaService metaService = new MetaService();
    private final ContaService contaService = new ContaService();

    @FXML
    public void initialize() {
        cmbConta.setItems(FXCollections.observableArrayList(contaService.listarContas()));
        colDescricao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescricao()));
        colAlvo.setCellValueFactory(c -> new SimpleStringProperty("R$ " + c.getValue().getValorAlvo()));
        colAtual.setCellValueFactory(c -> new SimpleStringProperty("R$ " + c.getValue().getValorAtual()));
        colProgresso.setCellValueFactory(c -> {
            Meta m = c.getValue();
            double pct = m.getValorAtual().doubleValue() / m.getValorAlvo().doubleValue() * 100;
            return new SimpleStringProperty(String.format("%.1f%%", pct));
        });
        colLimite.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDataLimite().toString()));
        colAcoes.setCellFactory(tc -> new TableCell<>() {
            private final Button btn = new Button("Excluir");
            { btn.setOnAction(e -> {
                Meta meta = getTableView().getItems().get(getIndex());
                metaService.deletar(meta.getId());
                carregarTabela();
            }); }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        carregarTabela();
    }

    @FXML
    private void criarMeta() {
        try {
            String descricao = txtDescricao.getText().trim();
            BigDecimal valorAlvo = new BigDecimal(txtValorAlvo.getText().trim().replace(",", "."));
            LocalDate limite = dataLimite.getValue();
            Conta conta = cmbConta.getValue();
            metaService.criarMeta(descricao, valorAlvo, limite, conta);
            txtDescricao.clear(); txtValorAlvo.clear();
            dataLimite.setValue(null); cmbConta.setValue(null);
            carregarTabela();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erro: " + ex.getMessage()).show();
        }
    }

    private void carregarTabela() {
        tabelaMetas.setItems(FXCollections.observableArrayList(metaService.listarAtivas()));
    }

    @FXML void irDashboard()      { SceneManager.navegarPara("dashboard"); }
    @FXML void irContas()         { SceneManager.navegarPara("contas"); }
    @FXML void irLancamentos()    { SceneManager.navegarPara("lancamentos"); }
    @FXML void irTransferencias() { SceneManager.navegarPara("transferencias"); }
    @FXML void irCategorias()     { SceneManager.navegarPara("categorias"); }
    @FXML void irMetas()          { SceneManager.navegarPara("metas"); }
    @FXML void irRelatorios()     { SceneManager.navegarPara("relatorios"); }
}
