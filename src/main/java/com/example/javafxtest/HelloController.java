package com.example.javafxtest;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.event.ActionEvent;//for swapping scenes
import javafx.scene.Node;
import javafx.beans.property.*;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class HelloController {

    @FXML
    protected void onButtonClickPVB(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("player_vs_bot_start.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            //linking style sheet
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

            //load and show stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
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


            //load and show stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private GridPane gameBoard;

    private String[][] boardLogic = new String[11][11];

    @FXML
    public void initialize() {
        // Only build the board if we are actually on the board screen
        if (gameBoard != null) {
            createBoard();
        }

    }

    private void createBoard() {
        for (int row = 0; row < 11; row++) {
            for (int col = 0; col < 11; col++) {

                //each tile is a button
                Button tile = new Button();
                tile.setPrefSize(70, 70);

                //turn this into a class (for consistency)
                tile.getStyleClass().add("board-tile");

                //Add to GridPane
                gameBoard.add(tile, col, row);
            }
        }
    }


}//end of class
