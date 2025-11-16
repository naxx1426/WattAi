package stud.g01.runner;

import core.problem.Problem;
import core.problem.State;
import core.runner.EngineFeeder;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.queue.EvaluationType;
import core.solver.queue.Frontier;
import stud.g01.solver.algorithm.heuristic.HammingDistancePredictor;
import stud.g01.solver.algorithm.heuristic.ManhattanDistancePredictor;
import stud.g01.queue.PqFrontier;
import stud.g01.queue.StackFrontier;
import stud.g01.problem.npuzzle.NPuzzleProblem;
import stud.g01.problem.npuzzle.PuzzleBoard;
import stud.g01.solver.algorithm.searcher.aStar;

import java.util.ArrayList;

public class PuzzleFeeder extends EngineFeeder {

    /**
     * 从输入的字符串列表中解析并创建 N-Puzzle 问题实例
     * @param problemLines 包含所有谜题定义字符串的列表
     * @return 一个仅包含所有可解的 Problem 实例的列表
     */
    @Override
    public ArrayList<Problem> getProblems(ArrayList<String> problemLines) {
        ArrayList<Problem> allProblems = new ArrayList<>();

        // 遍历输入文件中的每一行来解析单个问题
        for (String line : problemLines) {
            String[] parts = line.split(" ");
            // 第一个数字代表棋盘大小 (N)
            int n = Integer.parseInt(parts[0]);

            int[][] startGrid = new int[n][n];
            int[][] goalGrid = new int[n][n];
            int k = 1; // 用于遍历 parts 数组的索引

            // 解析初始状态棋盘
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    startGrid[i][j] = Integer.parseInt(parts[k++]);
                }
            }

            // 解析目标状态棋盘
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    goalGrid[i][j] = Integer.parseInt(parts[k++]);
                }
            }

            State startState = new PuzzleBoard(startGrid);
            State goalState = new PuzzleBoard(goalGrid);
            Problem problem = new NPuzzleProblem(startState, goalState, n);

            // 在添加问题前，过滤无解的谜题
            if (problem.solvable()) {
                allProblems.add(problem);
            } else {
                System.out.println("无解 " + line + "，跳过");
            }
        }
        return allProblems;
    }

    /**
     * 根据指定的评估类型，提供相应的 Frontier 实例
     * @param type 框架请求的评估类型 (FULL, HEURISTIC, etc.)
     * @return 一个实现了 Frontier 接口的实例
     */
    @Override
    public Frontier getFrontier(EvaluationType type) {
        return switch (type) {
            // 用于 A* 算法 (f = g + h)
            case FULL -> new PqFrontier();
            // 用于 IDA* 等其他算法
            case HEURISTIC -> new StackFrontier();
            default -> throw new IllegalArgumentException("不支持的 EvaluationType: " + type);
        };
    }

    /**
     * 根据指定的启发函数类型，提供相应的 Predictor (启发函数) 实例
     * @param type 框架请求的启发函数类型 (MANHATTAN, MISPLACED)
     * @return 一个实现了 Predictor 接口的实例
     */
    @Override
    public Predictor getPredictor(HeuristicType type) {
        return switch (type) {
            // 曼哈顿距离
            case MANHATTAN -> new ManhattanDistancePredictor();
            // 错位棋子距离 (汉明距离)
            case MISPLACED -> new HammingDistancePredictor();
            default -> {
                System.out.println("未知的启发函数, 默认使用曼哈顿距离");
                yield new ManhattanDistancePredictor();
            }
        };
    }

    /**
     * 重写父类的 getAStar 方法，以返回我们自定义的 aStar 搜索器
     * 这是将我们自己的 A* 实现注入框架的关键。
     * @param type 框架请求的启发函数类型
     * @return 一个我们自定义的 aStar 搜索器实例
     */
    @Override
    public AbstractSearcher getAStar(HeuristicType type) {
        // 获取指定的启发函数实例
        Predictor predictor = this.getPredictor(type);
        // 获取用于 A* 的优先队列 Frontier
        Frontier frontier = this.getFrontier(EvaluationType.FULL);
        // 构造并返回我们自己的 aStar 搜索器实例
        return new aStar(frontier, predictor);
    }
}