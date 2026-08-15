package br.com.MeuPlanner.app;

import java.math.BigDecimal;

import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.service.ContaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ContasController {

    @FXML private TextField txtNome;
    @FXML private ComboBox<Conta.TipoConta> cmbTipo;
    @FXML private TextField txtSaldo;
    @FXML private TableView<Conta> tabelaContas;
    @FXML private TableColumn<Conta, String> colNome;
    @FXML private TableColumn<Conta, String> colTipo;
    @FXML private TableColumn<Conta, String> colSaldo;
    @FXML private TableColumn<Conta, String> colAcoes;
    @FXML private Label lblSaldoGeral;

    private final ContaService contaService = new ContaService();

    @FXML
    public void initialize() {
        cmbTipo.setItems(FXCollections.observableArrayList(Conta.TipoConta.values()));
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().name()));
        colSaldo.setCellValueFactory(c -> new SimpleStringProperty("R$ " + c.getValue().getSaldoAtual()));
        colAcoes.setCellFactory(tc -> new TableCell<>() {
            private final Button btn = new Button("Excluir");
            { btn.setOnAction(e -> {
                Conta conta = getTableView().getItems().get(getIndex());
                contaService.deletarConta(conta.getId());
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
    private void adicionarConta() {
        try {
            String nome = txtNome.getText().trim();
            Conta.TipoConta tipo = cmbTipo.getValue();
            BigDecimal saldo = new BigDecimal(txtSaldo.getText().trim().replace(",", "."));
            contaService.criarConta(nome, tipo, saldo);
            txtNome.clear(); txtSaldo.clear(); cmbTipo.setValue(null);
            carregarTabela();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erro: " + ex.getMessage()).show();
        }
    }

    private void carregarTabela() {
        tabelaContas.setItems(FXCollections.observableArrayList(contaService.listarContas()));
        lblSaldoGeral.setText("Saldo Geral: R$ " + contaService.saldoTotalGeral());
    }
}
