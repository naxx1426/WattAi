package stud.g01.solver.algorithm.heuristic;

import core.problem.State;
import core.solver.algorithm.heuristic.Predictor;
import stud.g01.problem.npuzzle.PuzzleBoard;

/*
 * 计算汉明距离启发值
 * 它计算的是当前棋盘上有多少个数字不在其最终的目标位置上
 * @param state 需要被评估的当前状态
 * @param goal  目标状态
 * @return 错位的棋子数量(应该只有这个)
 */

public class HammingDistancePredictor implements Predictor {

    @Override
    public int heuristics(State state, State goal) {
        PuzzleBoard s = (PuzzleBoard) state;
        PuzzleBoard g = (PuzzleBoard) goal;
        int size = s.getSize();
        int[][] sg = s.getGrid();
        int[][] gg = g.getGrid();

        int misplaced = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // 标准汉明距离：比较所有位置，包括空位
                if (sg[i][j] != gg[i][j]) {
                    misplaced++;
                }
            }
        }
        return misplaced;
    }
}