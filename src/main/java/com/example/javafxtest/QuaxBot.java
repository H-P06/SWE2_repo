package com.example.javafxtest;

import java.util.List;

/*
 * AI bot for Quax using a Dijkstra shortest-path heuristic.
 * Strategy (in order of priority):
 *  1. Play winning move immediately if one exists.
 *  2. Block opponent's winning move.
 *  3. Play the empty cell that minimises the bot's own shortest path to
 *     victory, breaking ties by maximising the disruption to the opponent.
 */
public class QuaxBot {

    /*
     * Choose best move for the bot.
     * @param board     current board state — board[x][y], null = empty
     * @param botPlayer "X" (Black) or "O" (White)
     * @return {x, y} of the chosen cell, or null if no moves exist
     */
    public static int[] chooseBestMove(String[][] board, String botPlayer) {
        String opponent = botPlayer.equals("X") ? "O" : "X";
        List<int[]> empty = QuaxPathFinder.getEmptyCells(board);
        if (empty.isEmpty()) return null;

        // 1. Win immediately
        for (int[] cell : empty)
            if (QuaxPathFinder.shortestPath(QuaxPathFinder.withPiece(board, cell[0], cell[1], botPlayer), botPlayer) == 0)
                return cell;

        // 2. Block opponent's immediate win
        for (int[] cell : empty)
            if (QuaxPathFinder.shortestPath(QuaxPathFinder.withPiece(board, cell[0], cell[1], opponent), opponent) == 0)
                return cell;

        // 3. Minimise bot path; break ties by maximising opponent path
        int[] best = null;
        int bestBot = QuaxPathFinder.INF, bestOpp = -1;
        for (int[] cell : empty) {
            String[][] sim = QuaxPathFinder.withPiece(board, cell[0], cell[1], botPlayer);
            int botCost = QuaxPathFinder.shortestPath(sim, botPlayer);
            int oppCost = QuaxPathFinder.shortestPath(sim, opponent);
            if (botCost < bestBot || (botCost == bestBot && oppCost > bestOpp)) {
                bestBot = botCost; bestOpp = oppCost; best = cell;
            }
        }
        return best != null ? best : empty.get(0);
    }
}
