package stud.g01.solver.algorithm.heuristic;

import core.problem.State;
import core.solver.algorithm.heuristic.Predictor;

public class ManhattanDistancePredictor implements Predictor {

    /**
     * ���㺺����������ֵ
     * ��������ǵ�ǰ�������ж��ٸ����ֲ��������յ�Ŀ��λ����
     * @param state ��Ҫ�������ĵ�ǰ״̬
     * @param goal  Ŀ��״̬
     * @return ��λ����������(Ӧ��ֻ�����)
     */
    @Override
    public int heuristics(State state, State goal) {
        return 0;
    }
}
