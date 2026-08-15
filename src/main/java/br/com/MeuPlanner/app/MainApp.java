package br.com.MeuPlanner.app;

import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import atlantafx.base.theme.PrimerDark;
import br.com.MeuPlanner.service.RecorrenciaService;

public class MainApp extends Application {

    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        stage = primaryStage;
        stage.setTitle("MeuPlanner");
        carregarIcone();

        exibirLogin();
    }

    static void exibirLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(MainApp.class.getResource("/css/style.css").toExternalForm());
        stage.setMinWidth(480);
        stage.setMinHeight(420);
        stage.setScene(scene);
        stage.show();
    }

    static void entrarNoSistema() throws IOException {
        new RecorrenciaService().processarRecorrenciasDoMes();

        stage.setMinWidth(960);
        stage.setMinHeight(640);
        SceneManager.init(stage);
        SceneManager.navegarPara("dashboard");
    }

    private static void carregarIcone() throws IOException {
        try (InputStream icone = MainApp.class.getResourceAsStream("/images/icon.png")) {
            if (icone != null) {
                stage.getIcons().add(new Image(icone));
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
