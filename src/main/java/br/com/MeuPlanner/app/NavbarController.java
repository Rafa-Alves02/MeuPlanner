package br.com.MeuPlanner.app;

import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class NavbarController {

    private static final String CLASSE_ATIVA = "nav-btn-ativo";

    @FXML private Button btnDashboard;
    @FXML private Button btnContas;
    @FXML private Button btnLancamentos;
    @FXML private Button btnTransferencias;
    @FXML private Button btnCategorias;
    @FXML private Button btnMetas;
    @FXML private Button btnRelatorios;
    @FXML private ColorPicker colorPickerAccent;

    private Map<String, Button> botoesPorTela;

    @FXML
    private void initialize() {
        botoesPorTela = Map.of(
                "dashboard", btnDashboard,
                "contas", btnContas,
                "lancamentos", btnLancamentos,
                "transferencias", btnTransferencias,
                "categorias", btnCategorias,
                "metas", btnMetas,
                "relatorios", btnRelatorios
        );

        colorPickerAccent.setValue(Color.web(TemaPreferences.corAcentoSalva()));

        SceneManager.registrarNavbar(this);
        marcarAtiva(SceneManager.getTelaAtual());
    }

    public void marcarAtiva(String tela) {
        botoesPorTela.values().forEach(botao -> botao.getStyleClass().remove(CLASSE_ATIVA));
        if (tela == null) return;
        Button ativo = botoesPorTela.get(tela);
        if (ativo != null) {
            ativo.getStyleClass().add(CLASSE_ATIVA);
        }
    }

    @FXML
    private void trocarCor() {
        String corHex = toHex(colorPickerAccent.getValue());
        SceneManager.aplicarCorAcento(corHex);
        TemaPreferences.salvarCorAcento(corHex);
    }

    private String toHex(Color cor) {
        return String.format("#%02X%02X%02X",
                (int) Math.round(cor.getRed() * 255),
                (int) Math.round(cor.getGreen() * 255),
                (int) Math.round(cor.getBlue() * 255));
    }

    @FXML void irDashboard()      { SceneManager.navegarPara("dashboard"); }
    @FXML void irContas()         { SceneManager.navegarPara("contas"); }
    @FXML void irLancamentos()    { SceneManager.navegarPara("lancamentos"); }
    @FXML void irTransferencias() { SceneManager.navegarPara("transferencias"); }
    @FXML void irCategorias()     { SceneManager.navegarPara("categorias"); }
    @FXML void irMetas()          { SceneManager.navegarPara("metas"); }
    @FXML void irRelatorios()     { SceneManager.navegarPara("relatorios"); }
}
