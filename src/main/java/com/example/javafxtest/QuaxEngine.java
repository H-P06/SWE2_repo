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

    //to handle pie rule
    public void applyPieRule(){
        //find the one piece on the board
        for(int i = 0; i < 22; i++){
            for(int j = 0; j < 22; j++){
                //if X is in that spot
                if("X".equals(boardLogic[i][j])){
                    boardLogic[i][j] = "O";
                    totalNumberMoves = 2;
                    return;
                }
            }
        }
    }

}
