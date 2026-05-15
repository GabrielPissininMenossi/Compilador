package com.unoeste.compiladores;

import com.unoeste.compiladores.entities.stacks.NoPilhaGenerico;
import com.unoeste.compiladores.entities.stacks.PilhaGenerica;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1024, 600);
        scene.getStylesheets().add(getClass().getResource("/css/claro.css").toExternalForm());

        //stage.setTitle("Compilador");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
//        PilhaGenerica<String> pilha = new PilhaGenerica<>();
//        pilha.push("1");
//        String elemento = pilha.pop().getInfo();

        launch();
    }
}