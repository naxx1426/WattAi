package stud.g01.solver.algorithm.heuristic;

import core.problem.State;
import core.solver.algorithm.heuristic.Predictor;

public class HammingDistancePredictor implements Predictor {

    /**
     * ����N-Puzzle״̬�������پ�������ֵ
     * �����پ���������������������Ŀ��λ�õ�ˮƽ�ʹ�ֱ����֮��
     * @param state ��Ҫ�������ĵ�ǰ״̬
     * @param goal  Ŀ��״̬
     * @return ������������پ���ֵ(Ӧ��ֻ�����)
     */
    @Override
    public int heuristics(State state, State goal) {
        return 0;
    }
}
