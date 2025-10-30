package core.solver.algorithm.searcher;

import java.util.*;

import core.problem.Problem;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.queue.Frontier;
import core.solver.queue.Node;

/**
 * 最佳优先搜索算法。 根据对Frontier中Node的排序方法，以及所采用的启发函数的不同 可以配置出不同的特性的算法。
 *  前提条件：f(n) = g(n) + h(n)；其中h(n)满足单调限制!  h(n)≡0时，Dijkstra算法；g(n)=0时，Greedy Best-First
 * final类，不能被继承的类。同学们不可改写这个类！！
 *
 */
public final class BestFirstSearcher extends AbstractSearcher {

	private final Predictor predictor; //预测器，对当前状态进行启发式估值

	/**
	 * 构造函数
	 *
	 * @param frontier Node对象的一个优先队列，可以确定一个状态所对应的结点是否在frontier中，
	 * @param predictor 具体的预测器（不在位将牌，曼哈顿距离等）
	 */
	public BestFirstSearcher(Frontier frontier, Predictor predictor) {
		super(frontier);
		this.predictor = predictor;
	}

	@Override
	public Deque<Node> search(Problem problem) {
		// 先判断问题是否可解，无解时直接返回解路径为null
		if (!problem.solvable()) {
			return null;
		}

		// 每次新的搜索开始前，先清理掉Frontier和Explored的内容
		frontier.clear();
		explored.clear();
		nodesExpanded = 0;
		nodesGenerated = 0;

		// 起始节点root
		Node root = problem.root(predictor);
		frontier.offer(root);

		// 搜索。。。
		while (true) {

			if (frontier.isEmpty())  // 在到达目标状态之前frontier变为空，则问题无解
				return null;

			Node node = frontier.poll(); // 从优先队列frontier中取出估值最小的节点

			if (problem.goal(node.getState())) { //进入目标状态
				return generatePath(node);
			}

			explored.add(node.getState());

			//对节点node进行扩展  Expansion
			for (Node child : problem.childNodes(node, predictor)) {
				nodesGenerated++;
				if (!expanded(child)) // 如果新生成的节点（新扩展出的节点）还没有被扩展，则插入到frontier中。
					frontier.offer(child);  //同学们可以试一下，
				       // revisited也一律加到Frontier的情况，和扔掉不好的两种情况
				// 如果已经扩展过，就舍弃掉。
			}
			nodesExpanded++;
		}
	}
}