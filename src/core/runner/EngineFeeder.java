package core.runner;

import core.problem.Problem;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.algorithm.searcher.BestFirstSearcher;
import core.solver.queue.EvaluationType;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.queue.Frontier;
import stud.queue.StackFrontier;
import stud.solver.IdAStar;

import java.util.ArrayList;

/**
 * 为搜索算法提供各样素材。包括
 *    问题实例列表
 *    使用的Frontier，
 *    使用的启发式函数 Predictor，
 *
 */
public abstract class EngineFeeder {
    /**
     * 根据存放问题输入样例的文本文件的内容，生成问题实例列表
     * @param problemLines  字符串数组，存放的是：问题输入样例的文本文件的内容
     * @return
     */
    public abstract ArrayList<Problem> getProblems(ArrayList<String> problemLines);

    /**
     * 生成采取某种估值机制的Frontier；与问题无关，
     *
     * @param type 结点评估器的类型
     * @return 使用评估机制的一个Frontier实例
     */
    public abstract Frontier getFrontier(EvaluationType type);

    /**
     * 获得对状态进行估值的Predictor；不同问题有不同的估值函数
     *
     * @param type 不同问题的估值函数的类型
     * @return 启发函数
     */
    public abstract Predictor getPredictor(HeuristicType type);

    /**
     * 用来做对比实验的IdAStar （Iterative Deepening AStar，迭代加深的AStar）
     */
    public final AbstractSearcher getIdaStar(HeuristicType type) {
        Predictor predictor = getPredictor(type);
        // 获取Frontier，其Node以g(n)+h(n)的升序排列，相同时，按照g(n)的升序排列
        Frontier frontier = new StackFrontier();
        //生成IdAStar引擎（算法实例）
        return new IdAStar(frontier, predictor);
    }

    /**
     * 用来做对比实验的AStar, 对所有问题都是一样的
     * 可配置使用不同的启发函数
     * @param type 可配置的启发函数类型
     */
    public final AbstractSearcher getAStar(HeuristicType type) {
        Predictor predictor = getPredictor(type);
        // 获取Frontier，其Node以g(n)+h(n)的升序排列，相同时，按照g(n)的升序排列
        Frontier frontier = getFrontier(EvaluationType.FULL);
        // 根据frontier和predictor生成AStar引擎
        return new BestFirstSearcher(frontier, predictor);
    }

    /**
     * 用来做对比实验的Dijkstra，对所有的问题都是一样的
     * 
     * @return Dijkstra搜索算法
     */
    public final AbstractSearcher getDijkstra() {
        // 获取Frontier，其Node以g(n)的升序排列
        Frontier frontier = getFrontier(EvaluationType.PATH_COST);
        // predictor：h(n)≡0，即Dijkstra算法
        return new BestFirstSearcher(frontier, (state, goal) -> 0);
    }
}
