package stud.runner;

import core.problem.Problem;
import core.runner.EngineFeeder;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.queue.EvaluationType;
import core.solver.queue.Frontier;
import core.solver.queue.Node;
import stud.problem.pathfinding.GridType;
import stud.problem.pathfinding.PathFinding;
import stud.problem.pathfinding.Position;
import stud.queue.ListFrontier;

import java.util.ArrayList;

/**
 * 寻路问题的EngineFeeder，起名为WalkerFeeder
 * 同学们可以参考编写自己的PuzzleFeeder
 */
public class WalkerFeeder extends EngineFeeder {
    @Override
    public ArrayList<Problem> getProblems(ArrayList<String> problemLines) {
        //问题规模, 输入的第一行
        int size = Integer.parseInt(problemLines.get(0));
        //读入地图，size * size的Grid，从第2行开始的size行数据，每行有size个数字
        GridType[][] map = getMap(problemLines, size);

        /* 读入各个问题 */
        ArrayList<Problem> problems = new ArrayList<>();
        int lineNo = size + 1;
        while (lineNo < problemLines.size()){
            //读入问题实例
            PathFinding problem = getPathFinding(problemLines.get(lineNo), size);
            //为每个问题实例设置地图
            problem.setGrids(map);
            //添加到问题列表
            problems.add(problem);
            lineNo++;
        } //读入问题结束

        return problems;
    }

    /**
     * 生成寻路问题的一个实例
     * @param problemLine
     * @param size
     * @return
     */
    private PathFinding getPathFinding(String problemLine, int size) {
        String[] cells = problemLine.split(" ");
        //读入初始状态
        int row = Integer.parseInt(cells[0]);
        int col = Integer.parseInt(cells[1]);
        Position initialState = new Position(row, col);
        //读入目标状态
        //读入初始状态
        row = Integer.parseInt(cells[2]);
        col = Integer.parseInt(cells[3]);
        Position goal = new Position(row, col);

        //生成寻路问题的实例
        return new PathFinding(initialState, goal, size);
    }

    /**
     *
     * @param problemLines
     * @param size
     * @return
     */
    private GridType[][] getMap(ArrayList<String> problemLines, int size) {
        GridType[][] map = new GridType[size][];
        for (int i = 0; i < size; i++){
            map[i] = new GridType[size];
            String[] cells = problemLines.get(i + 1).split(" ");
            for (int j = 0; j < size; j++){
                int cellType = Integer.parseInt(cells[j]);
                map[i][j] = GridType.values()[cellType];
            }
        }
        return map;
    }


    @Override
    public Frontier getFrontier(EvaluationType type) {
        return new ListFrontier(Node.evaluator(type));
    }

    /**
     * 获得对状态进行估值的Predictor
     *
     * @param type 估值函数的类型
     * @return  估值函数
     */
    @Override
    public Predictor getPredictor(HeuristicType type) {
        return Position.predictor(type);
    }

}
