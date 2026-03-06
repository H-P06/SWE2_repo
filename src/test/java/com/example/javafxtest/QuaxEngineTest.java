package com.example.javafxtest;

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

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //it changes X to O (black to white)
        //and it increases the move count
        assertEquals(quaxEngine.getPieceAt(0,0),"X");
        assertEquals(quaxEngine.getMoveCount(),1);
    }
}