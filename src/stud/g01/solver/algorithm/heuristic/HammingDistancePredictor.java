package stud.g01.solver.algorithm.heuristic;

import core.problem.State;
import core.solver.algorithm.heuristic.Predictor;

public class HammingDistancePredictor implements Predictor {

    /**
     * 计算N-Puzzle状态的汉明距离启发值
     * 汉明距离是……………………
     * @param state 需要被评估的当前状态
     * @param goal  目标状态
     * @return 计算出的汉明距离值
     */
    @Override
    public int heuristics(State state, State goal) {
        return 0;
    }
}
