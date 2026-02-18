package com.example.javafxtest;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.event.ActionEvent;//for swapping scenes
import javafx.scene.Node;


public class HelloController {
    @FXML
    private Label welcomeText;


    @FXML
    protected void onButtonClickPVB(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("player_vs_bot_start.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // 1. Get the current Stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            //make new scene
            stage.setScene(scene);

            //force it to be fullscreen (for board)
            stage.setFullScreen(true);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onButtonClickPVP(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("player_vs_player_start.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);

            stage.setFullScreen(true);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}//end of hellocontroller class
