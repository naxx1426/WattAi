package stud.queue;

import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 用于BFS的队列
 */
public class QueueFrontier extends ArrayDeque<Node> implements Queue<Node>, Frontier{
    @Override
    public boolean contains(Node node) {
        return super.contains(node);
    }
}