package br.com.MeuPlanner.app;

import java.awt.Desktop;
import java.net.URI;

import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.pluggy.PluggyConta;
import br.com.MeuPlanner.service.ContaService;
import br.com.MeuPlanner.service.PluggyImportService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PluggyConectarController {

    @FXML private Label lblConta;
    @FXML private TextField txtItemId;
    @FXML private ListView<PluggyConta> listaContas;
    @FXML private Label lblStatus;

    private final PluggyImportService pluggyImportService = new PluggyImportService();
    private final ContaService contaService = new ContaService();

    private Conta conta;
    private boolean vinculado;

    public void setConta(Conta conta) {
        this.conta = conta;
        lblConta.setText("Vinculando: " + conta.getNome());
    }

    public boolean isVinculado() {
        return vinculado;
    }

    @FXML
    public void initialize() {
        listaContas.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(PluggyConta item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.nome() + " — " + item.tipo() + " — R$ " + item.saldo());
            }
        });
    }

    @FXML
    private void abrirNavegador() {
        try {
            String usuario = SessaoAtual.getUsuario() != null ? SessaoAtual.getUsuario().getUsername() : null;
            String connectToken = pluggyImportService.criarConnectToken(usuario);
            Desktop.getDesktop().browse(new URI("https://connect.pluggy.ai?connectToken=" + connectToken));
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erro ao abrir o navegador: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void buscarContas() {
        String itemId = txtItemId.getText() == null ? "" : txtItemId.getText().trim();
        if (itemId.isEmpty()) {
            lblStatus.setText("Cole o Item ID primeiro.");
            return;
        }
        try {
            listaContas.setItems(FXCollections.observableArrayList(pluggyImportService.listarContasDoItem(itemId)));
            lblStatus.setText(listaContas.getItems().size() + " conta(s) encontrada(s).");
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erro ao buscar contas no Pluggy: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void vincular() {
        PluggyConta selecionada = listaContas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            lblStatus.setText("Selecione uma conta na lista.");
            return;
        }
        conta.setPluggyItemId(txtItemId.getText().trim());
        conta.setPluggyAccountId(selecionada.id());
        contaService.atualizarConta(conta);
        vinculado = true;
        fechar();
    }

    @FXML
    private void cancelar() {
        fechar();
    }

    private void fechar() {
        ((Stage) lblConta.getScene().getWindow()).close();
    }
}
