package com.example.javafxtest;




public class QuaxEngine {
    private String[][] boardLogic = new String[22][22];
    private int totalNumberMoves = 0;
    private String currentPlayer = "X";

    // Adjacency deltas for octagons (even,even): 4 orthogonal octagons + 4 diagonal diamonds
    private static final int[][] OCT_NEIGHBORS = {{2,0},{-2,0},{0,2},{0,-2},{1,1},{1,-1},{-1,1},{-1,-1}};
    // Adjacency deltas for diamonds (odd,odd): 4 corner octagons
    private static final int[][] DIAMOND_NEIGHBORS = {{1,1},{1,-1},{-1,1},{-1,-1}};

    public boolean placePiece(int x, int y) {
        if (x < 0 || x >= 22 || y < 0 || y >= 22 || boardLogic[x][y] != null) {
            return false;
        }
        totalNumberMoves++;
        boardLogic[x][y] = currentPlayer;
        currentPlayer = currentPlayer.equals("X") ? "O" : "X";
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
        currentPlayer = "X";
    }

    // Returns "X" if Black wins (top-to-bottom), "O" if White wins (left-to-right), null if no winner yet.
    public String checkWinner() {
        if (isConnected("X", true))  return "X";
        if (isConnected("O", false)) return "O";
        return null;
    }

    private boolean isConnected(String player, boolean topToBottom) {
        boolean[][] visited = new boolean[22][22];
        java.util.Queue<int[]> queue = new java.util.LinkedList<>();

        // Seed BFS from the starting edge (only octagon positions, step=2)
        for (int i = 0; i <= 20; i += 2) {
            int sx = topToBottom ? i : 0;
            int sy = topToBottom ? 0 : i;
            if (player.equals(boardLogic[sx][sy]) && !visited[sx][sy]) {
                visited[sx][sy] = true;
                queue.add(new int[]{sx, sy});
            }
        }

        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int cx = cell[0], cy = cell[1];

            // Reached the goal edge?
            if ((topToBottom && cy == 20) || (!topToBottom && cx == 20)) return true;

            int[][] neighbors = (cx % 2 == 0 && cy % 2 == 0) ? OCT_NEIGHBORS : DIAMOND_NEIGHBORS;
            for (int[] d : neighbors) {
                int nx = cx + d[0], ny = cy + d[1];
                if (nx >= 0 && nx <= 20 && ny >= 0 && ny <= 20
                        && !visited[nx][ny] && player.equals(boardLogic[nx][ny])) {
                    visited[nx][ny] = true;
                    queue.add(new int[]{nx, ny});
                }
            }
        }
        return false;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public String[][] getBoardCopy() {
        String[][] copy = new String[22][22];
        for (int i = 0; i < 22; i++) {
            copy[i] = boardLogic[i].clone();
        }
        return copy;
    }

    //to handle pie rule
    // Player 2 swaps sides: they take over as Black (X), player 1 becomes White (O).
    // The existing Black stone stays Black. Next move will be White (O).
    // Pie rule counts as player 2's move (move 2), so increment the count.
    public void applyPieRule(){
        totalNumberMoves++;
        // currentPlayer was "O" (White's turn) — keep it as "O" so next piece is White
    }

}
