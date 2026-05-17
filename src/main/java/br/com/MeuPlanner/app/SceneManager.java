package br.com.MeuPlanner.app;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static Stage stage;

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("MeuPlanner");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
    }

    public static void navegarPara(String tela) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("/fxml/" + tela + ".fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    SceneManager.class.getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar tela: " + tela, e);
        }
    }
}