package com.example.javafxtest;

import java.util.List;

/*
 * Bot for Quax using a Dijkstra shortest-path heuristic.
 * Strategy (in order of priority):
 *  1. Play winning move immediately if one exists.
 *  2. Block opponent's winning move.
 *  3. Play the empty cell that minimises the bot's own shortest path to
 *     victory
 */
public class QuaxBot {

    public static int[] chooseBestMove(String[][] board, String botPlayer) {
        String opponent = botPlayer.equals("X") ? "O" : "X";
        List<int[]> empty = QuaxPathFinder.getEmptyCells(board);
        if (empty.isEmpty()) return null;

        //Win immediately
        for (int[] cell : empty)
            if (QuaxPathFinder.shortestPath(QuaxPathFinder.withPiece(board, cell[0], cell[1], botPlayer), botPlayer) == 0)
                return cell;

        //Block opponent's immediate win
        for (int[] cell : empty)
            if (QuaxPathFinder.shortestPath(QuaxPathFinder.withPiece(board, cell[0], cell[1], opponent), opponent) == 0)
                return cell;

        //Minimise bot path
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
