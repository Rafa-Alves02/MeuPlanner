package br.com.MeuPlanner.app;

import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SidebarController {

    @FXML private Button btnDashboard;
    @FXML private Button btnContas;
    @FXML private Button btnLancamentos;
    @FXML private Button btnTransferencias;
    @FXML private Button btnCategorias;
    @FXML private Button btnMetas;
    @FXML private Button btnRelatorios;

    @FXML
    private void initialize() {
        Map<String, Button> botoesPorTela = Map.of(
                "dashboard", btnDashboard,
                "contas", btnContas,
                "lancamentos", btnLancamentos,
                "transferencias", btnTransferencias,
                "categorias", btnCategorias,
                "metas", btnMetas,
                "relatorios", btnRelatorios
        );

        Button ativo = botoesPorTela.get(SceneManager.getTelaAtual());
        if (ativo != null) {
            ativo.getStyleClass().add("nav-btn-ativo");
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
