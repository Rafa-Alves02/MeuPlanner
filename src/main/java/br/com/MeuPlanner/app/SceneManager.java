package br.com.MeuPlanner.app;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneManager {

    private static Stage stage;
    private static Parent shellRoot;
    private static StackPane conteudoHost;
    private static NavbarController navbarController;
    private static String telaAtual;

    public static void init(Stage primaryStage) throws IOException {
        stage = primaryStage;

        FXMLLoader shellLoader = new FXMLLoader(SceneManager.class.getResource("/fxml/app-shell.fxml"));
        shellRoot = shellLoader.load();
        AppShellController shellController = shellLoader.getController();
        conteudoHost = shellController.getConteudoHost();

        aplicarCorAcento(TemaPreferences.corAcentoSalva());

        Scene scene = new Scene(shellRoot, 1080, 700);
        scene.getStylesheets().add(SceneManager.class.getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void navegarPara(String tela) {
        try {
            telaAtual = tela;
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/" + tela + ".fxml"));
            Parent conteudo = loader.load();
            trocarConteudo(conteudo);

            if (navbarController != null) {
                navbarController.marcarAtiva(tela);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar tela: " + tela, e);
        }
    }

    static void registrarNavbar(NavbarController controller) {
        navbarController = controller;
    }

    static void aplicarCorAcento(String corHex) {
        if (shellRoot != null) {
            shellRoot.setStyle("-accent-cor: " + corHex + ";");
        }
    }

    public static String getTelaAtual() {
        return telaAtual;
    }

    private static void trocarConteudo(Parent novoConteudo) {
        novoConteudo.setOpacity(0);

        if (conteudoHost.getChildren().isEmpty()) {
            conteudoHost.getChildren().add(novoConteudo);
            fade(novoConteudo, 0, 1, 180, null);
            return;
        }

        Node atual = conteudoHost.getChildren().get(0);
        fade(atual, 1, 0, 120, () -> {
            conteudoHost.getChildren().setAll(novoConteudo);
            fade(novoConteudo, 0, 1, 160, null);
        });
    }

    private static void fade(Node node, double de, double para, int millis, Runnable aoTerminar) {
        FadeTransition transicao = new FadeTransition(Duration.millis(millis), node);
        transicao.setFromValue(de);
        transicao.setToValue(para);
        if (aoTerminar != null) {
            transicao.setOnFinished(e -> aoTerminar.run());
        }
        transicao.play();
    }
}
