package stud.g01.problem.npuzzle;

import core.problem.Action;
import core.problem.State;
import stud.problem.pathfinding.Direction;
import stud.problem.pathfinding.Move;

import java.util.ArrayList;
import java.util.Arrays;

public class PuzzleBoard extends State {

    private final int[][] grid;
    private final int size;
    // 缓存空白格的位置，避免重复搜索，提升效率
    private int[] blankPosition;

    public PuzzleBoard(int[][] grid) {
        this.grid = grid;
        this.size = grid.length;
        this.blankPosition = findBlank();
    }

    /**
     * 寻找空白格(0)的位置
     * @return 一个包含[row, col]的数组
     */
    public int[] findBlank() {
        if (this.blankPosition != null) {
            return this.blankPosition;
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == 0) {
                    this.blankPosition = new int[]{i, j};
                    return this.blankPosition;
                }
            }
        }
        return new int[]{-1, -1}; // 理论上不会发生
    }

    /**
     * 打印棋盘的内容
     */
    @Override
    public void draw() {
        int size = grid.length;
        StringBuilder border = new StringBuilder("+");
        border.append("---+".repeat(size));
        for (int i = 0; i < size; i++) {
            System.out.println(border);
            System.out.print("|");
            for (int j = 0; j < size; j++) {
                int val = grid[i][j];
                String cell = (val == 0) ? "#" : String.valueOf(val);
                System.out.printf(" %-3s |", cell);
            }
            System.out.println();
        }
        System.out.println(border);
    }

    /**
     * 执行一个动作并返回一个新的状态 (使用 Move 和 Direction)
     */
    @Override
    public State next(Action action) {
        // 先进行类型转换
        Move move = (Move) action;
        Direction direction = move.getDirection();

        // 创建一个新的二维数组，用于存储新状态
        int[][] newGrid = new int[size][];
        for (int i = 0; i < size; i++) {
            newGrid[i] = Arrays.copyOf(grid[i], size);
        }

        int blankRow = blankPosition[0];
        int blankCol = blankPosition[1];

        // 使用 Direction.offset 来计算目标瓦片的位置
        int[] offset = Direction.offset(direction);
        int targetRow = blankRow + offset[1]; // 注意 offset 数组的索引
        int targetCol = blankCol + offset[0];

        // 交换瓦片和空白格
        newGrid[blankRow][blankCol] = grid[targetRow][targetCol];
        newGrid[targetRow][targetCol] = 0;

        return new PuzzleBoard(newGrid);
    }

    /**
     * 生成当前状态下所有可能的动作
     */
    @Override
    public Iterable<? extends Action> actions() {
        ArrayList<Action> possibleActions = new ArrayList<>();
        int row = blankPosition[0];
        int col = blankPosition[1];

        // 检查是否可以上移
        if (row > 0) {
            possibleActions.add(new Move(Direction.N));
        }
        // 检查是否可以下移
        if (row < size - 1) {
            possibleActions.add(new Move(Direction.S));
        }
        // 检查是否可以左移
        if (col > 0) {
            possibleActions.add(new Move(Direction.W));
        }
        // 检查是否可以右移
        if (col < size - 1) {
            possibleActions.add(new Move(Direction.E));
        }
        return possibleActions;
    }

    /**
     * 判断两个棋盘状态是否相同
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PuzzleBoard that = (PuzzleBoard) o;
        return Arrays.deepEquals(grid, that.grid);
    }

    /**
     * 为棋盘状态生成哈希码，用于在HashSet或HashMap中快速查找
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getSize() {
        return size;
    }
}
