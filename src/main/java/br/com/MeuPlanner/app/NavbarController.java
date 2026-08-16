package br.com.MeuPlanner.app;

import java.io.IOException;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.MenuButton;
import javafx.scene.paint.Color;

public class NavbarController {

    private static final String CLASSE_BTN_ATIVO = "nav-btn-ativo";
    private static final String CLASSE_MENU_ATIVO = "nav-menu-ativo";

    @FXML private Button btnDashboard;
    @FXML private MenuButton menuContas;
    @FXML private MenuButton menuPerfil;
    @FXML private ColorPicker colorPickerAccent;

    private Map<String, Button> botoesPorTela;
    private Map<String, MenuButton> menusPorTela;

    @FXML
    private void initialize() {
        botoesPorTela = Map.of("dashboard", btnDashboard);
        menusPorTela = Map.of(
                "contas", menuContas,
                "lancamentos", menuContas,
                "metas", menuContas,
                "perfil", menuPerfil,
                "relatorios", menuPerfil,
                "categorias", menuPerfil
        );

        colorPickerAccent.setValue(Color.web(TemaPreferences.corAcentoSalva()));

        SceneManager.registrarNavbar(this);
        marcarAtiva(SceneManager.getTelaAtual());
    }

    public void marcarAtiva(String tela) {
        botoesPorTela.values().forEach(botao -> botao.getStyleClass().remove(CLASSE_BTN_ATIVO));
        menuContas.getStyleClass().remove(CLASSE_MENU_ATIVO);
        menuPerfil.getStyleClass().remove(CLASSE_MENU_ATIVO);
        if (tela == null) return;

        Button botaoAtivo = botoesPorTela.get(tela);
        if (botaoAtivo != null) botaoAtivo.getStyleClass().add(CLASSE_BTN_ATIVO);

        MenuButton menuAtivo = menusPorTela.get(tela);
        if (menuAtivo != null) menuAtivo.getStyleClass().add(CLASSE_MENU_ATIVO);
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

    @FXML
    private void sair() {
        try {
            SessaoAtual.encerrar();
            MainApp.exibirLogin();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao voltar pro login", e);
        }
    }

    @FXML void irDashboard()   { SceneManager.navegarPara("dashboard"); }
    @FXML void irContas()      { SceneManager.navegarPara("contas"); }
    @FXML void irLancamentos() { SceneManager.navegarPara("lancamentos"); }
    @FXML void irMetas()       { SceneManager.navegarPara("metas"); }
    @FXML void irPerfil()      { SceneManager.navegarPara("perfil"); }
    @FXML void irRelatorios()  { SceneManager.navegarPara("relatorios"); }
    @FXML void irCategorias()  { SceneManager.navegarPara("categorias"); }
}
