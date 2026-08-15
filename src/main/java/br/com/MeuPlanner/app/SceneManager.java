package br.com.MeuPlanner.app;

import java.io.IOException;
import java.io.InputStream;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneManager {

    private static Stage stage;
    private static String telaAtual;

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("MeuPlanner");
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        try (InputStream icone = SceneManager.class.getResourceAsStream("/images/icon.png")) {
            if (icone != null) {
                stage.getIcons().add(new Image(icone));
            }
        } catch (IOException ignored) {
        }
    }

    public static void navegarPara(String tela) {
        try {
            telaAtual = tela;
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("/fxml/" + tela + ".fxml"));
            Parent conteudo = loader.load();

            Region scanlines = new Region();
            scanlines.setMouseTransparent(true);
            scanlines.getStyleClass().add("scanline-overlay");

            StackPane raiz = new StackPane(conteudo, scanlines);
            raiz.setOpacity(0);

            Scene scene = new Scene(raiz);
            scene.getStylesheets().add(
                    SceneManager.class.getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();

            FadeTransition fade = new FadeTransition(Duration.millis(160), raiz);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar tela: " + tela, e);
        }
    }

    public static String getTelaAtual() {
        return telaAtual;
    }
}