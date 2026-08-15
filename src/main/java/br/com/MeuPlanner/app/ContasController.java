package br.com.MeuPlanner.app;

import java.io.IOException;
import java.math.BigDecimal;

import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.service.ContaService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ContasController {

    private static final java.util.List<String> BANCOS_CONHECIDOS =
            java.util.List.of("Nubank", "Bradesco", "Inter", "Outro");

    @FXML private TextField txtNome;
    @FXML private ComboBox<Conta.TipoConta> cmbTipo;
    @FXML private ComboBox<String> cmbBanco;
    @FXML private TextField txtSaldo;
    @FXML private TableView<Conta> tabelaContas;
    @FXML private TableColumn<Conta, Conta> colBanco;
    @FXML private TableColumn<Conta, String> colNome;
    @FXML private TableColumn<Conta, String> colTipo;
    @FXML private TableColumn<Conta, String> colSaldo;
    @FXML private TableColumn<Conta, String> colAcoes;
    @FXML private Label lblSaldoGeral;

    private final ContaService contaService = new ContaService();

    @FXML
    public void initialize() {
        cmbTipo.setItems(FXCollections.observableArrayList(Conta.TipoConta.values()));
        cmbBanco.setItems(FXCollections.observableArrayList(BANCOS_CONHECIDOS));

        colBanco.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue()));
        colBanco.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Conta conta, boolean empty) {
                super.updateItem(conta, empty);
                if (empty || conta == null) {
                    setGraphic(null);
                    return;
                }
                var avatar = BancoAvatar.criar(conta.getBanco(), 22);
                var label = new Label(conta.getBanco() == null || conta.getBanco().isBlank() ? "—" : conta.getBanco());
                var linha = new HBox(8, avatar, label);
                linha.setAlignment(Pos.CENTER_LEFT);
                setGraphic(linha);
            }
        });
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().name()));
        colSaldo.setCellValueFactory(c -> new SimpleStringProperty("R$ " + c.getValue().getSaldoAtual()));
        colAcoes.setCellFactory(tc -> new TableCell<>() {
            private final Button btnOfx = new Button("OFX");
            private final Button btnExcluir = new Button("Excluir");
            private final HBox botoes = new HBox(6, btnOfx, btnExcluir);
            {
                btnOfx.setOnAction(e -> abrirImportarOfx(getTableView().getItems().get(getIndex())));
                btnExcluir.getStyleClass().add("btn-danger");
                btnExcluir.setOnAction(e -> {
                    Conta conta = getTableView().getItems().get(getIndex());
                    contaService.deletarConta(conta.getId());
                    carregarTabela();
                });
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : botoes);
            }
        });
        carregarTabela();
    }

    @FXML
    private void adicionarConta() {
        try {
            String nome = txtNome.getText().trim();
            Conta.TipoConta tipo = cmbTipo.getValue();
            String banco = cmbBanco.getValue();
            BigDecimal saldo = new BigDecimal(txtSaldo.getText().trim().replace(",", "."));
            contaService.criarConta(nome, tipo, banco, saldo);
            txtNome.clear(); txtSaldo.clear(); cmbTipo.setValue(null); cmbBanco.setValue(null);
            carregarTabela();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erro: " + ex.getMessage()).show();
        }
    }

    private void carregarTabela() {
        tabelaContas.setItems(FXCollections.observableArrayList(contaService.listarContas()));
        lblSaldoGeral.setText("Saldo Geral: R$ " + contaService.saldoTotalGeral());
    }

    private void abrirImportarOfx(Conta conta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ofx-importar.fxml"));
            Parent raiz = loader.load();
            OfxImportController controller = loader.getController();
            controller.setConta(conta);

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Importar OFX — " + conta.getNome());
            Scene scene = new Scene(raiz);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            modal.setScene(scene);
            modal.showAndWait();

            if (controller.isImportado()) {
                carregarTabela();
            }
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Erro ao abrir importador de OFX: " + ex.getMessage()).show();
        }
    }
}
