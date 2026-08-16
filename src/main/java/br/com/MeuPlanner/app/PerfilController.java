package br.com.MeuPlanner.app;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PerfilController {

    @FXML private Label lblUsername;

    @FXML
    public void initialize() {
        var usuario = SessaoAtual.getUsuario();
        lblUsername.setText(usuario != null ? usuario.getUsername() : "—");
    }

    @FXML void irRelatorios()  { SceneManager.navegarPara("relatorios"); }
    @FXML void irCategorias()  { SceneManager.navegarPara("categorias"); }

    @FXML
    private void sair() {
        try {
            SessaoAtual.encerrar();
            MainApp.exibirLogin();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao voltar pro login", e);
        }
    }
}
