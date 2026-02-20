package com.example.javafxtest;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class HelloController {

    // Start at 0: first increment → 1 (odd → Black/X plays first)
    private int totalNumberMoves = 0;
    private Timeline exitTimeline;
    private long escapePressTime = 0;
    private Map<String, Button> buttonMap = new HashMap<>();

    public static int gameMode = -1;  // 1 for PvB, 2 for PvP

    @FXML
    public void initialize() {
        System.out.println("Fields ready - gameBoard: " + (gameBoard != null) +
                ", root: " + (rootStackPane != null) +
                ", label: " + (playerToPlay != null));

        exitTimeline = new Timeline(
                new KeyFrame(Duration.millis(100), event -> checkEscapeHold())
        );
        exitTimeline.setCycleCount(Timeline.INDEFINITE);

        Platform.runLater(() -> {
            if (gameBoard == null || rootStackPane == null) {
                System.err.println("FATAL: FXML fields still null after delay! Check module-info.java or FXML path.");
                return;
            }
            createBoard();
        });
    }

    private void checkEscapeHold() {
        if (System.currentTimeMillis() - escapePressTime >= 3000) {
            exitTimeline.stop();
            try {
                switchToTitleScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void switchToTitleScreen() throws IOException {
        Stage stage = (Stage) gameBoard.getScene().getWindow();
        double currentWidth = stage.getScene().getWidth();
        double currentHeight = stage.getScene().getHeight();
        boolean wasMaximized = stage.isMaximized();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("title_screen.fxml"));
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
            gameMode = 1;
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            boolean wasMaximized = stage.isMaximized();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("player_vs_bot_start.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), currentWidth, currentHeight);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

            stage.setScene(scene);
            stage.setMaximized(wasMaximized);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onButtonClickPVP(ActionEvent event) {
        try {
            gameMode = 2;
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            boolean wasMaximized = stage.isMaximized();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("player_vs_player_start.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), currentWidth, currentHeight);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

            stage.setScene(scene);
            stage.setMaximized(wasMaximized);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private GridPane gameBoard;
    public int numberMove = 0;

    // boardLogic uses raw visual coordinates: octagons at (even,even), diamonds at (odd,odd)
    private String[][] boardLogic = new String[22][22];

    @FXML private StackPane rootStackPane;

    private void createBoard() {
        totalNumberMoves = 0;
        buttonMap.clear();
        for (String[] row : boardLogic) Arrays.fill(row, null);

        gameBoard.getChildren().clear();
        gameBoard.getColumnConstraints().clear();
        gameBoard.getRowConstraints().clear();
        gameBoard.setAlignment(Pos.CENTER);
        gameBoard.setHgap(0);
        gameBoard.setVgap(0);
        gameBoard.setPadding(new javafx.geometry.Insets(20));

        // Fix diamond columns/rows to 3px; octagons size themselves from their content
        for (int col = 0; col < 21; col++) {
            ColumnConstraints cc = new ColumnConstraints();
            if (col % 2 == 1) { cc.setMinWidth(3); cc.setPrefWidth(3); cc.setMaxWidth(3); }
            gameBoard.getColumnConstraints().add(cc);
        }
        for (int row = 0; row < 19; row++) {
            RowConstraints rc = new RowConstraints();
            if (row % 2 == 1) { rc.setMinHeight(3); rc.setPrefHeight(3); rc.setMaxHeight(3); }
            gameBoard.getRowConstraints().add(rc);
        }

        // Pass 1: octagons and spacers
        for (int vRow = 0; vRow < 19; vRow++) {
            for (int vCol = 0; vCol < 21; vCol++) {
                if (vRow % 2 == 0 && vCol % 2 == 0) {
                    Button oct = new Button();
                    oct.getStyleClass().add("board-tile");
                    bindSize(oct, 22);
                    final int logicX = vCol;
                    final int logicY = vRow;
                    buttonMap.put(logicX + "," + logicY, oct);
                    oct.setOnAction(e -> handleMove(logicX, logicY, oct));
                    gameBoard.add(oct, vCol, vRow);
                } else if (!(vRow % 2 == 1 && vCol % 2 == 1)) {
                    Region spacer = new Region();
                    spacer.setPrefSize(0, 0);
                    spacer.setPickOnBounds(false);
                    gameBoard.add(spacer, vCol, vRow);
                }
            }
        }

        // Pass 2: diamonds last so they render on top and sit in the corner gaps
        for (int vRow = 1; vRow < 19; vRow += 2) {
            for (int vCol = 1; vCol < 21; vCol += 2) {
                Button dia = new Button();
                dia.getStyleClass().add("diamond-tile");
                bindSize(dia, 32);
                GridPane.setFillWidth(dia, false);
                GridPane.setFillHeight(dia, false);
                GridPane.setHalignment(dia, HPos.CENTER);
                GridPane.setValignment(dia, VPos.CENTER);
                final int logicX = vCol;
                final int logicY = vRow;
                buttonMap.put(logicX + "," + logicY, dia);
                dia.setOnAction(e -> handleMove(logicX, logicY, dia));
                gameBoard.add(dia, vCol, vRow);
            }
        }

        gameBoard.setOnMouseClicked(e -> gameBoard.requestFocus());
        gameBoard.setFocusTraversable(true);

        // Show who plays first
        playerToPlay.setManaged(true);
        playerToPlay.setVisible(true);
        if (gameMode == 1) {
            playerToPlay.setText("Player (Black) to play");
        } else {
            playerToPlay.setText("Black to play");
        }
    }

    private void bindSize(Button btn, double divisor) {
        btn.prefWidthProperty().bind(rootStackPane.heightProperty().divide(divisor));
        btn.prefHeightProperty().bind(rootStackPane.heightProperty().divide(divisor));
        btn.minWidthProperty().bind(btn.prefWidthProperty());
        btn.minHeightProperty().bind(btn.prefHeightProperty());
        btn.setFocusTraversable(false);
    }

    @FXML private Label playerToPlay;

    private void handleMove(int x, int y, Button clickedButton) {
        if (boardLogic[x][y] != null) {
            return;
        }

        totalNumberMoves++;
        // Odd move count = Black (X), Even move count = White (O)
        boolean isBlackMove = (totalNumberMoves % 2 == 1);
        String playerPiece = isBlackMove ? "X" : "O";
        String moveClass   = isBlackMove ? "blackButtonPressed" : "whiteButtonPressed";
        String nextPlayer  = isBlackMove ? "White" : "Black";

        boardLogic[x][y] = playerPiece;

        clickedButton.getStyleClass().removeAll("whiteButtonPressed", "blackButtonPressed");
        clickedButton.getStyleClass().add(moveClass);
        clickedButton.applyCss();

        System.out.println(playerPiece + " placed at (" + x + "," + y + ") gamemode: " + gameMode);

        // Update "who plays next" label
        if (gameMode == 1) {
            // PvB: Black = Player, White = Bot
            playerToPlay.setText(isBlackMove ? "Bot to play" : "Player (Black) to play");
        } else {
            playerToPlay.setText(nextPlayer + " to play");
        }

    }

} // end of class