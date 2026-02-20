package com.example.javafxtest;




public class QuaxEngine {
    private String[][] boardLogic = new String[22][22];
    private int totalNumberMoves = 0;

    public boolean placePiece(int x, int y) {
        if (x < 0 || x >= 22 || y < 0 || y >= 22 || boardLogic[x][y] != null) {
            return false;
        }
        totalNumberMoves++;
        boardLogic[x][y] = (totalNumberMoves % 2 == 1) ? "X" : "O";
        return true;
    }

    public String getPieceAt(int x, int y) {
        System.out.println(x + "," + y);
        return boardLogic[x][y];
    }

    public int getMoveCount() {
        return totalNumberMoves;
    }

    public void reset() {
        boardLogic = new String[22][22];
        totalNumberMoves = 0;
    }

}
