package com.example.javafxtest;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.event.ActionEvent;//for swapping scenes
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class HelloController {

    private int totalNumberMoves;

    // Inside your Controller class
    private Timeline exitTimeline;

    @FXML
    public void initialize() {
        //make timer for exit 3 second feature
        exitTimeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            try {
                switchToTitleScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        exitTimeline.setCycleCount(1);

        //make board
        if (gameBoard != null) {
            createBoard();
        }
    }

    private void switchToTitleScreen() throws IOException {
        Stage stage = (Stage) gameBoard.getScene().getWindow();

        // 1. Get the size of the CONTENT, not the whole window
        double currentWidth = stage.getScene().getWidth();
        double currentHeight = stage.getScene().getHeight();
        boolean wasMaximized = stage.isMaximized();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("title_screen.fxml"));

        // 2. This now matches perfectly without adding border height
        Scene scene = new Scene(fxmlLoader.load(), currentWidth, currentHeight);

        if (getClass().getResource("style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        }

        stage.setScene(scene);
        stage.setMaximized(wasMaximized);
    }


    @FXML
    protected void onButtonClickPVB(ActionEvent event) {
        try {

            //keep the current dimensions of the stage when switching
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            boolean wasMaximized = stage.isMaximized();

            //load fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("player_vs_bot_start.fxml"));

            //amke the scene with the dimensions
            Scene scene = new Scene(fxmlLoader.load(), currentWidth, currentHeight);

            //linking style.css
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());


            stage.setScene(scene);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onButtonClickPVP(ActionEvent event) {
        try {

            //keep the current dimensions of the stage when switching
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            boolean wasMaximized = stage.isMaximized();

            //load fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("player_vs_player_start.fxml"));

            //amke the scene with the dimensions
            Scene scene = new Scene(fxmlLoader.load(), currentWidth, currentHeight);

            //linking style.css
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());


            stage.setScene(scene);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private GridPane gameBoard;
    public int numberMove = 0;

    private String[][] boardLogic = new String[11][11];


    private void createBoard() {
        for (int row = 0; row < 11; row++) {
            for (int col = 0; col < 11; col++) {

                //each tile is a button
                Button tile = new Button();
                tile.setPrefSize(50, 50);
                tile.setMinSize(50, 50);
                tile.setMaxSize(50, 50);

                //turn this into a css class (for consistency)
                tile.getStyleClass().add("board-tile");

                //interaction with button
                int finalRow = row;
                int finalCol = col;
                tile.setOnAction(event -> handleMove(finalRow, finalCol, tile));


                //Add to GridPane
                gameBoard.add(tile, col, row);
            }
        }


        //for the exit feature
        gameBoard.setFocusTraversable(true); // Allow the board to "hear" the keyboard

        gameBoard.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                // If they aren't already holding it, start the countdown
                if (exitTimeline.getStatus() != Animation.Status.RUNNING) {
                    exitTimeline.playFromStart();
                    System.out.println("Holding Escape...");

                }
            }
        });

        gameBoard.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                // If they let go before 3 seconds, cancel the countdown!
                exitTimeline.stop();
                System.out.println("Escape released too early.");
            }
        });

    }

    @FXML
    private void handleMove(int row, int col, Button clickedButton) {
        if(boardLogic[row][col] == null){   //for empty spot
            totalNumberMoves++;

            //white is o, black is x
            //meaning it's even (white should play)
            if(totalNumberMoves % 2 == 0){
                boardLogic[row][col] = "O";
                    clickedButton.getStyleClass().removeAll("whiteButtonPressed, focus");
                    //In this way you're sure you have no styles applied to your object button
                    clickedButton.getStyleClass().add("whiteButtonPressed");
                    //then you specify the class you would give to the button

                System.out.println("White placed in " + row + " " + col);   //for us to see it worked
            }
            else{
                boardLogic[row][col] = "X";
                    clickedButton.getStyleClass().removeAll("blackButtonPressed, focus");
                    //In this way you're sure you have no styles applied to your object button
                    clickedButton.getStyleClass().add("blackButtonPressed");
                    //then you specify the class you would give to the button
                System.out.println("Black placed in " + row + " " + col);   //for us to see it worked
            }
        }
        else{
            System.out.println("invalid move");
        }
    }


}//end of class
