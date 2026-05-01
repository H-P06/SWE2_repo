package com.example.javafxtest;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

// secret code to activate dev mode: dev
class DevModeHandler {

    private static final List<KeyCode> SECRET_CODE = List.of(KeyCode.D, KeyCode.E, KeyCode.V);

    private final Label devModeLabel;
    private final Button showStratDM;
    private final Map<String, Button> buttonMap;
    private final QuaxEngine engine;

    private final BooleanSupplier isGameOver;
    private final Supplier<int[]> getScheduledMove;
    private final BooleanSupplier isSecondPlayerHuman;
    private final BooleanSupplier isPieRuleSwapped;

    private int codeIndex = 0;
    private boolean strategyVisible = false;
    private final List<Button> winPathHighlights = new ArrayList<>();
    private final List<String> savedWinPathStyles = new ArrayList<>();

    DevModeHandler(Label devModeLabel, Button showStratDM,
                   Map<String, Button> buttonMap, QuaxEngine engine,
                   BooleanSupplier isGameOver, Supplier<int[]> getScheduledMove,
                   BooleanSupplier isSecondPlayerHuman, BooleanSupplier isPieRuleSwapped) {
        this.devModeLabel       = devModeLabel;
        this.showStratDM        = showStratDM;
        this.buttonMap          = buttonMap;
        this.engine             = engine;
        this.isGameOver         = isGameOver;
        this.getScheduledMove   = getScheduledMove;
        this.isSecondPlayerHuman = isSecondPlayerHuman;
        this.isPieRuleSwapped   = isPieRuleSwapped;
    }

    void setupStratButton() {
        if (showStratDM == null) return;
        showStratDM.setOnAction(e -> {
            if (strategyVisible) {
                clearHighlights();
                strategyVisible = false;
                showStratDM.setText("Show strategy");
            } else {
                strategyVisible = true;
                showBotWinPath();
                showStratDM.setText("Hide strategy");
            }
        });
    }

    void handleKeyEvent(KeyEvent event) {
        if (event.getCode() == KeyCode.LEFT) {
            setVisible(false);
            codeIndex = 0;
            return;
        }

        if (event.getCode() == SECRET_CODE.get(codeIndex)) {
            codeIndex++;
            if (codeIndex == SECRET_CODE.size()) {
                setVisible(!isActive());
                codeIndex = 0;
            }
        } else {
            codeIndex = event.getCode() == SECRET_CODE.get(0) ? 1 : 0;
        }
    }

    void showBotWinPath() {
        clearHighlights();
        if (isGameOver.getAsBoolean() || GameController.gameMode != 1) {
            return;
        }

        boolean secondHuman = isSecondPlayerHuman.getAsBoolean();
        boolean swapped     = isPieRuleSwapped.getAsBoolean();
        String botPiece = (secondHuman ^ swapped) ? "X" : "O";

        String[][] board = engine.getBoardCopy();
        int[] scheduled = getScheduledMove.get();
        if (scheduled != null) {
            board = QuaxPathFinder.withPiece(board, scheduled[0], scheduled[1], botPiece);
        }

        for (int[] tile : QuaxPathFinder.getShortestPathTiles(board, botPiece)) {
            Button btn = buttonMap.get(tile[0] + "," + tile[1]);
            if (btn != null) {
                savedWinPathStyles.add(btn.getStyle());
                btn.setStyle("-fx-background-color: #00cc55; -fx-background-insets: 0;");
                winPathHighlights.add(btn);
            }
        }
    }

    void clearHighlights() {
        for (int i = 0; i < winPathHighlights.size(); i++) {
            winPathHighlights.get(i).setStyle(savedWinPathStyles.get(i));
        }
        winPathHighlights.clear();
        savedWinPathStyles.clear();
    }

    boolean isActive() {
        return devModeLabel != null && devModeLabel.isVisible();
    }

    boolean isStrategyVisible() {
        return strategyVisible;
    }

    void reset() {
        codeIndex = 0;
        strategyVisible = false;
        clearHighlights();
        setVisible(false);
    }

    private void setVisible(boolean visible) {
        if (devModeLabel == null || showStratDM == null) return;
        devModeLabel.setVisible(visible);
        devModeLabel.setManaged(visible);
        showStratDM.setVisible(visible);
        showStratDM.setManaged(visible);
        showStratDM.setText("Show strategy");
        if (visible){
            devModeLabel.setText("Development Mode");
        }
        else{
            strategyVisible = false; clearHighlights();
        }
    }
}
