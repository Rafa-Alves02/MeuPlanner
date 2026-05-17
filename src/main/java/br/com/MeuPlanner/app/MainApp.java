package br.com.MeuPlanner.app;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        SceneManager.init(stage);
        SceneManager.navegarPara("dashboard");
    }

    public static void main(String[] args) {
        launch(args);
    }
}