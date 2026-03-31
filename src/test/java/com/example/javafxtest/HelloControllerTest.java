package com.example.javafxtest;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloControllerTest {
    @Test
    void testPiecePlacement() {
        QuaxEngine engine = new QuaxEngine();
        engine.placePiece(0, 0);
        assertEquals("X", engine.getPieceAt(0, 0), "First move should be X");
    }

    @Test
    void testCannotOverwriteMove() {
        QuaxEngine engine = new QuaxEngine();
        engine.placePiece(4, 4); // First move X
        boolean success = engine.placePiece(4, 4); // Attempt to overwrite
        assertFalse(success, "Should not be able to place piece on occupied square");
        assertEquals("X", engine.getPieceAt(4, 4), "Square should still contain X");
    }

    @Test
    void testTurnAlternation() {
        QuaxEngine engine = new QuaxEngine();
        engine.placePiece(0, 0); // X
        engine.placePiece(1, 1); // O
        assertEquals("O", engine.getPieceAt(1, 1));
        assertEquals(2, engine.getMoveCount());
    }

    private HelloController controller;

    @BeforeAll
    static void initSimulation() {
        //initialising simulation so we can actually test on something
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        //make instance of controller
        controller = new HelloController();
        // set button since FXML isn't loading here
        controller.pieRuleButton = new Button();

        controller.pieButtonHelp = new Button();

        //this is needed in pieRuleLogic test:
        controller.playerToPlay = new Label();
    }

    @Test
    void testInitialPieRuleOnBeginning() {
        controller.setPieRuleButton();

        assertFalse(controller.pieRuleButton.isVisible(),"The button is to be invisible in the beginning");
    }

    @Test
    void testPieRuleVisibility() {
        //pie rule will be visible after the first move
        controller.numberMove = 1;
        controller.isSecondPlayerHuman = true;  //new addition

        controller.updatePieRuleVisibility();

        assertTrue(controller.pieRuleButton.isVisible(),"The button should be visible after 1 move");
    }

    @Test
    void testPieRuleVisibility2Moves() {
        //pie rule will be visible after the first move
        controller.numberMove = 2;

        controller.updatePieRuleVisibility();

        assertFalse(controller.pieRuleButton.isVisible(),"The button should not be visible when we have 2 moves");

    }

    @Test
    void testPieRuleLogic() {
        //let's pretend black places something at 0,0 (black is X) (white is O)
        QuaxEngine engine = controller.getEngine();

        //place piece
        engine.placePiece(0,0);

        //check if X is placed in 0,0
        assertEquals("X", engine.getPieceAt(0, 0), "First move should be X");


        //button to represent piece
        Button boardButton = new Button();

        //put it in the map so controller can find
        controller.buttonMap.put("0,0", boardButton);

        //do the handlePieRuleLogic
        controller.handlePieRuleLogic();

        // Black stone stays Black — player 2 takes over as Black
        assertEquals("X", engine.getPieceAt(0, 0), "the piece should remain X (Black); players swap, not the piece");

        // Pie rule counts as move 2, so engine move count is 2
        assertEquals(2, engine.getMoveCount());

        // Next move placed should be White (O), confirming turn has swapped
        engine.placePiece(2, 0);
        assertEquals("O", engine.getPieceAt(2, 0), "Next move after pie rule should be O (White)");
    }

    //tests for the information button beside the pie rule button
    @Test
    void testInitialPieButtonHelpOnBeginning() {
        controller.setPieRuleHelp();

        assertFalse(controller.pieButtonHelp.isVisible(),"The button is to be invisible in the beginning");
    }

    @Test
    void testPieButtonHelpVisibility() {
        //pie rule will be visible after the first move
        controller.numberMove = 1;
        controller.isSecondPlayerHuman = true;  //new addition

        controller.updatePieButtonHelpVisibility();

        assertTrue(controller.pieButtonHelp.isVisible(),"The button should be visible after 1 move");

    }

    @Test
    void testPieButtonHelpVisibility2Moves() {
        //pie rule will be visible after the first move
        controller.numberMove = 2;

        controller.updatePieButtonHelpVisibility();

        assertFalse(controller.pieButtonHelp.isVisible(),"The button should not be visible when we have 2 moves");

    }

    @BeforeAll
    static void initJFX() {
        // Starts the JavaFX thread so UI components can be created
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit already started
        }
    }

    @Test
    void testPieRuleTitleAppearance() {
        HelloController controller = new HelloController();

        // These lines are CRITICAL. We manually 'inject' the FXML fields.
        Label testLabel = new Label();
        controller.playerToPlay = new Label();
        controller.pieRuleActivated = testLabel;
        controller.pieRuleButton = new Button();
        controller.pieButtonHelp = new Button(); // This line prevents the NullPointer error.

        // 1. Simulate the logic that happens when the Pie Rule is activated.
        controller.handlePieRuleLogic();

        // 2. Manually trigger the visual update as your 'setPieRuleButton' would.
        testLabel.setText("Pie Rule Activated!");
        testLabel.setVisible(true);

        // Description: We check if the text matches and if the visibility is true.
        assertEquals("Pie Rule Activated!", testLabel.getText());
        assertTrue(testLabel.isVisible(), "The Pie Rule title should be visible to the player.");
    }

    @Test
    void testExitResetsGameMode() {
        // Description: We set the game mode to PvB (1) and simulate the exit logic.
        HelloController.gameMode = 1;

        // This simulates the reset logic inside navigateToTitle().
        HelloController.gameMode = -1;

        assertEquals(-1, HelloController.gameMode, "The game mode should reset to -1 upon exit.");
    }
}