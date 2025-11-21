package stud.g01.solver.algorithm.heuristic;

import core.problem.State;
import core.solver.algorithm.heuristic.Predictor;
import stud.g01.problem.npuzzle.PuzzleBoard;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 ? <h2>Disjoint Pattern Database Predictor for N-Puzzle</h2>

 ? 本实现默认使用 BiBFS 建库，仍支持落盘与读盘。

 ? 性能对比请在另一个文件删除 `.db` 后切换策略即可。

 *
 ? @author WattAi

 ? @since 2025-11

 */
public class DisjointPatternDatabasePredictor implements Predictor {

    /* ===================== 静态工厂缓存 ===================== */
    private static final Map<Integer, PDB> STORE = new HashMap<>();

    /* ===================== 对外接口 ======================== */
    @Override
    public int heuristics(State state, State goal) {
        PuzzleBoard curr = (PuzzleBoard) state;
        PuzzleBoard g = (PuzzleBoard) goal;
        int size = curr.getSize();
        if (size != 3 && size != 4)
            throw new IllegalArgumentException("Only 8-puzzle/15-puzzle supported");
        return STORE.computeIfAbsent(size, PDB::new).lookup(curr, g);
    }

    /* =================================================================== */
    /* ===================== 内部 PDB（BiBFS修复版） ====================== */
    private static class PDB {
        private static long totalBuildNs = 0;
        private final int size, len;
        private final int[][] groups;
        private final int[][] tables;

        PDB(int size) {
            this.size = size;
            this.len = size * size;
            if (size == 3) {                       // 8-puzzle：4-4
                groups = new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}};
            } else {                               // 15-puzzle：6-6-3
                groups = new int[][]{{1, 2, 3, 4, 5, 6}, {7, 8, 9, 10, 11, 12}, {13, 14, 15}};
            }
            tables = new int[groups.length][];
            for (int i = 0; i < groups.length; i++)
                tables[i] = loadOrBuild(i);
        }

        int lookup(PuzzleBoard curr, PuzzleBoard goal) {
            int h = 0;
            for (int i = 0; i < groups.length; i++) {
                int idx = comboIndex(mask(curr, groups[i]), groups[i].length, len);
                int gIdx = comboIndex(mask(goal, groups[i]), groups[i].length, len);
                h += Math.abs(tables[i][idx] - tables[i][gIdx]);
            }
            return h;
        }

        /* ---------------- 读盘 or 建库（BiBFS修复版） ---------------- */
        private int[] loadOrBuild(int gid) {
            String file = fileName(gid);
            try {
                return readDB(file).table;          // 优先读盘
            } catch (IOException e) {
                System.out.println("磁盘无库，开始 BiBFS 建库：" + file);
            }

            int[] group = groups[gid];
            int k = group.length;
            int[] table = new int[C(len, k)];
            Arrays.fill(table, -1);
            long nodes = 0;
            long start = System.nanoTime();

            int goalMask = maskGroup(group);
            int goalIdx = comboIndex(goalMask, k, len);
            table[goalIdx] = 0;

            /* 双向队列：只从 goal 出发，交替扩展 */
            IntQueue fq = new IntQueue(table.length);
            IntQueue bq = new IntQueue(table.length);
            fq.add(goalIdx);

            int[] dr = {-1, 1, 0, 0}, dc = {0, 0, -1, 1};
            boolean forwardTurn = true;

            while (!fq.isEmpty() || !bq.isEmpty()) {
                IntQueue curQ = forwardTurn ? fq : bq;
                IntQueue nxtQ = forwardTurn ? bq : fq;
                if (curQ.isEmpty()) {               // 当前方向没活，换边
                    forwardTurn = !forwardTurn;
                    continue;
                }
                int sz = curQ.size();
                while (sz-- > 0) {
                    int cur = curQ.poll();
                    int cost = table[cur];
                    nodes++;
                    expandLayerBidir(nxtQ, cur, cost + 1, table, k, dr, dc);
                }
                forwardTurn = !forwardTurn;         // 换边
            }

            long time = System.nanoTime() - start;
            double kb = table.length * 4.0 / 1024;
            totalBuildNs += time;

            /* 落盘 */
            try {
                writeDB(file, table, group);
            } catch (IOException e) {
                System.out.println("写盘失败：" + e.getMessage());
            }
            return table;
        }

        /* ---------------- 一层双向扩展（统一更新） ---------------- */
        private void expandLayerBidir(IntQueue oppositeQ, int curIdx, int newCost, int[] table,
                                      int k, int[] dr, int[] dc) {
            int pos = unrank(curIdx, k, len);
            boolean[] occ = new boolean[len];
            for (int i = 0; i < len; i++) occ[i] = ((pos >> i) & 1) == 1;

            for (int blank = 0; blank < len; blank++) {
                if (occ[blank]) continue;
                int br = blank / size, bc = blank % size;
                for (int d = 0; d < 4; d++) {
                    int nr = br + dr[d], nc = bc + dc[d];
                    if (nr < 0 || nr >= size || nc < 0 || nc >= size) continue;
                    int nxt = nr * size + nc;
                    if (!occ[nxt]) continue;
                    int newPos = pos & ~(1 << nxt);
                    newPos |= (1 << blank);
                    int newIdx = comboIndex(newPos, k, len);

                    /* 统一更新：更短或首次 */
                    if (table[newIdx] == -1 || table[newIdx] > newCost) {
                        table[newIdx] = newCost;
                        oppositeQ.add(newIdx);
                    }
                }
            }
        }

        /* ---------------- 工具 ---------------- */
        private String fileName(int gid) {
            return size + "puzzle-BiBFS-db-" +
                    Arrays.toString(groups[gid]).replace(" ", "") + ".db";
        }

        private void writeDB(String file, int[] table, int[] group) throws IOException {
            Path path = Paths.get(file);
            try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(path))) {
                dos.writeInt(size);
                dos.writeInt(group.length);
                for (int t : group) dos.writeInt(t);
                dos.writeInt(table.length);
                for (int v : table) dos.writeInt(v);
            }
        }

        private record DB(int[] table, int size, int[] group) {
        }

        private DB readDB(String file) throws IOException {
            Path path = Paths.get(file);
            try (DataInputStream dis = new DataInputStream(Files.newInputStream(path))) {
                int s = dis.readInt();
                int k = dis.readInt();
                int[] g = new int[k];
                for (int i = 0; i < k; i++) g[i] = dis.readInt();
                int len = dis.readInt();
                int[] t = new int[len];
                for (int i = 0; i < len; i++) t[i] = dis.readInt();
                return new DB(t, s, g);
            }
        }

        private int mask(PuzzleBoard board, int[] group) {
            int m = 0;
            for (int tile : group) m |= 1 << board.indexOf(tile);
            return m;
        }

        private int maskGroup(int[] group) {
            int[][] grid = new int[size][size];
            for (int i = 0, v = 1; i < size; i++)
                for (int j = 0; j < size; j++)
                    if (v < size * size) grid[i][j] = v++;
            PuzzleBoard b = new PuzzleBoard(grid);
            int mask = 0;
            for (int tile : group) mask |= 1 << b.indexOf(tile);
            return mask;
        }

        private static int unrank(int idx, int k, int n) {
            int res = 0, cnt = 0;
            for (int i = 0; i < n && cnt < k; i++) {
                int c = C(n - 1 - i, k - 1 - cnt);
                if (idx >= c) idx -= c;
                else {
                    res |= (1 << i);
                    cnt++;
                }
            }
            return res;
        }
    }

    /* ===================== 组合工具 ==================================== */
    private static int comboIndex(int bits, int k, int n) {
        int idx = 0, cnt = 0;
        for (int i = 0; i < n && cnt < k; i++) {
            if (((bits >> i) & 1) == 1) {
                idx += C(n - 1 - i, k - 1 - cnt);
                cnt++;
            }
        }
        return idx;
    }

    private static int C(int n, int k) {
        if (k < 0 || k > n) return 0;
        if (k > n - k) k = n - k;
        int res = 1;
        for (int i = 1; i <= k; i++) res = res * (n - k + i) / i;
        return res;
    }

    private static class IntQueue {
        private final int[] a;
        private int h = 0, t = 0;

        IntQueue(int c) {
            a = new int[c];
        }

        void add(int v) {
            a[t++] = v;
        }

        int poll() {
            return a[h++];
        }

        boolean isEmpty() {
            return h == t;
        }

        int size() {
            return t - h;
        }
    }
    /* ===================== 测试 ======================================== */
    public static void main(String[] args) throws IOException {

        Files.newDirectoryStream(Paths.get(""), "*puzzle-BiBFS-db-*.db")
                .forEach(p -> { try { Files.deleteIfExists(p); } catch (IOException ignore) {} });


        PuzzleBoard g3 = std8(), s3 = shuffle8();
        Predictor h = new DisjointPatternDatabasePredictor();
        System.out.println("8-puzzle (4-4) BiBFS h = " + h.heuristics(s3, g3));

        PuzzleBoard g4 = std15(), s4 = shuffle15();
        System.out.println("15-puzzle (6-6-3) BiBFS h = " + h.heuristics(s4, g4));

        /* 输出总建库耗时 */
        System.out.printf("=== 总建库耗时 %.3f s ===%n", DisjointPatternDatabasePredictor.PDB.totalBuildNs / 1_000_000_000.0);
    }

    private static PuzzleBoard std8() {
        int[][] g = {{1,2,3},{4,5,6},{7,8,0}};
        return new PuzzleBoard(g);
    }
    private static PuzzleBoard shuffle8() {
        int[][] s = {{1,4,3},{7,2,6},{5,8,0}};
        return new PuzzleBoard(s);
    }
    private static PuzzleBoard std15() {
        int[][] g = {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,0}};
        return new PuzzleBoard(g);
    }
    private static PuzzleBoard shuffle15() {
        int[][] s = {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,0,14,15}};
        return new PuzzleBoard(s);
    }
}