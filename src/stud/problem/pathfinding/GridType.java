package stud.problem.pathfinding;

public enum GridType {
    EMPTY('0', 1),  // 空地
    GRASS('*', 5),  // 草地，通过的代价高，普通代价的5倍
    //MUDDY('~', 7),  // 泥地，通过代价为7倍
    WALL('+', Integer.MAX_VALUE);   // 石墙，无法通过

    private final char symbol; 
    private final int magnify;

    GridType(char symbol, int magnify){
        this.symbol = symbol;
        this.magnify = magnify;
    }

    public char symbol(){
        return symbol;
    }
    public int magnify(){return magnify;}

    public GridType valueOfDigit(char digit){
        return values()[digit - '0'];
    }

    @Override
    public String toString() {
        return symbol + "";
    }
}
