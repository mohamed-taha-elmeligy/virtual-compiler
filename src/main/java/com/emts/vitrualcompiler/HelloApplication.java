package com.emts.vitrualcompiler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
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
        // حجم النافذة
        stage.setWidth(1400);
        stage.setHeight(900);
        stage.setMinWidth(1000);
        stage.setMinHeight(700);

        // مركز النافذة على الشاشة
        centerWindowOnScreen(stage);
        stage.show();
    }

    private void centerWindowOnScreen(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }
    public static void main(String[] args) {
        launch();
    }
}