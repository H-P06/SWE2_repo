package com.example.javafxtest;

import java.util.*;

// Dijkstra-based pathfinding on the octagon-diamond Quax board.
public class QuaxPathFinder {

    static final int[][] OCT_NEIGHBORS     = {{2,0},{-2,0},{0,2},{0,-2},{1,1},{1,-1},{-1,1},{-1,-1}};
    static final int[][] DIAMOND_NEIGHBORS = {{1,1},{1,-1},{-1,1},{-1,-1}};
    static final int INF = Integer.MAX_VALUE / 2;

    static boolean isValid(int x, int y) {
        return x >= 0 && x <= 20 && y >= 0 && y <= 20
                && ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1));
    }

    static List<int[]> getEmptyCells(String[][] board) {
        List<int[]> cells = new ArrayList<>();
        for (int x = 0; x <= 20; x++)
            for (int y = 0; y <= 20; y++)
                if (isValid(x, y) && board[x][y] == null)
                    cells.add(new int[]{x, y});
        return cells;
    }

    public static String[][] withPiece(String[][] board, int px, int py, String player) {
        String[][] copy = new String[22][22];
        for (int i = 0; i < 22; i++) copy[i] = board[i].clone();
        copy[px][py] = player;
        return copy;
    }

    static int shortestPath(String[][] board, String player) {
        boolean topToBottom = player.equals("X");
        String  opponent    = player.equals("X") ? "O" : "X";

        int[][] dist = new int[22][22];
        for (int[] row : dist) Arrays.fill(row, INF);

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));

        for (int i = 0; i <= 20; i += 2) {
            int sx = topToBottom ? i : 0;
            int sy = topToBottom ? 0 : i;
            if (opponent.equals(board[sx][sy])) continue;
            int cost = (board[sx][sy] == null) ? 1 : 0;
            if (cost < dist[sx][sy]) { dist[sx][sy] = cost; pq.offer(new int[]{cost, sx, sy}); }
        }

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int d = cur[0], cx = cur[1], cy = cur[2];
            if (d > dist[cx][cy]) continue;
            if ((topToBottom && cy == 20) || (!topToBottom && cx == 20)) return d;

            int[][] nbrs = (cx % 2 == 0 && cy % 2 == 0) ? OCT_NEIGHBORS : DIAMOND_NEIGHBORS;
            for (int[] delta : nbrs) {
                int nx = cx + delta[0], ny = cy + delta[1];
                if (!isValid(nx, ny) || opponent.equals(board[nx][ny])) continue;
                int nd = d + (board[nx][ny] == null ? 1 : 0);
                if (nd < dist[nx][ny]) { dist[nx][ny] = nd; pq.offer(new int[]{nd, nx, ny}); }
            }
        }
        return INF;
    }

    /*
     * Returns the tiles on the shortest winning path
     * Returns an empty list if no path exists.
     */
    public static List<int[]> getShortestPathTiles(String[][] board, String player) {
        boolean topToBottom = player.equals("X");
        String  opponent    = player.equals("X") ? "O" : "X";

        int[][] dist   = new int[22][22];
        int[][][] prev = new int[22][22][];
        for (int[] row : dist) Arrays.fill(row, INF);

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
            if ((topToBottom && cy == 20) || (!topToBottom && cx == 20)) { goalTile = new int[]{cx, cy}; break; }

            int[][] nbrs = (cx % 2 == 0 && cy % 2 == 0) ? OCT_NEIGHBORS : DIAMOND_NEIGHBORS;
            for (int[] delta : nbrs) {
                int nx = cx + delta[0], ny = cy + delta[1];
                if (!isValid(nx, ny) || opponent.equals(board[nx][ny])) continue;
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
}
