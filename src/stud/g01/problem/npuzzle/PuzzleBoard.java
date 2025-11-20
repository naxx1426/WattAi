package stud.g01.problem.npuzzle;

import core.problem.Action;
import core.problem.State;
import stud.problem.pathfinding.Direction;
import stud.problem.pathfinding.Move;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public int indexOf(int tile) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == tile) {
                    return i * size + j;
                }
            }
        }
        return -1; // 如果没找到（比如空白格）
    }

    // ==========================================
    // 新增功能区：文件输出支持
    // ==========================================

    /**
     * 将当前棋盘转换为单行空格分隔的字符串
     * 例如: "5 0 8 4 2 1 7 3 6"
     */
    public String toLinearString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sb.append(grid[i][j]);
                if (i < size - 1 || j < size - 1) {
                    sb.append(" ");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 将解题路径写入指定文件
     * 使用追加模式（Append Mode），所以可以连续写入多个Puzzle的解。
     * * 格式：
     * N
     * grid_state_line
     * ...
     * finish
     *
     * @param path 搜索算法返回的路径列表
     * @param filename 输出文件的路径（例如 "visualization_data.txt"）
     */
    public static void saveSolutionToFile(List<State> path, String filename) {
        if (path == null || path.isEmpty()) {
            return;
        }

        // 使用 try-with-resources 自动关闭文件流
        // FileWriter(filename, true) 中的 true 表示追加写入
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {

            // 1. 写入阶数 (Size)
            if (path.get(0) instanceof PuzzleBoard) {
                int size = ((PuzzleBoard) path.get(0)).getSize();
                writer.write(String.valueOf(size));
                writer.newLine(); // 换行
            }

            // 2. 写入每一步的状态
            for (State state : path) {
                if (state instanceof PuzzleBoard) {
                    writer.write(((PuzzleBoard) state).toLinearString());
                    writer.newLine(); // 换行
                }
            }

            // 3. 写入完成标记
            writer.write("finish");
            writer.newLine(); // 换行

        } catch (IOException e) {
            System.err.println("写入文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}