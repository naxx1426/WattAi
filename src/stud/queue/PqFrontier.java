package stud.queue;

import core.solver.queue.Frontier;
import core.solver.queue.Node;

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
