package stud.problem.pathfinding;

import java.util.EnumMap;
import java.util.List;

/**
 * 
 * 地图中可以移动的8个方向，及其箭头符号
 */
public enum Direction {
    N('↑'),  //北
    NE('↗'), //东北
    E('→'),  //东
    SE('↘'), //东南
    S('↓'),  //南
    SW('↙'), //西南
    W('←'),  //西
    NW('↖'); //西北

    private final char symbol;

    /**
     * 构造函数
     * @param symbol 枚举项的代表符号--箭头
     */
    Direction(char symbol){
        this.symbol = symbol;
    }

    public char symbol(){
        return symbol;
    }

    public static final int SCALE = 10;       //单元格的边长
    public static final double ROOT2 = 1.4;   //2的平方根

    /**
     * 移动方向的两种不同情况（4个方向，8个方向）。
     */
    public static final List<Direction> FOUR_DIRECTIONS = List.of(Direction.N, Direction.E, Direction.S, Direction.W);
    public static final List<Direction> EIGHT_DIRECTIONS = List.of(Direction.values());
    
    /**
     * 不同方向的耗散值
     */
    public static int cost(Direction dir){
        return FOUR_DIRECTIONS.contains(dir) ? SCALE : (int) (SCALE * ROOT2);
    }

    //各个方向移动的坐标位移量
    private static final EnumMap<Direction, int[]> DIRECTION_OFFSET = new EnumMap<>(Direction.class);
    static{
        //列号（或横坐标）增加量；行号（或纵坐标）增加量
        DIRECTION_OFFSET.put(N, new int[]{0, -1});
        DIRECTION_OFFSET.put(NE, new int[]{1, -1});
        DIRECTION_OFFSET.put(E, new int[]{1, 0});
        DIRECTION_OFFSET.put(SE, new int[]{1, 1});
        DIRECTION_OFFSET.put(S, new int[]{0, 1});
        DIRECTION_OFFSET.put(SW, new int[]{-1, 1});
        DIRECTION_OFFSET.put(W, new int[]{-1, 0});
        DIRECTION_OFFSET.put(NW, new int[]{-1, -1});
    }
    
    public static int[] offset(Direction dir){
        return DIRECTION_OFFSET.get(dir);
    }
}
