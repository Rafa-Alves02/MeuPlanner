package br.com.MeuPlanner.app;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.model.Transferencia;
import br.com.MeuPlanner.service.ContaService;
import br.com.MeuPlanner.service.TransferenciaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TransferenciasController {

    @FXML private ComboBox<Conta> cmbOrigem;
    @FXML private ComboBox<Conta> cmbDestino;
    @FXML private TextField txtValor;
    @FXML private TextField txtDescricao;
    @FXML private TableView<Transferencia> tabelaTransferencias;
    @FXML private TableColumn<Transferencia, String> colOrigem;
    @FXML private TableColumn<Transferencia, String> colDestino;
    @FXML private TableColumn<Transferencia, String> colValor;
    @FXML private TableColumn<Transferencia, String> colData;
    @FXML private TableColumn<Transferencia, String> colDesc;

    private final TransferenciaService transferenciaService = new TransferenciaService();
    private final ContaService contaService = new ContaService();

    @FXML
    public void initialize() {
        List<Conta> contas = contaService.listarContas();
        cmbOrigem.setItems(FXCollections.observableArrayList(contas));
        cmbDestino.setItems(FXCollections.observableArrayList(contas));

        colOrigem.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getContaOrigem().getNome()));
        colDestino.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getContaDestino().getNome()));
        colValor.setCellValueFactory(c -> new SimpleStringProperty("R$ " + c.getValue().getValor()));
        colData.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDataTransferencia().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        colDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescricao() != null ? c.getValue().getDescricao() : ""));
    }

    @FXML
    private void transferir() {
        try {
            Conta origem = cmbOrigem.getValue();
            Conta destino = cmbDestino.getValue();
            BigDecimal valor = new BigDecimal(txtValor.getText().trim().replace(",", "."));
            String descricao = txtDescricao.getText().trim();

            transferenciaService.transferir(origem, destino, valor, descricao);
            txtValor.clear(); txtDescricao.clear();
            cmbOrigem.setValue(null); cmbDestino.setValue(null);
            new Alert(Alert.AlertType.INFORMATION, "Transferência realizada!").show();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erro: " + ex.getMessage()).show();
        }
    }

    @FXML void irDashboard()      { SceneManager.navegarPara("dashboard"); }
    @FXML void irContas()         { SceneManager.navegarPara("contas"); }
    @FXML void irLancamentos()    { SceneManager.navegarPara("lancamentos"); }
    @FXML void irTransferencias() { SceneManager.navegarPara("transferencias"); }
    @FXML void irCategorias()     { SceneManager.navegarPara("categorias"); }
    @FXML void irMetas()          { SceneManager.navegarPara("metas"); }
    @FXML void irRelatorios()     { SceneManager.navegarPara("relatorios"); }
}
