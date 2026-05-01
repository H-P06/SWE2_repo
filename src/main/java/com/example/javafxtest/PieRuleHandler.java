package com.example.javafxtest;

import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

class PieRuleHandler {

    private final Button pieRuleButton;
    private final Button pieButtonHelp;
    private final Label pieRuleActivated;
    private final IntSupplier getNumberMove;
    private final BooleanSupplier isSecondPlayerHuman;

    PieRuleHandler(Button pieRuleButton, Button pieButtonHelp, Label pieRuleActivated,
                   IntSupplier getNumberMove, BooleanSupplier isSecondPlayerHuman) {
        this.pieRuleButton       = pieRuleButton;
        this.pieButtonHelp       = pieButtonHelp;
        this.pieRuleActivated    = pieRuleActivated;
        this.getNumberMove       = getNumberMove;
        this.isSecondPlayerHuman = isSecondPlayerHuman;
    }

    void setup(Runnable onActivate) {
        pieRuleButton.setVisible(false);
        pieRuleButton.setManaged(false);
        pieRuleButton.setOnAction(event -> {
            pieRuleActivated.setText("Pie Rule Activated!");
            pieRuleActivated.setVisible(true);
            pieRuleActivated.setManaged(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> { pieRuleActivated.setVisible(false); pieRuleActivated.setManaged(false); });
            delay.play();
            onActivate.run();
            updateVisibility();
        });

        pieButtonHelp.setVisible(false);
        pieButtonHelp.setManaged(false);
        pieButtonHelp.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Rules");
            alert.setHeaderText("The Pie Rule");
            alert.setContentText("The second player can choose to swap with the first player.\nThis balances the advantage the first move holds.");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.getDialogPane().setPrefWidth(400);
            alert.showAndWait();
            updateHelpVisibility();
        });
    }

    // Show pie rule button only on move 1 when there is a human second player.
    void updateVisibility() {
        boolean show = getNumberMove.getAsInt() == 1 && isSecondPlayerHuman.getAsBoolean();
        pieRuleButton.setVisible(show);
        pieRuleButton.setManaged(show);
        if (show) pieRuleButton.setText("Activate Pie Rule");
    }

    void updateHelpVisibility() {
        boolean show = getNumberMove.getAsInt() == 1 && isSecondPlayerHuman.getAsBoolean();
        pieButtonHelp.setVisible(show);
        pieButtonHelp.setManaged(show);
        if (show) pieButtonHelp.setText("?");
    }
}
