package br.com.MeuPlanner.app;

import br.com.MeuPlanner.model.Categoria;
import br.com.MeuPlanner.service.CategoriaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class CategoriasController {

    @FXML private TextField txtNome;
    @FXML private ComboBox<Categoria.TipoCategoria> cmbTipo;
    @FXML private TextField txtCor;
    @FXML private TableView<Categoria> tabelaCategorias;
    @FXML private TableColumn<Categoria, String> colNome;
    @FXML private TableColumn<Categoria, String> colTipo;
    @FXML private TableColumn<Categoria, String> colCor;
    @FXML private TableColumn<Categoria, String> colAcoes;

    private final CategoriaService categoriaService = new CategoriaService();

    @FXML
    public void initialize() {
        cmbTipo.setItems(FXCollections.observableArrayList(Categoria.TipoCategoria.values()));
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().name()));
        colCor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCor()));
        colAcoes.setCellFactory(tc -> new TableCell<>() {
            private final Button btn = new Button("Excluir");
            { btn.setOnAction(e -> {
                Categoria cat = getTableView().getItems().get(getIndex());
                categoriaService.deletar(cat.getId());
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
    private void adicionarCategoria() {
        try {
            String nome = txtNome.getText().trim();
            Categoria.TipoCategoria tipo = cmbTipo.getValue();
            String cor = txtCor.getText().trim().isEmpty() ? "#607D8B" : txtCor.getText().trim();
            categoriaService.criarCategoria(nome, tipo, cor);
            txtNome.clear(); txtCor.clear(); cmbTipo.setValue(null);
            carregarTabela();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erro: " + ex.getMessage()).show();
        }
    }

    private void carregarTabela() {
        tabelaCategorias.setItems(FXCollections.observableArrayList(categoriaService.listarTodas()));
    }

    @FXML void irDashboard()      { SceneManager.navegarPara("dashboard"); }
    @FXML void irContas()         { SceneManager.navegarPara("contas"); }
    @FXML void irLancamentos()    { SceneManager.navegarPara("lancamentos"); }
    @FXML void irTransferencias() { SceneManager.navegarPara("transferencias"); }
    @FXML void irCategorias()     { SceneManager.navegarPara("categorias"); }
    @FXML void irMetas()          { SceneManager.navegarPara("metas"); }
    @FXML void irRelatorios()     { SceneManager.navegarPara("relatorios"); }
}
