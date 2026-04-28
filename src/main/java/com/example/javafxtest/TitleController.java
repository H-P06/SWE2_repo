package com.example.javafxtest;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

// Controller for the title screen — handles navigation to game modes and ESC to exit.
public class TitleController {

    @FXML private VBox titleRoot;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (titleRoot.getScene() != null)
                SceneUtils.setupEscapeHold(titleRoot.getScene(), Platform::exit);
        });
    }

    @FXML
    protected void onButtonClickPVB(ActionEvent event) {
        try {
            GameController.gameMode = 1;
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Quax - Player vs Bot");
            SceneUtils.loadFxml(stage, getClass(), "player_vs_bot_start.fxml", "style.css");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
