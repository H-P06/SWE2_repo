package com.example.javafxtest;

import javafx.animation.Animation;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class HelloController {

    private Timeline exitTimeline;
    private long exitPressTime = 0;
    Map<String, Button> buttonMap = new HashMap<>();
    private QuaxEngine engine = new QuaxEngine();


    public static int gameMode = -1;  //1 for PvB, 2 for PvP

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (gameBoard == null || rootStackPane == null) {
                return;
            }
            createBoard();
            setPieRuleButton();
        });
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

    @FXML private StackPane rootStackPane;

    private void createBoard() {
        engine.reset(); // Clear the logic
        buttonMap.clear();
        gameBoard.getChildren().clear();
        gameBoard.getColumnConstraints().clear();
        gameBoard.getRowConstraints().clear();
        gameBoard.setAlignment(Pos.CENTER);
        gameBoard.setPadding(new javafx.geometry.Insets(10));

        //Setup Constraints
        for (int col = 0; col < 21; col++) {
            ColumnConstraints cc = new ColumnConstraints();
            if (col % 2 == 1) { cc.setPrefWidth(3); cc.setMaxWidth(3); }
            gameBoard.getColumnConstraints().add(cc);
        }
        for (int row = 0; row < 21; row++) {
            RowConstraints rc = new RowConstraints();
            if (row % 2 == 1) { rc.setPrefHeight(3); rc.setMaxHeight(3); }
            gameBoard.getRowConstraints().add(rc);
        }

        // Create Octagons and Diamonds
        for (int vRow = 0; vRow < 21; vRow++) {
            for (int vCol = 0; vCol < 21; vCol++) {
                if (vRow % 2 == 0 && vCol % 2 == 0) {
                    createTile(vCol, vRow, "board-tile", 30);
                } else if (vRow % 2 == 1 && vCol % 2 == 1) {
                    createTile(vCol, vRow, "diamond-tile", 42);
                }
            }
        }

        updateLabel(false);
        gameBoard.setFocusTraversable(true);
        setupKeyHandlers();
    }

    private void createTile(int x, int y, String styleClass, double divisor) {
        Button btn = new Button();
        btn.getStyleClass().add(styleClass);
        bindSize(btn, divisor);

        if (styleClass.equals("diamond-tile")) {
            GridPane.setHalignment(btn, HPos.CENTER);
            GridPane.setValignment(btn, VPos.CENTER);
        }

        btn.setOnAction(e -> handleMove(x, y, btn));
        gameBoard.add(btn, x, y);
        buttonMap.put(x + "," + y, btn);
    }

    private void updateLabel(boolean lastWasBlack) {
        if (gameMode == 1) {
            playerToPlay.setText(lastWasBlack ? "Bot to play" : "Player (Black) to play");
        } else {
            playerToPlay.setText(lastWasBlack ? "White to play" : "Black to play");
        }
    }

    private void bindSize(Button btn, double divisor) {
        btn.prefWidthProperty().bind(rootStackPane.heightProperty().divide(divisor));
        btn.prefHeightProperty().bind(rootStackPane.heightProperty().divide(divisor));
        btn.minWidthProperty().bind(btn.prefWidthProperty());
        btn.minHeightProperty().bind(btn.prefHeightProperty());
        btn.setFocusTraversable(false);
    }

    @FXML Label playerToPlay;

    private void handleMove(int x, int y, Button clickedButton) {
        System.out.println("Move: " + engine.getMoveCount());
        boolean success = engine.placePiece(x, y);

        if (!success) return; // valid move or spot taken

        numberMove++;
        updatePieRuleVisibility();



        //result from the engine to update the UI
        String piece = engine.getPieceAt(x, y);
        boolean isBlackMove = piece.equals("X");

        String moveClass = isBlackMove ? "blackButtonPressed" : "whiteButtonPressed";
        String nextPlayer = isBlackMove ? "White" : "Black";

        clickedButton.getStyleClass().removeAll("whiteButtonPressed", "blackButtonPressed");
        clickedButton.getStyleClass().add(moveClass);

        //Update label (keep your existing gameMode logic)
        if (gameMode == 1) {
            playerToPlay.setText(isBlackMove ? "Bot to play" : "Player (Black) to play");
        } else {
            playerToPlay.setText(nextPlayer + " to play");
        }

    }

    private void setupKeyHandlers() {
        gameBoard.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                exitPressTime = System.currentTimeMillis();
                if (exitTimeline.getStatus() != Animation.Status.RUNNING) {
                    exitTimeline.play();
                }
            }
        });

        gameBoard.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                exitTimeline.stop();
            }
        });
    }


    @FXML Button pieRuleButton;

    void updatePieRuleVisibility() {
        if (numberMove == 1) {
            pieRuleButton.setVisible(true);
            pieRuleButton.setManaged(true);
            pieRuleButton.setText("Activate Pie Rule");
        } else {//when pie rule window is over
            pieRuleButton.setVisible(false);
            pieRuleButton.setManaged(false);
        }
    }

    void setPieRuleButton() {
        pieRuleButton.setVisible(false);
        pieRuleButton.setManaged(false);

        pieRuleButton.setOnAction(event -> {
            System.out.println("Pie Rule Activated!");
            System.out.println("no moves: " + numberMove);
            handlePieRuleLogic();
            updatePieRuleVisibility();
        });
    }

    void handlePieRuleLogic(){
        engine.applyPieRule();

        for (Map.Entry<String, Button> entry : buttonMap.entrySet()) {
            Button btn = entry.getValue();
            if (btn.getStyleClass().contains("blackButtonPressed")) {
                btn.getStyleClass().remove("blackButtonPressed");
                btn.getStyleClass().add("whiteButtonPressed");
                break;
            }
        }

        this.numberMove = engine.getMoveCount();
        updateLabel(false);

        //so button is gone
        updatePieRuleVisibility();
    }

    //will be used for test
    public QuaxEngine getEngine() {
        return  this.engine;
    }
} // end of class