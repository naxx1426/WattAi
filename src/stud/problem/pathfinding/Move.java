package stud.problem.pathfinding;

import core.problem.Action;

/**
 *
 * 寻路的移动动作，根据情况，可以NSEW四个方向，或者8个方向。
 */
public class Move extends Action {

    private final Direction direction;

    public Move(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public void draw() {
        System.out.println(this);
    }

    @Override
    public int stepCost() {
        return Direction.cost(direction);
    }

    @Override
    public String toString() {
        return direction.name();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (obj instanceof Move) {
            Move another = (Move) obj;
            //两个Node对象的状态相同，则认为是相同的
            return this.direction.equals(another.direction);
        }
        return false;
    }
}
