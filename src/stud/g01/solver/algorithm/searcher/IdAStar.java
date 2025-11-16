package stud.g01.solver.algorithm.searcher;

import core.problem.Problem;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.Deque;

/**
 * 迭代加深的A*算法，需要同学们自己编写完成
 */

public class IdAStar extends AbstractSearcher {
    Predictor predictor;
    int maxDepth = 0;
    private Node answer;
    private Problem problem;

    public IdAStar(Frontier frontier, Predictor predictor) {
        super(frontier);
        this.predictor = predictor;
    }

    // 一次dfs
    public int IDASearch(Node node, Node parent) {
        if (node.evaluation() > maxDepth)
            return node.evaluation();
        if (problem.goal(node.getState())) {
            answer = node;
            return 0;
        }
        nodesExpanded++;
        int res = Integer.MAX_VALUE;
        for (Node child : problem.childNodes(node, predictor)) {
            // 这里没有使用close表，所以判重改成了下一步不会回到父节点
            // 但是即使使用了close表，也不能用contains方法判断是否重复
            // 因为每次搜索前没有清空close表，所以会有问题
            // 如果每次搜索前清空close表，发现求出来的解路径不对
            // 只需要保证不会重复访问同一个节点即可
            if (parent != null && child.getState().equals(parent.getState()))
                continue;
            nodesGenerated++;
            int t = IDASearch(child, node);
            if (t == 0)
                return 0;
            res = Math.min(res, t);
        }
        return res;
    }

    @Override
    public Deque<Node> search(Problem problem) {
        // 先判断问题是否可解，无解时直接返回解路径为null
        if (!problem.solvable()) {
            return null;
        }

        this.problem = problem;

        // 这里其实不需要open表和close表, 因为IDASearch采用的是深度优先搜索
        // 所以只需要记个数就行了
        nodesExpanded = 0;
        nodesGenerated = 0;

        // 起始节点root
        Node root = problem.root(predictor);

        // 设置搜索的上限, 查找的资料都取的是初始节点的h值, 所以就这样写了
        // 根据每次搜索的结果来更新这个值
        maxDepth = root.getHeuristic();
        while (true) {
            int res = IDASearch(root, null);
            if (res == 0)
                break;
            maxDepth = res;
        }
        return generatePath(answer);
    }
}