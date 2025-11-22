package com.emts.vitrualcompiler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("/com/emts/vitrualcompiler/hello-view.fxml")
        );
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 1200, 800);

        scene.getStylesheets().add(String.valueOf(getClass().getResource("/style.css")));
        stage.setMaximized(true);
        stage.setTitle("Compiler IDE - Professional");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}