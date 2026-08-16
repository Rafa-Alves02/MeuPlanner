package br.com.MeuPlanner.app;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.MeuPlanner.model.Categoria;
import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.ofx.TransacaoOfx;
import br.com.MeuPlanner.service.CategorizacaoIAService;
import br.com.MeuPlanner.service.OfxImportService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class OfxImportController {

    private final OfxImportService ofxImportService = new OfxImportService();
    private final CategorizacaoIAService categorizacaoIAService = new CategorizacaoIAService();

    @FXML private Label lblArquivo;
    @FXML private Label lblResumo;
    @FXML private TableView<LinhaImportacao> tabelaTransacoes;
    @FXML private TableColumn<LinhaImportacao, Boolean> colSelecionar;
    @FXML private TableColumn<LinhaImportacao, String> colData;
    @FXML private TableColumn<LinhaImportacao, String> colDescricao;
    @FXML private TableColumn<LinhaImportacao, String> colValor;
    @FXML private TableColumn<LinhaImportacao, String> colCategoria;
    @FXML private TableColumn<LinhaImportacao, String> colStatus;

    private Conta conta;
    private boolean importado;

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public boolean isImportado() {
        return importado;
    }

    @FXML
    public void initialize() {
        colSelecionar.setCellValueFactory(c -> c.getValue().selecionado);
        colSelecionar.setCellFactory(CheckBoxTableCell.forTableColumn(colSelecionar));
        colData.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().transacao.data().toString()));
        colDescricao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().transacao.descricao()));
        colValor.setCellValueFactory(c -> new SimpleStringProperty("R$ " + c.getValue().transacao.valor()));
        colCategoria.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().categoriaSugerida == null ? "—" : c.getValue().categoriaSugerida.getNome()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().jaImportado ? "Já importado" : "Novo"));
    }

    @FXML
    private void escolherArquivo() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Selecionar arquivo OFX");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos OFX", "*.ofx"));
        File arquivo = chooser.showOpenDialog(tabelaTransacoes.getScene().getWindow());
        if (arquivo == null) return;

        try {
            List<OfxImportService.ItemImportacao> itens = ofxImportService.lerParaRevisao(arquivo, conta);
            ObservableList<LinhaImportacao> linhas = FXCollections.observableArrayList();
            for (var item : itens) {
                linhas.add(new LinhaImportacao(item.transacao(), item.jaImportado()));
            }
            tabelaTransacoes.setItems(linhas);
            lblArquivo.setText(arquivo.getName());
            atualizarResumo();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erro ao ler o OFX: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void sugerirCategoriasIA() {
        if (tabelaTransacoes.getItems().isEmpty()) return;

        try {
            for (LinhaImportacao linha : tabelaTransacoes.getItems()) {
                if (linha.jaImportado) continue;

                Categoria.TipoCategoria tipo = linha.transacao.isEntrada()
                        ? Categoria.TipoCategoria.ENTRADA
                        : Categoria.TipoCategoria.SAIDA;
                linha.categoriaSugerida = categorizacaoIAService.sugerirCategoria(linha.transacao.descricao(), tipo);
            }
            tabelaTransacoes.refresh();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erro ao consultar a IA: " + ex.getMessage()).showAndWait();
        }
    }

    private void atualizarResumo() {
        long novos = tabelaTransacoes.getItems().stream().filter(l -> !l.jaImportado).count();
        long duplicadas = tabelaTransacoes.getItems().size() - novos;
        lblResumo.setText(novos + " novas, " + duplicadas + " já importadas anteriormente");
    }

    @FXML
    private void confirmarImportacao() {
        if (conta == null || tabelaTransacoes.getItems().isEmpty()) return;

        Map<TransacaoOfx, Categoria> selecionadas = new HashMap<>();
        for (LinhaImportacao linha : tabelaTransacoes.getItems()) {
            if (linha.selecionado.get() && !linha.jaImportado) {
                selecionadas.put(linha.transacao, linha.categoriaSugerida);
            }
        }

        if (selecionadas.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Nenhuma transação nova selecionada.").showAndWait();
            return;
        }

        int total = ofxImportService.importar(conta, selecionadas);
        importado = total > 0;
        new Alert(Alert.AlertType.INFORMATION, total + " lançamento(s) importado(s) com sucesso.").showAndWait();
        fechar();
    }

    @FXML
    private void cancelar() {
        fechar();
    }

    private void fechar() {
        ((Stage) tabelaTransacoes.getScene().getWindow()).close();
    }

    private static class LinhaImportacao {
        final TransacaoOfx transacao;
        final boolean jaImportado;
        final SimpleBooleanProperty selecionado;
        Categoria categoriaSugerida;

        LinhaImportacao(TransacaoOfx transacao, boolean jaImportado) {
            this.transacao = transacao;
            this.jaImportado = jaImportado;
            this.selecionado = new SimpleBooleanProperty(!jaImportado);
        }
    }
}
