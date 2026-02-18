package com.example.javafxtest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class application extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(application.class.getResource("title_screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);   //size of window

        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setTitle("Quax Software System"); //this is for the window title
        stage.setScene(scene);
        stage.show();
    }



}
