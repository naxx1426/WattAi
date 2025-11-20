package stud.g01.solver.algorithm.heuristic;

import core.problem.State;
import core.solver.algorithm.heuristic.Predictor;
import stud.g01.problem.npuzzle.PuzzleBoard;

/**
 * 计算汉明距离启发值
 * 它计算的是当前棋盘上有多少个数字不在其最终的目标位置上
 * @param state 需要被评估的当前状态
 * @param goal  目标状态
 * @return 错位的棋子数量(应该只有这个)
 */

/**
 * 曼哈顿距离：只考虑数字块，忽略空白
 * 它计算的是每个数字块到其目标位置的横向+纵向距离之和
 */
public class ManhattanDistancePredictor implements Predictor {

    @Override
    public int heuristics(State state, State goal) {
        PuzzleBoard curr = (PuzzleBoard) state;
        PuzzleBoard targ = (PuzzleBoard) goal;
        int size = curr.getSize();

        int dist = 0;
        // 遍历每个格子
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                int num = curr.getGrid()[r][c];
                if (num == 0) continue;          // 跳过空白
                int idxTarg = targ.indexOf(num); // 目标线性下标
                int tr = idxTarg / size;
                int tc = idxTarg % size;
                dist += Math.abs(r - tr) + Math.abs(c - tc);
            }
        }
        return dist;
    }

    /* ====================== 内置测试 ====================== */
    public static void main(String[] args) {
        /* 初始格局（0 1 2
         *             3 4 5
         *             6 7 8） */
        int[][] start = {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8}
        };

        /* 目标格局（1 2 3
         *             4 5 6
         *             7 8 0） */
        int[][] goal = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 0}
        };

        PuzzleBoard startBoard = new PuzzleBoard(start);
        PuzzleBoard goalBoard = new PuzzleBoard(goal);

        ManhattanDistancePredictor predictor = new ManhattanDistancePredictor();
        int dist = predictor.heuristics(startBoard, goalBoard);

        System.out.println("计算结果 = " + dist);
        System.out.println("预期结果 = 12");
        System.out.println(dist == 12 ? "? 逻辑正确" : "? 逻辑错误");
        // 放在 main 里计算完后
    }
}