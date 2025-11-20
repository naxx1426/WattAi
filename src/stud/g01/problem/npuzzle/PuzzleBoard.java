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

    /**
     * 构造一个新的 PuzzleBoard 状态实例
     * @param grid 用于初始化状态的二维数组
     */
    public PuzzleBoard(int[][] grid) {
        this.grid = grid;
        this.size = grid.length;
        // 缓存空白格位置，提升后续 findBlank(), next(), actions() 的效率
        this.blankPosition = findBlank();
    }

    /**
     * 寻找空白格(0)的位置
     * 此方法使用缓存 (blankPosition) 来避免重复遍历网格。
     * @return 一个包含 [row, col] 索引的整型数组
     */
    public int[] findBlank() {
        // 如果已有缓存，直接返回
        if (this.blankPosition != null) {
            return this.blankPosition;
        }
        // 如果没有缓存，则遍历网格寻找
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == 0) {
                    // 找到空白格，缓存其位置
                    this.blankPosition = new int[]{i, j};
                    return this.blankPosition;
                }
            }
        }
        // 理论上不应发生，除非棋盘无空白格
        return new int[]{-1, -1};
    }

    /**
     * 在控制台打印当前棋盘状态的可视化表示
     * 空白格将用 '#' 表示。
     */
    @Override
    public void draw() {
        int size = grid.length;
        StringBuilder border = new StringBuilder("+");
        border.append("-----+".repeat(size)); // (保留用户自定义的边框宽度)
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
     * 根据给定的动作 (Action) 生成并返回一个新的状态 (State)
     * 此方法通过交换空白格和相邻瓦片来实现，并确保状态的不可变性。
     * @param action 要执行的动作 (必须是 Move 类型)
     * @return 执行动作后产生的新 PuzzleBoard 状态
     */
    @Override
    public State next(Action action) {
        // 将 Action 转换为 Move 以获取方向
        Move move = (Move) action;
        Direction direction = move.getDirection();

        // 创建 grid 的深度拷贝，以保证状态不可变
        int[][] newGrid = new int[size][];
        for (int i = 0; i < size; i++) {
            newGrid[i] = Arrays.copyOf(grid[i], size);
        }

        // 获取缓存的空白格位置
        int blankRow = blankPosition[0];
        int blankCol = blankPosition[1];

        // 计算目标瓦片 (要与空白格交换的瓦片) 的坐标
        int[] offset = Direction.offset(direction);
        int targetRow = blankRow + offset[1]; // Y 轴偏移
        int targetCol = blankCol + offset[0]; // X 轴偏移

        // 执行交换：将瓦片移到空白格处
        newGrid[blankRow][blankCol] = grid[targetRow][targetCol];
        // 将原瓦片位置设为空白格
        newGrid[targetRow][targetCol] = 0;

        // 返回包含新网格的新状态
        return new PuzzleBoard(newGrid);
    }

    /**
     * 生成当前状态下所有可移动方向的Move
     * 基于空白格的当前位置判断 N, S, E, W 四个方向是否可以移动
     * @return 一个包含所有可移动方向的Move
     */
    @Override
    public Iterable<? extends Action> actions() {
        ArrayList<Action> possibleActions = new ArrayList<>();
        // 获取缓存的空白格位置
        int row = blankPosition[0];
        int col = blankPosition[1];

        // 检查上移 (N) 是否越界
        if (row > 0) {
            possibleActions.add(new Move(Direction.N));
        }
        // 检查下移 (S) 是否越界
        if (row < size - 1) {
            possibleActions.add(new Move(Direction.S));
        }
        // 检查左移 (W) 是否越界
        if (col > 0) {
            possibleActions.add(new Move(Direction.W));
        }
        // 检查右移 (E) 是否越界
        if (col < size - 1) {
            possibleActions.add(new Move(Direction.E));
        }
        return possibleActions;
    }

    /**
     * 比较此 PuzzleBoard 状态与另一个对象是否相同
     * 两个状态当且仅当它们的网格 (grid) 内容完全相同时才被视为相等
     * @param o 要比较的另一个对象
     * @return 如果状态相同则返回 true, 否则返回 false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PuzzleBoard that = (PuzzleBoard) o;
        return Arrays.deepEquals(grid, that.grid);
    }

    /**
     * 为当前棋盘状态生成哈希码
     * 基于网格 (grid) 的内容生成，用于在 HashSet 或 HashMap 中进行高效存储和查找。
     * @return 基于 grid 内容计算出的哈希码 (整数)
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

    /**
     * 快速查找某个数字在当前棋盘中的线性下标（0 ~ size?-1）
     * @param number 要查找的数字（通常为 1 ~ size?-1）
     * @return 线性下标；若找不到返回 -1
     */
    public int indexOf(int number) {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (grid[r][c] == number)
                    return r * size + c;   // 行优先
            }
        }
        return -1; // 未找到（不应发生）
    }

}