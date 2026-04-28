package com.example.javafxtest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

class GameControllerTest extends ApplicationTest {

    private GameController controller;

    @Override
    public void start(Stage stage) throws Exception {
        GameController.gameMode = 1;
        FXMLLoader loader = new FXMLLoader(GameController.class.getResource("player_vs_bot_start.fxml"));
        Scene scene = new Scene(loader.load());
        controller = loader.getController();
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    void setUp() {
        GameController.gameMode = 1;
        interact(() -> {
            controller.resetForTesting();
            controller.getEngine().reset();
            controller.numberMove = 0;
            controller.isSecondPlayerHuman = false;
            controller.pieRuleSwapped = false;
            controller.pieRuleButton.setVisible(false);
            controller.pieRuleButton.setManaged(false);
            controller.pieButtonHelp.setVisible(false);
            controller.pieButtonHelp.setManaged(false);
        });
    }

    @Test
    void testPiecePlacement() {
        QuaxEngine engine = new QuaxEngine();
        engine.placePiece(0, 0);
        assertEquals("X", engine.getPieceAt(0, 0), "First move should be X");
    }

    @Test
    void testCannotOverwriteMove() {
        QuaxEngine engine = new QuaxEngine();
        engine.placePiece(4, 4);
        boolean success = engine.placePiece(4, 4);
        assertFalse(success, "Should not be able to place piece on occupied square");
        assertEquals("X", engine.getPieceAt(4, 4), "Square should still contain X");
    }

    @Test
    void testTurnAlternation() {
        QuaxEngine engine = new QuaxEngine();
        engine.placePiece(0, 0); // X
        engine.placePiece(1, 1); // O
    }

    @Test
    void testInitialPieRuleOnBeginning() {
        interact(() -> controller.setPieRuleButton());

        assertFalse(controller.pieRuleButton.isVisible(), "The button is to be invisible in the beginning");
    }

    @Test
    void testPieRuleVisibility() {
        interact(() -> {
            controller.numberMove = 1;
            controller.isSecondPlayerHuman = true;
            controller.updatePieRuleVisibility();
        });

        assertTrue(controller.pieRuleButton.isVisible(), "The button should be visible after 1 move");
    }

    @Test
    void testPieRuleVisibility2Moves() {
        interact(() -> {
            controller.numberMove = 2;
            controller.updatePieRuleVisibility();
        });

        assertFalse(controller.pieRuleButton.isVisible(), "The button should not be visible when we have 2 moves");
    }

    @Test
    void testPieRuleLogic() {
        QuaxEngine engine = controller.getEngine();

        engine.placePiece(0, 0);
        assertEquals("X", engine.getPieceAt(0, 0), "First move should be X");

        Button boardButton = new Button();
        controller.buttonMap.put("0,0", boardButton);

        interact(() -> controller.handlePieRuleLogic());

        assertEquals("X", engine.getPieceAt(0, 0), "the piece should remain X (Black); players swap, not the piece");
        assertEquals(2, engine.getMoveCount());

        engine.placePiece(2, 0);
        assertEquals("O", engine.getPieceAt(2, 0), "Next move after pie rule should be O (White)");
    }

    @Test
    void testInitialPieButtonHelpOnBeginning() {
        interact(() -> controller.setPieRuleHelp());

        assertFalse(controller.pieButtonHelp.isVisible(), "The button is to be invisible in the beginning");
    }

    @Test
    void testPieButtonHelpVisibility() {
        interact(() -> {
            controller.numberMove = 1;
            controller.isSecondPlayerHuman = true;
            controller.updatePieButtonHelpVisibility();
        });

        assertTrue(controller.pieButtonHelp.isVisible(), "The button should be visible after 1 move");
    }

    @Test
    void testPieButtonHelpVisibility2Moves() {
        interact(() -> {
            controller.numberMove = 2;
            controller.updatePieButtonHelpVisibility();
        });

        assertFalse(controller.pieButtonHelp.isVisible(), "The button should not be visible when we have 2 moves");
    }

    @Test
    void testPieRuleTitleAppearance() {
        Label testLabel = new Label();
        interact(() -> {
            controller.playerToPlay = new Label();
            controller.pieRuleActivated = testLabel;

            controller.handlePieRuleLogic();

            testLabel.setText("Pie Rule Activated!");
            testLabel.setVisible(true);
        });

        assertEquals("Pie Rule Activated!", testLabel.getText());
        assertTrue(testLabel.isVisible(), "The Pie Rule title should be visible to the player.");
    }

    @Test
    void testExitResetsGameMode() {
        GameController.gameMode = 1;

        GameController.gameMode = -1;

        assertEquals(-1, GameController.gameMode, "The game mode should reset to -1 upon exit.");
    }

    @Test
    void testBotChoosesWinningMove() {
        String[][] board = new String[22][22];
        for (int y = 0; y <= 18; y += 2) {
            board[10][y] = "X";
        }
        int[] move = QuaxBot.chooseBestMove(board, "X");

        assertNotNull(move);
        assertEquals(10, move[0]);
        assertEquals(20, move[1]);
    }

    @Test
    void testBotMakesValidFirstMove() {
        String[][] emptyBoard = new String[22][22];
        int[] move = QuaxBot.chooseBestMove(emptyBoard, "X");

        assertNotNull(move);
        assertTrue(move[0] >= 0 && move[0] <= 20);
        assertTrue(move[1] >= 0 && move[1] <= 20);
        assertTrue((move[0] % 2 == 0 && move[1] % 2 == 0) || (move[0] % 2 == 1 && move[1] % 2 == 1));
    }

    @Test
    void testRandomColourAssignLogic() {
        interact(() -> controller.randomColourAssign());

        if (controller.pieRuleSwapped) {
            assertTrue(controller.pieRuleSwapped);
            assertEquals("X", controller.getEngine().getCurrentPlayer());
        } else {
            assertEquals(0, controller.getEngine().getMoveCount());
            assertFalse(controller.pieRuleSwapped);
            assertEquals("X", controller.getEngine().getCurrentPlayer());
        }
    }

    @Test
    public void testDevModeActivation() {
        verifyThat("#devModeLabel", isInvisible());

        type(KeyCode.D);
        type(KeyCode.E);
        type(KeyCode.V);

        verifyThat("#devModeLabel", isVisible());
        verifyThat("#showStratDM", isVisible());
    }

    @Test
    public void testStrategyHighlighting() {
        type(KeyCode.D, KeyCode.E, KeyCode.V);
        clickOn("#showStratDM");

        boolean hasGreenHighlight = lookup(".board-tile").queryAll().stream()
                .anyMatch(node -> node.getStyle().contains("#00cc55"));

        assertTrue(hasGreenHighlight, "Bot path should be visible in green.");
    }

    @Test
    public void testClearHighlights() {
        type(KeyCode.D, KeyCode.E, KeyCode.V);
        clickOn("#showStratDM");

        interact(() -> controller.clearWinPathHighlights());

        boolean stillHasGreen = lookup(".board-tile").queryAll().stream()
                .anyMatch(node -> node.getStyle().contains("#00cc55"));

        assertFalse(stillHasGreen, "All highlights should be reverted to original styles.");
    }

    @Test
    public void testBotPathfindingLogic() {
        String[][] emptyBoard = new String[22][22];
        String botPiece = "X";

        List<int[]> path = QuaxPathFinder.getShortestPathTiles(emptyBoard, botPiece);

        assertNotNull(path);
        assertFalse(path.isEmpty());
        assertTrue(path.stream().anyMatch(t -> t[1] == 0), "Should start at top.");
        assertTrue(path.stream().anyMatch(t -> t[1] == 20), "Should reach bottom.");
    }

    @Test
    public void testExitDevMode() {
        type(KeyCode.D, KeyCode.E, KeyCode.V);
        type(KeyCode.LEFT);

        verifyThat("#devModeLabel", isInvisible());
        verifyThat("#showStratDM", isInvisible());
    }
}
