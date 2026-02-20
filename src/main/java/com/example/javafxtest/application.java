package com.example.javafxtest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class application extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/javafxtest/title_screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        //link style sheet
        scene.getStylesheets().add(getClass().getResource("/com/example/javafxtest/style.css").toExternalForm());

        //label
        stage.setTitle("Quax Software System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args); //set up as javafx application
    }



}
