package stud.g01.solver.algorithm.searcher;

import core.problem.Problem;
import core.problem.State;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.Deque;
import java.util.ArrayDeque;

/**
 * 迭代加深A*算法
 * 内存效率高，适合状态空间大的问题
 */
public class IdAStar extends AbstractSearcher {

    private final Predictor predictor;
    private Node goalNode;//用于记录找到的目标节点

    public IdAStar(Frontier frontier, Predictor predictor) {
        super(frontier);
        this.predictor = predictor;
    }

    @Override
    public Deque<Node> search(Problem problem) {
        //可解性判断
        if(!problem.solvable())
            return null;

        //清理历史数据
        explored.clear();
        nodesGenerated = 0;
        nodesExpanded = 0;
        goalNode = null;

        //获取根节点并计算启发值
        Node root = problem.root();
        int rootHeuristic = predictor.heuristics(root.getState(), problem.getGoal());
        root.setHeuristic(rootHeuristic);

        //初始阙值设置为根节点的启发值
        int threshold = rootHeuristic;

        System.out.println("IDA*开始搜索，初始阙值：" + threshold);

        //迭代加深搜索
        while (true) {
            System.out.println(("当前阙值：" + threshold));

            int minExceed = depthLimitedSearch(problem, root, threshold);
            if (goalNode != null) {
                //找到解
                System.out.println("IDA*找到解，阙值：" + threshold);
                return generatePath(goalNode);
            }

            if (minExceed == Integer.MAX_VALUE) {
                //无解
                System.out.println("IDA*无解");
                return null;
            }

            if (minExceed == -1) {
                //理论上不会发生，安全处理
                System.out.println("IDA*搜索异常");
                return null;
            }

            //更新阙值继续搜索
            threshold = minExceed;
            explored.clear();//清除当前迭代的搜索记录

            //安全机制，防止无限循环
            if (threshold > 1000) {
                System.out.println("IDA*阙值过大，可能无解");
                return null;
            }
        }
    }

    /**
     * 深度受限搜索
     * @param problem 问题实例
     * @param node 当前节点
     * @param threshold 当前阙值
     * @return 最小超过阙值，如果找到目标返回-1，如果无解返回Integer.MAX_VALUE
     */
    private int depthLimitedSearch(Problem problem, Node node, int threshold) {
        nodesExpanded++;

        //计算f(n) = g(n) + h(n)
        int fValue = node.getPathCost() + node.getPathCost();

        //如果超过阙值，返回最小的超过值
        if (fValue > threshold) {
            return fValue;
        }

        //目标检测
        if (problem.goal(node.getState())) {
            goalNode = node;
            return -1;
        }

        int minExceed = Integer.MAX_VALUE;

        //扩展当前节点
        for (Node child : problem.childNodes(node)) {
            nodesGenerated++;

            //计算子节点的启发值
            int childHeuristic = predictor.heuristics(child.getState(), problem.getGoal());
            child.setHeuristic(childHeuristic);

            //避免重复状态
            if (explored.contains(child.getState())) {
                continue;
            }

            explored.add(child.getState());

            //递归搜索
            int result =  depthLimitedSearch(problem, child, threshold);

            if (result == -1) {
                return -1;
            }

            if (result < minExceed) {
                minExceed = result;
            }

            // 回溯：从探索集合中移除，允许其他路径探索
            explored.remove(child.getState());
        }
        return minExceed;
    }

    /**
     * 获取目标状态（用于Problem接口
     */

    private State getGoal(Problem problem) {
        try {
            java.lang.reflect.Field goalField = problem.getClass().getDeclaredField("goal");
            goalField.setAccessible(true);
            return  (State)  goalField.get(problem);
        } catch ( Exception e) {
            throw new RuntimeException("无法获取目标状态", e);
        }
    }
}
