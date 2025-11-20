package stud.g01.solver.algorithm.heuristic;

import core.problem.State;
import core.solver.algorithm.heuristic.Predictor;
import stud.g01.problem.npuzzle.PuzzleBoard;

public class BlankDistancePredictor implements Predictor {

    @Override
    public int heuristics(State state, State goal) {
        PuzzleBoard s = (PuzzleBoard) state;
        PuzzleBoard g = (PuzzleBoard) goal;
        int size = s.getSize();
        int[][] sg = s.getGrid();
        int[][] gg = g.getGrid();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (sg[i][j] == 0 && gg[i][j] != 0) {
                    return 1; // 空位不在目标位置，至少需要一次移动
                }
            }
        }
        return 0; // 空位已经在正确位置
    }


    /* ====================== 内置测试 ====================== */
    public static void main(String[] args) {
        int size = 3;

        /* 情况1：空白错位（空白在(0,0)，目标在(2,2)）→ 预期 1 */
        int[][] s1 = {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8}
        };
        int[][] g1 = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 0}
        };

        /* 情况2：空白正确（空白已在(2,2)）→ 预期 0 */
        int[][] s2 = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 0}
        };
        int[][] g2 = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 0}
        };

        PuzzleBoard b1 = new PuzzleBoard(s1);
        PuzzleBoard b2 = new PuzzleBoard(s2);
        PuzzleBoard goal = new PuzzleBoard(g1); // g1 与 g2 相同

        BlankDistancePredictor p = new BlankDistancePredictor();

        int dist1 = p.heuristics(b1, goal);
        int dist2 = p.heuristics(b2, goal);

        System.out.println("空白错位测试 = " + dist1 + " (预期 1)");
        System.out.println("空白正确测试 = " + dist2 + " (预期 0)");
        System.out.println((dist1 == 1 && dist2 == 0) ? "? 逻辑正确" : "? 逻辑错误");
    }
}
