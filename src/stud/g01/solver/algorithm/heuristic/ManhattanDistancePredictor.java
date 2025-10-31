package stud.g01.solver.algorithm.heuristic;

import core.problem.State;
import core.solver.algorithm.heuristic.Predictor;

public class ManhattanDistancePredictor implements Predictor {

    /**
     * 计算汉明距离启发值
     * 它计算的是当前棋盘上有多少个数字不在其最终的目标位置上
     * @param state 需要被评估的当前状态
     * @param goal  目标状态
     * @return 错位的棋子数量(应该只有这个)
     */
    @Override
    public int heuristics(State state, State goal) {
        return 0;
    }
}
