package com.example.javafxtest;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class HelloController {

    Map<String, Button> buttonMap = new HashMap<>();
    private QuaxEngine engine = new QuaxEngine();
    private boolean gameOver = false;
    boolean pieRuleSwapped = false;
    boolean isSecondPlayerHuman = pieRuleSwapped;


    @FXML private Button showStratDM;

    //THE CODE TO TURN ON DEV MODE
    private final List<KeyCode> SECRET_CODE = List.of(KeyCode.D, KeyCode.E, KeyCode.V);
    private int codeIndex = 0;
    @FXML private Label devModeLabel;

    private Timeline escapeTimeline;



    public static int gameMode = -1;  //1 for PvB, 2 for PvP

    @FXML VBox titleRoot;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Scene scene = null;
            if (gameBoard != null && gameBoard.getScene() != null) {
                scene = gameBoard.getScene();
            } else if (titleRoot != null && titleRoot.getScene() != null) {
                scene = titleRoot.getScene();
            }

            if (scene != null) {
                setupEscapeExit(scene, Platform::exit);

                scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleDevModeCode);
            }

            if (gameBoard != null && rootStackPane != null) {
                createBoard();
                setPieRuleButton();
                setPieRuleHelp();
                randomColourAssign();
            }
        });
    }


    @FXML
    protected void onButtonClickPVB(ActionEvent event) {
        try {
            gameMode = 1;
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Quax - Player vs Bot");

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
    private GridPane gameBoard;
    public int numberMove = 0;

    @FXML private StackPane rootStackPane;

    private void createBoard() {
        gameBoard.setMaxWidth(Region.USE_PREF_SIZE);
        gameBoard.setMaxHeight(Region.USE_PREF_SIZE);
        engine.reset();
        gameOver = false;
        pieRuleSwapped = false;
        buttonMap.clear();
        gameBoard.getChildren().clear();
        gameBoard.getColumnConstraints().clear();
        gameBoard.getRowConstraints().clear();
        gameBoard.setAlignment(Pos.CENTER);

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

        if (x == 0 && y == 0) {
            btn.setStyle("-fx-background-color: #000000, #ffffff, #725242; -fx-background-insets: 0, 3 0 0 0, 3 0 0 3;");
        } else if (x == 20 && y == 0) {
            btn.setStyle("-fx-background-color: #000000, #ffffff, #725242; -fx-background-insets: 0, 3 0 0 0, 3 3 0 0;");
        } else if (x == 0 && y == 20) {
            btn.setStyle("-fx-background-color: #000000, #ffffff, #725242; -fx-background-insets: 0, 0 0 3 0, 0 0 3 3;");
        } else if (x == 20 && y == 20) {
            btn.setStyle("-fx-background-color: #000000, #ffffff, #725242; -fx-background-insets: 0, 0 0 3 0, 0 3 3 0;");
        } else if (y == 0) {
            btn.setStyle("-fx-background-color: #000000, #725242; -fx-background-insets: 0, 3 0 0 0;");
        } else if (y == 20) {
            btn.setStyle("-fx-background-color: #000000, #725242; -fx-background-insets: 0, 0 0 3 0;");
        } else if (x == 0) {
            btn.setStyle("-fx-background-color: #ffffff, #725242; -fx-background-insets: 0, 0 0 0 3;");
        } else if (x == 20) {
            btn.setStyle("-fx-background-color: #ffffff, #725242; -fx-background-insets: 0, 0 3 0 0;");
        }

        btn.setOnAction(e -> handleMove(x, y, btn));
        gameBoard.add(btn, x, y);
        buttonMap.put(x + "," + y, btn);
    }

    private void updateLabel(boolean lastWasBlack) {
        if (gameMode == 1) {
            boolean humanIsCurrentlyBlack = !(isSecondPlayerHuman ^ pieRuleSwapped);

            if (lastWasBlack) {
                playerToPlay.setText(humanIsCurrentlyBlack ? "Bot to play (White)" : "Player (White) to play");
            } else {
                playerToPlay.setText(humanIsCurrentlyBlack ? "Player (Black) to play" : "Bot to play (Black)");
            }
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

        if (gameOver) return;

        if (pieRuleButton.isVisible()) {
            pieRuleButton.setVisible(false);
            pieButtonHelp.setVisible(false);
        }

        boolean success = engine.placePiece(x, y);
        if (!success) return;

        numberMove++;
        updatePieRuleVisibility();
        updatePieButtonHelpVisibility();



        String piece = engine.getPieceAt(x, y);
        boolean isBlackMove = piece.equals("X");

        String moveClass = isBlackMove ? "blackButtonPressed" : "whiteButtonPressed";
        String nextPlayer = isBlackMove ? "White" : "Black";

        clickedButton.getStyleClass().removeAll("whiteButtonPressed", "blackButtonPressed");
        clickedButton.getStyleClass().add(moveClass);

        updateLabel(isBlackMove);

        String winner = engine.checkWinner();
        if (winner != null) {
            gameOver = true;
            String winnerName;
            if (gameMode == 1) {
                boolean playerIsBlack = !(isSecondPlayerHuman ^ pieRuleSwapped);

                boolean playerWon = (winner.equals("X") && playerIsBlack) || (winner.equals("O") && !playerIsBlack);
                winnerName = playerWon ? "Player" : "Bot";
            } else {
                winnerName = winner.equals("X") ? "Black" : "White";
            }
            playerToPlay.setText(winnerName + " wins!");
            return;
        }

        if (gameMode == 1) {
            String botPiece = (isSecondPlayerHuman ^ pieRuleSwapped) ? "X" : "O";

            if (engine.getCurrentPlayer().equals(botPiece)) {
                scheduleBotMove(botPiece);
            }
        }
    }

    private void scheduleBotMove(String botPiece) {
        String[][] boardSnapshot = engine.getBoardCopy();
        int[] move = QuaxBot.chooseBestMove(boardSnapshot, botPiece);
        if (move == null) return;

        PauseTransition delay = new PauseTransition(Duration.millis(400));
        delay.setOnFinished(e -> {
            if (gameOver) return;
            Button btn = buttonMap.get(move[0] + "," + move[1]);
            if (btn != null) {
                handleMove(move[0], move[1], btn);
            }
        });
        delay.play();
    }

    private void setupKeyHandlers() {
        setupEscapeExit(gameBoard.getScene(), this::navigateToTitle);
    }

    private void setupEscapeExit(Scene scene, Runnable onExit) {
        if (scene == null) return;

        // 1. If a timer already exists, stop it before making a new one
        if (escapeTimeline != null) {
            escapeTimeline.stop();
        }

        // 2. Define the timer
        escapeTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            onExit.run();
        }));

        // 3. Use setOnKey... (this replaces any existing escape listeners)
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE && escapeTimeline.getStatus() != Animation.Status.RUNNING) {
                escapeTimeline.playFromStart();
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                escapeTimeline.stop();
            }
        });
    }

    private void navigateToTitle() {
        try {
            Stage stage = (Stage) gameBoard.getScene().getWindow();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            boolean wasMaximized = stage.isMaximized();
            gameMode = -1;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("title_screen.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(fxmlLoader.load(), currentWidth, currentHeight);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(wasMaximized);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML Button pieRuleButton;
    @FXML Button pieButtonHelp;
    @FXML Label pieRuleActivated;

    void updatePieRuleVisibility() {
        if (numberMove == 1 && isSecondPlayerHuman) {
            pieRuleButton.setVisible(true);
            pieRuleButton.setText("Activate Pie Rule");
        } else {//when pie rule window is over
            pieRuleButton.setVisible(false);
        }
    }

    //for the question mark button
    void updatePieButtonHelpVisibility() {
        if (numberMove == 1 && isSecondPlayerHuman) {
            pieButtonHelp.setVisible(true);
            pieButtonHelp.setText("?");
        } else {//when pie rule window is over
            pieButtonHelp.setVisible(false);
        }
    }

    void setPieRuleButton() {
        pieRuleButton.setVisible(false);
        pieRuleButton.setVisible(false);

        pieRuleButton.setOnAction(event -> {
            pieRuleActivated.setText("Pie Rule Activated!");
            pieRuleActivated.setVisible(true);

            //3 second wait
            PauseTransition delay = new PauseTransition(Duration.seconds(3));

            delay.setOnFinished(e -> pieRuleActivated.setVisible(false));

            delay.play();

            handlePieRuleLogic();
            updatePieRuleVisibility();
        });
    }

    void setPieRuleHelp() {
        pieButtonHelp.setVisible(false);
        pieButtonHelp.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Rules");
            alert.setHeaderText("The Pie Rule");
            alert.setContentText("The second player can choose to swap with the first player.\nThis balances the advantage the first move holds.");

            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.getDialogPane().setPrefWidth(400);

            alert.showAndWait();

            updatePieButtonHelpVisibility();
        });
    }

    void handlePieRuleLogic(){
        engine.applyPieRule();
        pieRuleSwapped = !pieRuleSwapped;

        this.numberMove = engine.getMoveCount();
        updateLabel(true); // Black (X) just moved, so lastWasBlack = true

        updatePieRuleVisibility();
        updatePieButtonHelpVisibility();

        if (gameMode == 1) {
            // After a swap, if the Human is now Black (X), the Bot must be White (O)
            String botPiece = (isSecondPlayerHuman ^ pieRuleSwapped) ? "X" : "O";

            if (engine.getCurrentPlayer().equals(botPiece)) {
                scheduleBotMove(botPiece);
            }
        }
    }

    //will be used for test
    public QuaxEngine getEngine() {
        return  this.engine;
    }


    //random assign white or black
    void randomColourAssign() {
        int randomNum = (int)(Math.random() * 2);

        if (randomNum == 0) {
            isSecondPlayerHuman = false;
            pieRuleSwapped = false;
            updateLabel(false);
        } else {
            isSecondPlayerHuman = true;
            pieRuleSwapped = false;
            updateLabel(false);

            // Bot takes the first move as Black (X)
            scheduleBotMove("X");
        }
    }

    private void handleDevModeCode(KeyEvent event) {
        //exit dev mode here
        if (event.getCode() == KeyCode.LEFT) {
            if (showStratDM != null && showStratDM.isVisible()) {
                showStratDM.setVisible(false);
                showStratDM.setManaged(false);

                devModeLabel.setVisible(false);
                devModeLabel.setManaged(false);
            }
            codeIndex = 0;
            return;
        }

        if (event.getCode() == SECRET_CODE.get(codeIndex)) {
            codeIndex++;
            if (codeIndex == SECRET_CODE.size()) {
                if (showStratDM != null) {
                    showStratDM.setVisible(true);
                    showStratDM.setManaged(true);
                    showStratDM.setText("Show strategy");
                    devModeLabel.setText("Development Mode");
                    devModeLabel.setVisible(true);
                    devModeLabel.setManaged(true);
                }
                codeIndex = 0;
            }
        } else {
            // Only reset if they press a key that isn't the start of the code
            if (event.getCode() == SECRET_CODE.get(0)) {
                codeIndex = 1;
            } else {
                codeIndex = 0;
            }
        }
    }



} // end of class