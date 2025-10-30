package stud.queue;

import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.ArrayDeque;
import java.util.Stack;

public class StackFrontier extends ArrayDeque<Node> implements Frontier {
    @Override
    public Node poll() {
        return super.pop();
    }

    @Override
    public boolean contains(Node node) {
        return super.contains(node);
    }

    @Override
    public boolean offer(Node node) {
        super.push(node);
        return true;
    }
}
