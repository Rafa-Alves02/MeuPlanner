package br.com.MeuPlanner.app;

import java.io.IOException;

import br.com.MeuPlanner.exception.BusinessException;
import br.com.MeuPlanner.model.Usuario;
import br.com.MeuPlanner.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private final AuthService authService = new AuthService();

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtSenha;
    @FXML private Label lblErro;

    @FXML
    private void entrar() {
        try {
            Usuario usuario = authService.autenticar(txtUsuario.getText(), txtSenha.getText())
                    .orElseThrow(() -> new BusinessException("Usuário ou senha inválidos."));
            SessaoAtual.setUsuario(usuario);
            prosseguir();
        } catch (BusinessException ex) {
            lblErro.setText(ex.getMessage());
        }
    }

    @FXML
    private void criarConta() {
        try {
            Usuario usuario = authService.registrar(txtUsuario.getText(), txtSenha.getText());
            SessaoAtual.setUsuario(usuario);
            prosseguir();
        } catch (BusinessException ex) {
            lblErro.setText(ex.getMessage());
        }
    }

    private void prosseguir() {
        try {
            MainApp.entrarNoSistema();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao abrir o sistema", e);
        }
    }
}
