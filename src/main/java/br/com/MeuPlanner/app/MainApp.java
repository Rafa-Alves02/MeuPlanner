package br.com.MeuPlanner.app;

import javafx.application.Application;
import javafx.stage.Stage;

import atlantafx.base.theme.PrimerDark;
import br.com.MeuPlanner.service.RecorrenciaService;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        new RecorrenciaService().processarRecorrenciasDoMes();

        SceneManager.init(stage);
        SceneManager.navegarPara("dashboard");
    }

    public static void main(String[] args) {
        launch(args);
    }
}