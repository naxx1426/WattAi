package stud.g01.queue;

import core.solver.queue.Frontier;
import core.solver.queue.Node;
import core.problem.State;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Frontier 接口的优先队列高性能实现
 * 内部结合了优先队列 (PriorityQueue) 和哈希表 (HashMap)，
 * 以高效支持 A* 算法中对节点的查找、插入和更新操作。
 */
public class PqFrontier implements Frontier {

    // 优先队列，用于存储节点并根据 f-score (g+h) 自动排序
    private final PriorityQueue<Node> queue;
    // 辅助哈希表，用于快速查找某个状态 (State) 是否已存在于队列中
    private final Map<State, Node> map;

    /**
     * 构造一个新的 PqFrontier 实例
     */
    public PqFrontier() {
        this.queue = new PriorityQueue<>();
        this.map = new HashMap<>();
    }

    /**
     * 从 Frontier 中移除并返回 f-score 最低的节点
     * @return 队列中 f-score 最低的节点；如果队列为空, 则返回 null
     */
    @Override
    public Node poll() {
        // 从优先队列中取出最优节点
        Node node = queue.poll();
        if (node != null) {
            // 保持哈希表与队列的同步，移除对应状态的记录
            map.remove(node.getState());
        }
        return node;
    }

    /**
     * 清空 Frontier 中的所有节点
     */
    @Override
    public void clear() {
        queue.clear();
        map.clear();
    }

    /**
     * 获取 Frontier 中当前的节点数量
     * @return 队列中的节点总数
     */
    @Override
    public int size() {
        return queue.size();
    }

    /**
     * 检查 Frontier 是否为空
     * @return 如果队列中没有节点则返回 true, 否则返回 false
     */
    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * 高效地检查 Frontier 中是否包含代表某个状态的节点
     * @param node 要检查的节点 (主要使用其 State)
     * @return 如果包含该节点所代表的状态，则返回 true
     */
    @Override
    public boolean contains(Node node) {
        // 利用哈希表 O(1) 的时间复杂度进行快速查找
        return map.containsKey(node.getState());
    }

    /**
     * 智能地将一个节点插入 Frontier
     * 此方法实现了 A* 算法的关键逻辑：
     * 1. 如果节点状态是全新的，直接插入。
     * 2. 如果已存在相同状态的节点，则比较路径成本 (g-cost)，并保留成本更低的那一个。
     * @param node 要插入的节点
     * @return 如果 Frontier 因本次操作而发生改变，则返回 true
     */
    @Override
    public boolean offer(Node node) {
        State state = node.getState();
        // 通过哈希表查找是否已存在代表相同状态的节点
        Node existingNode = map.get(state);

        // Frontier 中没有这个状态的节点，直接添加
        if (existingNode == null) {
            queue.add(node);
            map.put(state, node);
            return true;
        }

        // 已存在相同状态的节点，但新节点的路径更优 (g-cost 更小)
        if (node.getPathCost() < existingNode.getPathCost()) {
            // 移除旧节点，添加新节点，以此来替换旧节点
            queue.remove(existingNode);
            queue.add(node);
            // 同时更新哈希表中的引用，使其指向更优的新节点
            map.put(state, node);
            return true;
        }

        // 已存在的节点路径更优或相等，不做任何操作
        return false;
    }
}