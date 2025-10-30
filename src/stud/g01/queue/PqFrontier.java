package stud.g01.queue;

import core.solver.queue.Frontier;
import core.solver.queue.Node;
import stud.queue.QueueFrontier;

import java.util.PriorityQueue;

public class PqFrontier extends PriorityQueue<Node> implements Frontier {
    @Override
    public boolean contains(Node node) {
        return false;
    }

    @Override
    public boolean offer(Node node) {
        return false;
    }
}
