package stud.g01.solver.algorithm.searcher;

import core.problem.Problem;
import core.problem.State;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class aStar extends AbstractSearcher {

    private final Predictor predictor;

    public aStar(Frontier frontier, Predictor predictor) {
        super(frontier);
        if (predictor == null) {
            throw new IllegalArgumentException("aStar 搜索器必须需要一个 Predictor (启发函数)");
        }
        this.predictor = predictor;
    }

    @Override
    public Deque<Node> search(Problem problem) {
        frontier.clear();
        explored.clear();
        nodesGenerated = 0;
        nodesExpanded = 0;
        // 获取根节点并加入 Frontier
        Node startNode = problem.root(this.predictor);
        frontier.offer(startNode);
        nodesGenerated = 1;

        // 循环直到 Frontier 为空
        while (!frontier.isEmpty()) {
            // 弹出 f-score 最低的节点
            Node currentNode = frontier.poll();
            State currentState = currentNode.getState();

            //    检查是否已在 explored 集合中 (防止重复扩展)
            //    注意：这一步仍然是必要的！Frontier 防止的是 frontier 内部的重复，
            //    explored 防止的是对已经处理过的节点的重复扩展。
            if (explored.contains(currentState)) {
                continue;
            }

            // 目标测试
            if (problem.goal(currentState)) {
                return buildPath(currentNode); // 找到解，构建并返回路径
            }

            // 将当前节点加入 explored 集合，并扩展
            explored.add(currentState);
            nodesExpanded++;

            List<Node> children = problem.childNodes(currentNode, this.predictor);
            nodesGenerated += children.size();

            // 将所有子节点交给“智能的” Frontier 处理
            for (Node child : children) {
                // 不需要任何 if-else 判断，直接 offer！
                // PqFrontier 内部会自动处理所有情况。
                frontier.offer(child);
            }
        }

        // 搜索失败
        return null;
    }

    // 辅助方法，用于从目标节点回溯构建路径
    private Deque<Node> buildPath(Node goalNode) {
        Deque<Node> path = new ArrayDeque<>();
        Node curr = goalNode;
        while (curr != null) {
            path.addFirst(curr);
            curr = curr.getParent();
        }
        return path;
    }
}