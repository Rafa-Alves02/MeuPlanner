package br.com.MeuPlanner.app;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.MeuPlanner.model.Categoria;
import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.ofx.TransacaoOfx;
import br.com.MeuPlanner.service.CategoriaService;
import br.com.MeuPlanner.service.CategorizacaoIAService;
import br.com.MeuPlanner.service.OfxImportService;
import br.com.MeuPlanner.service.PluggyImportService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class OfxImportController {

    private final OfxImportService ofxImportService = new OfxImportService();

    @FXML
    private Label lblArquivo;
    @FXML
    private Label lblResumo;
    @FXML
    private TableView<LinhaImportacao> tabelaTransacoes;
    @FXML
    private TableColumn<LinhaImportacao, Boolean> colSelecionar;
    @FXML
    private TableColumn<LinhaImportacao, String> colData;
    @FXML
    private TableColumn<LinhaImportacao, String> colDescricao;
    @FXML
    private TableColumn<LinhaImportacao, String> colValor;
    @FXML
    private TableColumn<LinhaImportacao, String> colCategoria;
    @FXML
    private TableColumn<LinhaImportacao, String> colStatus;
    @FXML
    private TableColumn<LinhaImportacao, Void> colAcao;

    private final CategorizacaoIAService categorizacaoIAService = new CategorizacaoIAService();
    private final CategoriaService categoriaService = new CategoriaService();
    private final PluggyImportService pluggyImportService = new PluggyImportService();

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
        colCategoria.setCellValueFactory(c -> c.getValue().categoriaSugeridaNome);
        colStatus
                .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().jaImportado ? "Já importado" : "Novo"));
        colAcao.setCellFactory(tc -> new TableCell<>() {
            private final Button btn = new Button("Corrigir");
            { btn.getStyleClass().add("btn-danger"); btn.setOnAction(e -> corrigirCategoria(getTableRow().getItem())); }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    @FXML
    private void sugerirCategoriasIA() {
        for (LinhaImportacao linha : tabelaTransacoes.getItems()) {
            if (linha.jaImportado) continue;
            Categoria.TipoCategoria tipo = linha.transacao.isEntrada()
                    ? Categoria.TipoCategoria.ENTRADA
                    : Categoria.TipoCategoria.SAIDA;
            try {
                CategorizacaoIAService.Sugestao sugestao =
                        categorizacaoIAService.sugerirCategoria(linha.transacao.descricao(), tipo);
                aplicarSugestao(linha, sugestao.categoria(), sugestao.aprendida());
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Erro ao consultar a IA: " + ex.getMessage()).showAndWait();
                return;
            }
        }
    }

    private void corrigirCategoria(LinhaImportacao linha) {
        if (linha == null) return;

        Categoria.TipoCategoria tipo = linha.transacao.isEntrada()
                ? Categoria.TipoCategoria.ENTRADA
                : Categoria.TipoCategoria.SAIDA;
        List<Categoria> opcoes = tipo == Categoria.TipoCategoria.ENTRADA
                ? categoriaService.listarEntradas()
                : categoriaService.listarSaidas();
        if (opcoes.isEmpty()) return;

        Categoria valorInicial = linha.categoriaSugerida != null ? linha.categoriaSugerida : opcoes.get(0);
        ChoiceDialog<Categoria> dialog = new ChoiceDialog<>(valorInicial, opcoes);
        dialog.setTitle("Corrigir categoria");
        dialog.setHeaderText(linha.transacao.descricao());
        dialog.setContentText("Categoria correta:");

        Optional<Categoria> escolha = dialog.showAndWait();
        escolha.ifPresent(categoria -> {
            categorizacaoIAService.corrigir(linha.transacao.descricao(), categoria);
            aplicarSugestao(linha, categoria, true);
        });
    }

    private void aplicarSugestao(LinhaImportacao linha, Categoria categoria, boolean aprendida) {
        linha.categoriaSugerida = categoria;
        linha.categoriaSugeridaNome.set(categoria != null
                ? categoria.getNome() + (aprendida ? " (aprendida)" : "")
                : "—");
    }

    @FXML
    private void escolherArquivo() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Selecionar arquivo OFX");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos OFX", "*.ofx"));
        File arquivo = chooser.showOpenDialog(tabelaTransacoes.getScene().getWindow());
        if (arquivo == null)
            return;

        try {
            popular(ofxImportService.lerParaRevisao(arquivo, conta));
            lblArquivo.setText(arquivo.getName());
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erro ao ler o OFX: " + ex.getMessage()).showAndWait();
        }
    }

    public void carregarDoOpenFinance() {
        try {
            popular(pluggyImportService.lerParaRevisao(conta));
            lblArquivo.setText("Open Finance — sincronizado agora");
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erro ao sincronizar com o Open Finance: " + ex.getMessage()).showAndWait();
        }
    }

    private void popular(List<OfxImportService.ItemImportacao> itens) {
        ObservableList<LinhaImportacao> linhas = FXCollections.observableArrayList();
        for (var item : itens) {
            linhas.add(new LinhaImportacao(item.transacao(), item.jaImportado()));
        }
        tabelaTransacoes.setItems(linhas);
        atualizarResumo();
    }

    private void atualizarResumo() {
        long novos = tabelaTransacoes.getItems().stream().filter(l -> !l.jaImportado).count();
        long duplicadas = tabelaTransacoes.getItems().size() - novos;
        lblResumo.setText(novos + " novas, " + duplicadas + " já importadas anteriormente");
    }

    @FXML
    private void confirmarImportacao() {
        if (conta == null || tabelaTransacoes.getItems().isEmpty())
            return;

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
        final SimpleStringProperty categoriaSugeridaNome = new SimpleStringProperty("—");
        Categoria categoriaSugerida;

        LinhaImportacao(TransacaoOfx transacao, boolean jaImportado) {
            this.transacao = transacao;
            this.jaImportado = jaImportado;
            this.selecionado = new SimpleBooleanProperty(!jaImportado);
        }
    }
}
