package stud.g01.runner;

import core.problem.Problem;
import core.problem.State;
import core.runner.EngineFeeder;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.queue.EvaluationType;
import core.solver.queue.Frontier;
import core.solver.queue.Node;
import stud.g01.problem.npuzzle.NPuzzleProblem;
import stud.g01.problem.npuzzle.PuzzleBoard;
import stud.queue.StackFrontier;

import java.util.ArrayList;
import java.util.Comparator;

//Fix Me   //Fix Me
public class PuzzleFeeder extends EngineFeeder {

    /**
     * 获得当前问题对应的棋盘大小，初始棋盘和目标棋盘
     */
    @Override
    public ArrayList<Problem> getProblems(ArrayList<String> problemLines) {
        // 创建一个最终要返回的问题列表
        ArrayList<Problem> allProblems = new ArrayList<>();

        for (String line : problemLines) {
            String[] parts = line.split(" ");
            // 第一个数字代表棋盘大小
            int n = Integer.parseInt(parts[0]);
            // 准备两个二维数组来存棋盘的初始状态和最终状态
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

            // 是否有解
            if (problem.solvable()) {
                allProblems.add(problem);
            } else {
                System.out.println("无解，跳过");
            }
        }
        return allProblems;
    }

    @Override
    public Frontier getFrontier(EvaluationType type) {
        //对于IDA*，需要StackFrontier
        if(type == EvaluationType.HEURISTIC) {
            return new StackFrontier();//深度优先的栈实现
        }


    }

    @Override
    public Predictor getPredictor(HeuristicType type) {
        return null;
    }
}
