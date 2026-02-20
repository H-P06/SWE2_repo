package com.example.javafxtest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class application extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Add the leading / and the full package path
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/javafxtest/title_screen.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        // Do the same for the CSS
        scene.getStylesheets().add(getClass().getResource("/com/example/javafxtest/style.css").toExternalForm());

        stage.setTitle("Quax Software System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }



}
