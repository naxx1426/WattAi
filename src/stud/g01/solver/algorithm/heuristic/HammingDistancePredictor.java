package stud.g01.solver.algorithm.heuristic;

import core.problem.State;
import core.solver.algorithm.heuristic.Predictor;

public class HammingDistancePredictor implements Predictor {

    /**
     * 计算N-Puzzle状态的曼哈顿距离启发值
     * 曼哈顿距离是所有数字棋子与其目标位置的水平和垂直距离之和
     * @param state 需要被评估的当前状态
     * @param goal  目标状态
     * @return 计算出的曼哈顿距离值(应该只有这个)
     */
    @Override
    public int heuristics(State state, State goal) {
        return 0;
    }
}
