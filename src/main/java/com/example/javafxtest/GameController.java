package com.example.javafxtest;

import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Main controller for game
 * manages board, turns, pie rule and win detection.
 */

public class GameController {

    public static int gameMode = -1; // 1=PvB, 2=PvP, -1=none

    @FXML private GridPane gameBoard;
    @FXML private StackPane rootStackPane;
    @FXML Label playerToPlay;
    @FXML Button pieRuleButton;
    @FXML Button pieButtonHelp;
    @FXML Label pieRuleActivated;
    @FXML private Button showStratDM;
    @FXML private Label devModeLabel;

    private final QuaxEngine engine = new QuaxEngine();
    Map<String, Button> buttonMap = new HashMap<>();
    private boolean gameOver = false;
    public int numberMove = 0;
    boolean pieRuleSwapped = false;
    boolean isSecondPlayerHuman = false; // true when human is assigned Black by colour randomisation

    private int[] scheduledBotMove = null;
    private PauseTransition botDelay = null;
    private DevModeHandler devMode;
    private PieRuleHandler pieRule;
    private Timeline escapeTimeline;

    @FXML
    public void initialize() {
        devMode = new DevModeHandler(devModeLabel, showStratDM, buttonMap, engine,
            () -> gameOver, () -> scheduledBotMove, () -> isSecondPlayerHuman, () -> pieRuleSwapped);
        pieRule = new PieRuleHandler(pieRuleButton, pieButtonHelp, pieRuleActivated,
            () -> numberMove, () -> isSecondPlayerHuman);
        Platform.runLater(() -> {
            Scene scene = gameBoard.getScene();
            if (scene != null)
                scene.addEventFilter(KeyEvent.KEY_PRESSED, devMode::handleKeyEvent);
            createBoard();
            pieRule.setup(this::handlePieRuleLogic);
            devMode.setupStratButton();
            randomColourAssign();
        });
    }

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
        BoardBuilder.build(gameBoard, rootStackPane, buttonMap, this::handleMove);
        updateLabel(false);
        gameBoard.setFocusTraversable(true);
        if (escapeTimeline != null) escapeTimeline.stop();
        escapeTimeline = SceneUtils.setupEscapeHold(gameBoard.getScene(), this::navigateToTitle);
    }

    private void handleMove(int x, int y, Button clickedButton) {
        if (gameOver) return;
        if (pieRuleButton.isVisible()) {
            pieRuleButton.setVisible(false);   pieRuleButton.setManaged(false);
            pieButtonHelp.setVisible(false);   pieButtonHelp.setManaged(false);
        }
        if (!engine.placePiece(x, y)) return;
        numberMove++;
        updatePieRuleVisibility();
        updatePieButtonHelpVisibility();

        boolean isBlackMove = engine.getPieceAt(x, y).equals("X");
        clickedButton.getStyleClass().removeAll("whiteButtonPressed", "blackButtonPressed");
        clickedButton.getStyleClass().add(isBlackMove ? "blackButtonPressed" : "whiteButtonPressed");
        updateLabel(isBlackMove);

        String winner = engine.checkWinner();
        if (winner != null) {
            gameOver = true;
            String name;
            if (gameMode == 1) {
                boolean playerIsBlack = !(isSecondPlayerHuman ^ pieRuleSwapped);
                boolean playerWon = (winner.equals("X") && playerIsBlack) || (winner.equals("O") && !playerIsBlack);
                name = playerWon ? "Player" : "Bot";
            } else {
                name = winner.equals("X") ? "Black" : "White";
            }
            playerToPlay.setText(name + " wins!");
            return;
        }
        if (gameMode == 1) {
            String botPiece = (isSecondPlayerHuman ^ pieRuleSwapped) ? "X" : "O";
            if (engine.getCurrentPlayer().equals(botPiece)) scheduleBotMove(botPiece);
        }
    }

    private void scheduleBotMove(String botPiece) {
        int[] move = QuaxBot.chooseBestMove(engine.getBoardCopy(), botPiece);
        if (move == null) return;
        scheduledBotMove = move;
        if (devMode.isActive() && devMode.isStrategyVisible()) devMode.showBotWinPath();
        if (botDelay != null) botDelay.stop();
        botDelay = new PauseTransition(Duration.millis(400));
        botDelay.setOnFinished(e -> {
            if (gameOver) return;
            botDelay = null; scheduledBotMove = null;
            devMode.clearHighlights();
            Button btn = buttonMap.get(move[0] + "," + move[1]);
            if (btn != null) handleMove(move[0], move[1], btn);
            if (!gameOver && devMode.isActive() && devMode.isStrategyVisible()) devMode.showBotWinPath();
        });
        botDelay.play();
    }

    private void updateLabel(boolean lastWasBlack) {
        if (gameMode != 1) { playerToPlay.setText(lastWasBlack ? "White to play" : "Black to play"); return; }
        boolean humanIsBlack = !(isSecondPlayerHuman ^ pieRuleSwapped);
        playerToPlay.setText(lastWasBlack
            ? (humanIsBlack ? "Bot to play (White)" : "Player (White) to play")
            : (humanIsBlack ? "Player (Black) to play" : "Bot to play (Black)"));
    }

    void handlePieRuleLogic() {
        engine.applyPieRule();
        pieRuleSwapped = !pieRuleSwapped;
        numberMove = engine.getMoveCount();
        updateLabel(true);
        updatePieRuleVisibility();
        updatePieButtonHelpVisibility();
        if (gameMode == 1) {
            String botPiece = (isSecondPlayerHuman ^ pieRuleSwapped) ? "X" : "O";
            if (engine.getCurrentPlayer().equals(botPiece)) scheduleBotMove(botPiece);
        }
    }

    void randomColourAssign() {
        isSecondPlayerHuman = (int) (Math.random() * 2) == 1;
        pieRuleSwapped = false;
        updateLabel(false);
        if (isSecondPlayerHuman) scheduleBotMove("X");
    }

    private void navigateToTitle() {
        try { gameMode = -1; SceneUtils.loadFxml((Stage) gameBoard.getScene().getWindow(), getClass(), "title_screen.fxml", "style.css"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    // Thin wrappers so tests can access pie rule behaviour directly
    void setPieRuleButton()            { pieRule.setup(this::handlePieRuleLogic); }
    void setPieRuleHelp()              { pieRule.setup(this::handlePieRuleLogic); }
    void updatePieRuleVisibility()     { pieRule.updateVisibility(); }
    void updatePieButtonHelpVisibility(){ pieRule.updateHelpVisibility(); }
    public QuaxEngine getEngine()      { return engine; }
    void clearWinPathHighlights()      { devMode.clearHighlights(); }

    void resetForTesting() {
        if (botDelay != null) { botDelay.stop(); botDelay = null; }
        scheduledBotMove = null;
        if (devMode != null) devMode.reset();
    }
}
