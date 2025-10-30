package core.solver.algorithm.searcher;

import core.problem.Problem;
import core.problem.State;
import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * 抽象的搜素者，保留搜索历史。
 * 使用explored保存已经扩展过的结点，
 * 使用Frontier保存已扩展出来但尚未被扩展的结点
 *
 */
public abstract class AbstractSearcher {

    // 已经访问过的节点集合
    protected final Set<State> explored = new HashSet<>();

    // 还未扩展的节点队列
    protected final Frontier frontier;

    protected int nodesGenerated = 0;   //已生成的节点的个数
    protected int nodesExpanded = 0;    //已扩展的节点的个数

    public AbstractSearcher(Frontier frontier) {
        this.frontier = frontier;
    }

    public int nodesGenerated() {
        return nodesGenerated;
    }

    public int nodesExpanded() {
        return nodesExpanded;
    }

    public abstract Deque<Node> search(Problem problem);

    protected Deque<Node> generatePath(Node goal) {
        Deque<Node> stack = new ArrayDeque<>();
        Node curr = goal;
        while (curr.getParent() != null) {
            stack.push(curr);
            curr = curr.getParent();
        }
        return stack;
    }

//    /**
//     *
//     * @return	算法执行至此，已经被扩展的结点个数。
//     */
//    @Override
//    public int nodesExpanded() {
//        return explored.size();  // explored表中的节点有可能从explored表中删除，然后重新放入Frontier，从而有可能被重新扩展。
//                                 // 所以搜索结束后，explored中的节点的个数，有可能小于被扩展过的节点总个数
//                                 // 当启发函数满足单调限制时，二者才相等。
//    }
//
//    /**
//     *
//     * @return 已经生成的结点的个数；
//     * 在启发函数满足单调限制时，frontier中的节点也有可能被新生成的重复访问节点替换掉
//     * 所以，这个数目即使在满足单调限制时，也是不准确的
//     */
//    @Override
//    public int nodesGenerated(){
//        return explored.size() + frontier.size();
//    }

    /**
     * 节点是否已经扩展过
     * @param node
     * @return
     */
    protected boolean expanded(Node node) {
        return explored.contains(node.getState());
    }
}