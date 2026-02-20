package com.example.javafxtest;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.util.Objects;

import javafx.event.ActionEvent;//for swapping scenes
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class HelloController {

    private int totalNumberMoves;
    private Timeline exitTimeline;


    public static int gameMode = -1;  //1 for PvB 2 for PvP

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
            //set the gamemode to playervsbot
            gameMode = 1;
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
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());


            stage.setScene(scene);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onButtonClickPVP(ActionEvent event) {
        try {
            gameMode = 2;

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

    //private String[][] boardLogic = new String[11][11];
    private String[][] boardLogic = new String[21][21];

    @FXML private StackPane rootStackPane; // No longer red!

    private void createBoard() {
// 1. Reset and Setup
        gameBoard.getChildren().clear();
        gameBoard.setAlignment(Pos.CENTER);
        gameBoard.setHgap(0);
        gameBoard.setVgap(0);
        gameBoard.setPadding(new javafx.geometry.Insets(20));

        // 2. Build the 21x21 Mosaic
        for (int row = 0; row < 21; row++) {
            for (int col = 0; col < 21; col++) {

                // --- CASE 1: OCTAGONS (The Playable Tiles) ---
                // These occupy Even Rows and Even Columns (0,0), (0,2), (2,0)...
                if (row % 2 == 0 && col % 2 == 0) {
                    Button tile = new Button();
                    tile.getStyleClass().add("board-tile");

                    // Responsive Size Binding
                    tile.prefWidthProperty().bind(rootStackPane.heightProperty().divide(25));
                    tile.prefHeightProperty().bind(rootStackPane.heightProperty().divide(25));
                    tile.minWidthProperty().bind(tile.prefWidthProperty());
                    tile.minHeightProperty().bind(tile.prefHeightProperty());

                    int finalRow = row; // Use the raw 0-20 value
                    int finalCol = col;
                    tile.setOnAction(e -> handleMove(finalRow, finalCol, tile));

                    gameBoard.add(tile, col, row);
                }

                // --- diamonds ---
                else if (row % 2 != 0 && col % 2 != 0) {
                    Button diamond = new Button();
                    diamond.getStyleClass().add("diamond-tile");

                    // Responsive Size (Slightly larger than the gap to look connected)
                    diamond.prefWidthProperty().bind(rootStackPane.heightProperty().divide(40));
                    diamond.prefHeightProperty().bind(rootStackPane.heightProperty().divide(40));
                    diamond.minWidthProperty().bind(diamond.prefWidthProperty());
                    diamond.minHeightProperty().bind(diamond.prefHeightProperty());

                    diamond.setRotate(45);

                    // For now, diamonds are just visual/buttons.
                    // We don't call handleMove to avoid overlapping the 11x11 logic.
                    int finalRow = row;
                    int finalCol = col;
                    diamond.setOnAction(e -> handleMove(finalRow, finalCol, diamond));


                    gameBoard.add(diamond, col, row);
                }

                // These fill the empty spaces between an octagon and a diamond
                else {
                    Region spacer = new Region();
                    spacer.setPrefSize(0, 0);
                    gameBoard.add(spacer, col, row);
                }
            }
        }

        // 3. Keyboard Listener for the Escape Exit Feature
        gameBoard.setFocusTraversable(true);
        gameBoard.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                if (exitTimeline.getStatus() != Animation.Status.RUNNING) {
                    exitTimeline.playFromStart();
                }
            }
        });

        gameBoard.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                exitTimeline.stop();
            }
        });


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

    //needed for the label
    @FXML private Label playerToPlay;

    @FXML
    private void handleMove(int row, int col, Button clickedButton) {
        if(boardLogic[row][col] == null){   //for empty spot
            totalNumberMoves++;

            //white is o, black is x
            //meaning it's even (white should play)
            if(totalNumberMoves % 2 == 0){
                if(gameMode == 1){
                    playerToPlay.setManaged(true);
                    playerToPlay.setVisible(true);
                    playerToPlay.setText("Player (Black) to play");
                }
                if(gameMode == 2){
                    playerToPlay.setManaged(true);
                    playerToPlay.setVisible(true);
                    playerToPlay.setText("Black to play");
                }

                boardLogic[row][col] = "O";
                    clickedButton.getStyleClass().removeAll("whiteButtonPressed", "focus");
                    //In this way you're sure you have no styles applied to your object button
                    clickedButton.getStyleClass().add("whiteButtonPressed");
                    //then you specify the class you would give to the button
                System.out.println("White placed in " + row + " " + col + " gamemode: " + gameMode);   //for us to see it worked

            }
            else{
                if(gameMode == 1){
                    playerToPlay.setManaged(true);
                    playerToPlay.setVisible(true);
                    playerToPlay.setText("Bot to play");
                }
                if(gameMode == 2){
                    playerToPlay.setManaged(true);
                    playerToPlay.setVisible(true);
                    playerToPlay.setText("White to play");
                }

                boardLogic[row][col] = "X";
                    clickedButton.getStyleClass().removeAll("blackButtonPressed, focus");
                    //In this way you're sure you have no styles applied to your object button
                    clickedButton.getStyleClass().add("blackButtonPressed");
                    //then you specify the class you would give to the button
                System.out.println("Black placed in " + row + " " + col + " gamemode: " + gameMode);   //for us to see it worked

            }
        }
        else{
            System.out.println("invalid move DEBUG: " + row + " " + col);
        }
    }


}//end of class
