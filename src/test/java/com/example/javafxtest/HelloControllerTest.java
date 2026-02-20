package com.example.javafxtest;

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
}