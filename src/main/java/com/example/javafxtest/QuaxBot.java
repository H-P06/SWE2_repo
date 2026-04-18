package com.example.javafxtest;

import java.util.*;

/*
 * AI bot for Quax using a Dijkstra shortest-path heuristic.
 * Strategy (in order of priority):
 *  1. Play winning move immediately if one exists.
 *  2. Block opponent's winning move.
 *  3. Play the empty cell that minimises the bot's own shortest path to
 *     victory, breaking ties by maximising the disruption to the opponent.
 *
 * Black (X) wins top-to-bottom  (y = 0 → y = 20).
 * White (O) wins left-to-right  (x = 0 → x = 20).
 */
public class QuaxBot {

    private static final int[][] OCT_NEIGHBORS     = {{2,0},{-2,0},{0,2},{0,-2},{1,1},{1,-1},{-1,1},{-1,-1}};
    private static final int[][] DIAMOND_NEIGHBORS = {{1,1},{1,-1},{-1,1},{-1,-1}};
    private static final int INF = Integer.MAX_VALUE / 2;

    /*
     * Choose best move for the bot.
     * @param board     current board state – board[x][y], null = empty
     * @param botPlayer "X" (Black) or "O" (White)
     * @return {x, y} coordinates of the chosen cell, or null if no moves exist
     */
    public static int[] chooseBestMove(String[][] board, String botPlayer) {
        String opponent = botPlayer.equals("X") ? "O" : "X";
        List<int[]> empty = getEmptyCells(board);
        if (empty.isEmpty()) return null;

        // 1. Win immediately
        for (int[] cell : empty) {
            if (shortestPath(withPiece(board, cell[0], cell[1], botPlayer), botPlayer) == 0) {
                return cell;
            }
        }

        // 2. Block opponent's immediate win
        for (int[] cell : empty) {
            if (shortestPath(withPiece(board, cell[0], cell[1], opponent), opponent) == 0) {
                return cell;
            }
        }

        // 3. Best heuristic: minimise bot path, break ties by maximising opponent path
        int[] best = null;
        int bestBot  = INF;
        int bestOpp  = -1;

        for (int[] cell : empty) {
            String[][] sim = withPiece(board, cell[0], cell[1], botPlayer);
            int botCost = shortestPath(sim, botPlayer);
            int oppCost = shortestPath(sim, opponent);
            if (botCost < bestBot || (botCost == bestBot && oppCost > bestOpp)) {
                bestBot  = botCost;
                bestOpp  = oppCost;
                best     = cell;
            }
        }

        return best != null ? best : empty.get(0);
    }

    // helpers

    private static boolean isValid(int x, int y) {
        return x >= 0 && x <= 20 && y >= 0 && y <= 20
                && ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1));
    }

    private static List<int[]> getEmptyCells(String[][] board) {
        List<int[]> cells = new ArrayList<>();
        for (int x = 0; x <= 20; x++) {
            for (int y = 0; y <= 20; y++) {
                if (isValid(x, y) && board[x][y] == null) {
                    cells.add(new int[]{x, y});
                }
            }
        }
        return cells;
    }

    /* return copy of board with board[px][py] set to player. */
    public static String[][] withPiece(String[][] board, int px, int py, String player) {
        String[][] copy = new String[22][22];
        for (int i = 0; i < 22; i++) copy[i] = board[i].clone();
        copy[px][py] = player;
        return copy;
    }

    /*
     * Returns the tiles on the bot's shortest winning path for the given board.
     * Includes both already-owned tiles (cost 0) and empty tiles to fill (cost 1).
     * Returns an empty list if no path exists.
     */
    public static List<int[]> getShortestPathTiles(String[][] board, String player) {
        boolean topToBottom = player.equals("X");
        String  opponent    = player.equals("X") ? "O" : "X";

        int[][] dist = new int[22][22];
        for (int[] row : dist) Arrays.fill(row, INF);

        // prev[x][y]: null = unvisited, {-1,-1} = start node, {px,py} = predecessor
        int[][][] prev = new int[22][22][];

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));

        for (int i = 0; i <= 20; i += 2) {
            int sx = topToBottom ? i : 0;
            int sy = topToBottom ? 0 : i;
            if (opponent.equals(board[sx][sy])) continue;
            int cost = (board[sx][sy] == null) ? 1 : 0;
            if (cost < dist[sx][sy]) {
                dist[sx][sy] = cost;
                prev[sx][sy] = new int[]{-1, -1};
                pq.offer(new int[]{cost, sx, sy});
            }
        }

        int[] goalTile = null;
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int d = cur[0], cx = cur[1], cy = cur[2];
            if (d > dist[cx][cy]) continue;

            if ((topToBottom && cy == 20) || (!topToBottom && cx == 20)) {
                goalTile = new int[]{cx, cy};
                break;
            }

            int[][] nbrs = (cx % 2 == 0 && cy % 2 == 0) ? OCT_NEIGHBORS : DIAMOND_NEIGHBORS;
            for (int[] delta : nbrs) {
                int nx = cx + delta[0], ny = cy + delta[1];
                if (!isValid(nx, ny)) continue;
                if (opponent.equals(board[nx][ny])) continue;
                int nd = d + (board[nx][ny] == null ? 1 : 0);
                if (nd < dist[nx][ny]) {
                    dist[nx][ny] = nd;
                    prev[nx][ny] = new int[]{cx, cy};
                    pq.offer(new int[]{nd, nx, ny});
                }
            }
        }

        if (goalTile == null) return Collections.emptyList();

        List<int[]> path = new ArrayList<>();
        int[] cur = goalTile;
        while (cur != null) {
            path.add(cur);
            int[] p = prev[cur[0]][cur[1]];
            if (p == null || p[0] == -1) break;
            cur = p;
        }
        return path;
    }

    /*
     * Dijkstra shortest-path cost for player on the given board.
     * Cost model: own piece = 0, empty cell = 1, opponent cell = blocked.
     * Returns minimum number of empty cells that must be filled to form
     * winning path.  Returns 0 if already connected, INF if impossible.
     */
    private static int shortestPath(String[][] board, String player) {
        boolean topToBottom = player.equals("X");
        String  opponent    = player.equals("X") ? "O" : "X";

        int[][] dist = new int[22][22];
        for (int[] row : dist) Arrays.fill(row, INF);

        // {cost, x, y}
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));

        // Seed from starting edge (octagons only, step 2)
        for (int i = 0; i <= 20; i += 2) {
            int sx = topToBottom ? i : 0;
            int sy = topToBottom ? 0 : i;
            if (opponent.equals(board[sx][sy])) continue;
            int cost = (board[sx][sy] == null) ? 1 : 0;
            if (cost < dist[sx][sy]) {
                dist[sx][sy] = cost;
                pq.offer(new int[]{cost, sx, sy});
            }
        }

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int d = cur[0], cx = cur[1], cy = cur[2];
            if (d > dist[cx][cy]) continue;

            // Reached goal edge?
            if ((topToBottom && cy == 20) || (!topToBottom && cx == 20)) return d;

            int[][] nbrs = (cx % 2 == 0 && cy % 2 == 0) ? OCT_NEIGHBORS : DIAMOND_NEIGHBORS;
            for (int[] delta : nbrs) {
                int nx = cx + delta[0], ny = cy + delta[1];
                if (!isValid(nx, ny)) continue;
                if (opponent.equals(board[nx][ny])) continue;
                int nd = d + (board[nx][ny] == null ? 1 : 0);
                if (nd < dist[nx][ny]) {
                    dist[nx][ny] = nd;
                    pq.offer(new int[]{nd, nx, ny});
                }
            }
        }
        return INF;
    }
}
