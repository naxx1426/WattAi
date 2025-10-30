package stud.g01.runner;

import core.problem.Problem;
import core.problem.State;
import core.runner.EngineFeeder;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.queue.EvaluationType;
import core.solver.queue.Frontier;
import stud.g01.problem.npuzzle.NPuzzleProblem;
import stud.g01.problem.npuzzle.PuzzleBoard;

import java.util.ArrayList;
//Fix Me   //Fix Me
public class PuzzleFeeder extends EngineFeeder {

    /**
     * ��õ�ǰ�����Ӧ�����̴�С����ʼ���̺�Ŀ������
     */
    @Override
    public ArrayList<Problem> getProblems(ArrayList<String> problemLines) {
        // ����һ������Ҫ���ص������б�
        ArrayList<Problem> allProblems = new ArrayList<>();

        for (String line : problemLines) {
            String[] parts = line.split(" ");
            // ��һ�����ִ������̴�С
            int n = Integer.parseInt(parts[0]);
            // ׼��������ά�����������̵ĳ�ʼ״̬������״̬
            int[][] startGrid = new int[n][n];
            int[][] goalGrid = new int[n][n];
            int k = 1;

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    startGrid[i][j] = Integer.parseInt(parts[k]);
                    k++;
                }
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    goalGrid[i][j] = Integer.parseInt(parts[k]);
                    k++;
                }
            }

            State startState = new PuzzleBoard(startGrid);
            State goalState = new PuzzleBoard(goalGrid);

            Problem problem = new NPuzzleProblem(startState, goalState, n);

            // �Ƿ��н�
            if (problem.solvable()) {
                allProblems.add(problem);
            } else {
                System.out.println("�޽⣬����");
            }
        }
        return allProblems;
    }

    @Override
    public Frontier getFrontier(EvaluationType type) {
        return null;
    }

    @Override
    public Predictor getPredictor(HeuristicType type) {
        return null;
    }
}
