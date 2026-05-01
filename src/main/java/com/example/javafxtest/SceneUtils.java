package com.example.javafxtest;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.Objects;

// for scene setup: escape-hold handling and FXML scene loading.
class SceneUtils {

    // Sets up a 3-second ESC hold to trigger onExit — prevents accidental exits.
    // Returns the Timeline so the caller can stop it before the next setup call.
    static Timeline setupEscapeHold(Scene scene, Runnable onExit) {
        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(3), e -> onExit.run()));
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE && timer.getStatus() != Animation.Status.RUNNING)
                timer.playFromStart();
        });
        scene.setOnKeyReleased(e -> { if (e.getCode() == KeyCode.ESCAPE) timer.stop(); });
        return timer;
    }

    // Loads an FXML scene into the stage, preserving its size and maximised state.
    static void loadFxml(Stage stage, Class<?> resourceBase, String fxml, String css) throws IOException {
        double w = stage.getWidth(), h = stage.getHeight();
        boolean max = stage.isMaximized();
        FXMLLoader loader = new FXMLLoader(resourceBase.getResource(fxml));
        Scene scene = new Scene(loader.load(), w, h);
        scene.getStylesheets().add(Objects.requireNonNull(resourceBase.getResource(css)).toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(max);
        stage.show();
    }
}
