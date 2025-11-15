package stud.g01.queue;

import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class StackFrontier implements Frontier {
    private final Deque<Node> stack = new ArrayDeque<>();

    @Override
    public boolean offer(Node node) {
        return stack.offerLast(node);//»Î’ª
    }

    @Override
    public Node poll() {
        return stack.pollLast();//≥ˆ’ª
    }

    @Override
    public int size() {
        return stack.size();
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public void clear() {
        stack.clear();
    }

    @Override
    public boolean contains(Node node) {
        return stack.contains(node);
    }
}
