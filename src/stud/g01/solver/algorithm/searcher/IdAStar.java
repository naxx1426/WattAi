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
    int maxDepth = 0; //阙值
    private Node answer;  //解节点
    private Problem problem;

    public IdAStar(Frontier frontier, Predictor predictor) {
        super(frontier);
        this.predictor = predictor;
    }

    // 利用dfs
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
        // 判断问题是否可解，无解时直接返回解路径为null
        if (!problem.solvable()) {
            return null;
        }

        this.problem = problem;

        // 不需要open表和close表
        nodesExpanded = 0;
        nodesGenerated = 0;

        // 起始节点root
        Node root = problem.root(predictor);

        // 设置搜索的上限为初始节点的启发值
        // 每次搜索更新
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