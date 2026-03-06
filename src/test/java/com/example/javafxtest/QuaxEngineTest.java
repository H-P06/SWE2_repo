package com.example.javafxtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuaxEngineTest {

    @Test
    void placePieceTestInvalid() {
        QuaxEngine quaxEngine = new QuaxEngine();
        assertFalse(quaxEngine.placePiece(22,23));
    }

    @Test
    void placePieceTestValid() {
        QuaxEngine quaxEngine = new QuaxEngine();
        assertTrue(quaxEngine.placePiece(0,0));

    }

    @Test
    //nothing should be there
    void getPieceAtNotThere() {
        QuaxEngine quaxEngine = new QuaxEngine();
        quaxEngine.placePiece(0,1);
        assertEquals(quaxEngine.getPieceAt(0,0),null);
    }

    @Test
    //the move is there and it should return X since the first move is X
    void getPieceAtThere() {
        QuaxEngine quaxEngine = new QuaxEngine();
        quaxEngine.placePiece(0,1);
        assertEquals(quaxEngine.getPieceAt(0,1), "X");
    }



    @Test
    void getMoveCountUpdates() {
        QuaxEngine quaxEngine = new QuaxEngine();
        quaxEngine.placePiece(0,1);
        quaxEngine.placePiece(0,2);
        quaxEngine.placePiece(0,3);
        assertEquals(quaxEngine.getMoveCount(),3);
    }

    @Test
    void resetTest() {
        QuaxEngine quaxEngine = new QuaxEngine();
        //lets make a move at 0,0
        quaxEngine.placePiece(0,0);
        quaxEngine.reset();
        assertEquals(quaxEngine.getPieceAt(0,0),null);
        assertEquals(quaxEngine.getMoveCount(), 0);
    }

    @Test
    void applyPieRuleSwitchesPlayers() {
        QuaxEngine quaxEngine = new QuaxEngine();
        quaxEngine.placePiece(0,0);

        //just to show it does indeed check and it is indeed X at first
        //and to see the move count
        assertEquals(quaxEngine.getPieceAt(0,0),"X");
        assertEquals(quaxEngine.getMoveCount(),1);

        quaxEngine.applyPieRule();

        //the move shouldn't change (just to the change to adapt to the actual pie rule is correct)
        //move count increased
        assertEquals(quaxEngine.getPieceAt(0,0),"X");
        assertEquals(quaxEngine.getMoveCount(),2);
    }

    private QuaxEngine engine;

    @BeforeEach
    void setUp() {
        engine = new QuaxEngine();
    }

    @Test
    void testBlackWinVertical() {
        // Place a vertical line of Black (X) pieces from top to bottom
        // Black moves on even turns: 0, 2, 4...
        for (int y = 0; y <= 22; y += 1) {
            engine.placePiece(0, y); // Black move
            engine.placePiece(2, y); // White dummy move (to keep turns alternating)
        }
        assertEquals("X", engine.checkWinner(), "Black should win with a vertical line");
    }

    @Test
    void testWhiteWinHorizontal() {
        // Place a horizontal line of White (O) pieces from left to right
        for (int x = 0; x <= 22; x += 2) {
            engine.placePiece(x, 5); // Black dummy move
            engine.placePiece(x, 0);    // White move
        }
        assertEquals("O", engine.checkWinner(), "White should win with a horizontal line");
    }

    @Test
    void testNoWinnerInitially() {
        assertNull(engine.checkWinner(), "New game should have no winner");
    }


}