package stud.g01.solver.algorithm.searcher;

import core.problem.Problem;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.Deque;

public class IdAStar extends AbstractSearcher {
    public IdAStar(Frontier frontier) {
        super(frontier);
    }

    @Override
    public Deque<Node> search(Problem problem) {
        return null;
    }
}
