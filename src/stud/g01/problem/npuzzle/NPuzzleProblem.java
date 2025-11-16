package stud.g01.problem.npuzzle;

import core.problem.Action;
import core.problem.Problem;
import core.problem.State;
import core.solver.queue.Node;
import stud.problem.pathfinding.Direction;
import stud.problem.pathfinding.Move;

import java.util.Deque;
import java.util.Iterator;

public class NPuzzleProblem extends Problem {

    /**
     * 构造 NPuzzleProblem 实例
     * @param initialState 初始状态
     * @param goal 目标状态
     */
    public NPuzzleProblem(State initialState, State goal) {
        super(initialState, goal);
    }

    /**
     * 构造 NPuzzleProblem 实例 (包含棋盘大小)
     * @param initialState 初始状态
     * @param goal 目标状态
     * @param size 棋盘大小
     */
    public NPuzzleProblem(State initialState, State goal, int size) {
        super(initialState, goal, size);
    }

    /**
     * 检查当前的 N-Puzzle 问题是否可解
     * 基于逆序数 (inversions) 和空白格位置的奇偶性来判断。
     * @return 如果可解则返回 true, 否则返回 false
     */
    @Override
    public boolean solvable() {
        State initialState = getInitialState();
        PuzzleBoard board = (PuzzleBoard) initialState;
        int size = board.getSize();
        int[][] grid = board.getGrid();

        // 获取棋盘状态的逆序数
        int inversions = getInversionCount(grid);

        // 由于目标状态的华容道奇偶性为偶，
        // 所以如果一个华容道, 其奇偶性如果是偶，那么就可以移动到目标状态, 即为有解。
        // 如果一个华容道状态的奇偶性是奇，那么它一定不能移动到一个偶的华容道状态(目标状态), 即是无解
        // 数字华容道，必然有解，只存在于如下3个细分情形
        // 若格子列数为奇数，则逆序数必须为偶数；
        // 若格子列数为偶数，且逆序数为偶数，则当前空格所在行数与初始空格所在行数的差为偶数；
        // 若格子列数为偶数，且逆序数为奇数，则当前空格所在行数与初始空格所在行数的差为奇数。
        if (size % 2 == 1) {
            return (inversions % 2 == 0);
        } else {
            // 3. N 为奇数 (如 3x3)
            // 逆序数必须为偶数
            int blankRowFromBottom = findBlankRowFromBottom(grid);
            return (inversions % 2 != blankRowFromBottom % 2);
        }
    }

    /**
     * 计算给定棋盘状态的逆序数
     * 此方法将二维数组扁平化, 然后统计所有非空白滑块的逆序对数量。
     * @param grid 当前的棋盘状态 (二维数组)
     * @return 棋盘状态的逆序数
     */
    private int getInversionCount(int[][] grid) {
        int size = grid.length;

        // 将二维棋盘扁平化为一维数组，以便计算
        int[] flatArray = new int[size * size];
        int k = 0;
        for (int[] ints : grid) {
            for (int j = 0; j < size; j++) {
                flatArray[k++] = ints[j];
            }
        }

        int inversionCount = 0;
        // 遍历数组，计算逆序对的总数
        for (int i = 0; i < flatArray.length - 1; i++) {
            for (int j = i + 1; j < flatArray.length; j++) {
                // 空白格 (0) 不参与逆序数计算
                if (flatArray[i] != 0 && flatArray[j] != 0 && flatArray[i] > flatArray[j]) {
                    inversionCount++;
                }
            }
        }
        return inversionCount;
    }

    /**
     * 寻找空白格(0)所在的行 (从底部开始计数)
     * @param grid 当前的棋盘状态 (二维数组)
     * @return 空白格所在的行数 (从 1 开始, 底部为第 1 行)
     */
    private int findBlankRowFromBottom(int[][] grid) {
        int size = grid.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == 0) {
                    // 例如在 4x4 棋盘中, 物理索引第 0 行将返回 4, 第 3 行将返回 1
                    return size - i;
                }
            }
        }
        // 理论上不应发生
        return -1;
    }

    /**
     * 定义 N-Puzzle 问题中每一步的成本
     * 对于标准的 N-Puzzle 问题，每次移动的成本固定为 1。
     * @param state  执行动作前的状态 (未使用)
     * @param action 被执行的动作 (未使用)
     * @return 动作的成本 (固定为 1)
     */
    @Override
    public int stepCost(State state, Action action) {
        return 1;
    }

    /**
     * 检查一个动作在当前状态下是否可以执行
     * 通过检查移动空白格是否会超出棋盘边界来判断。
     * @param state  当前的棋盘状态
     * @param action 尝试执行的动作
     * @return 如果动作合法 (不越界) 则返回 true, 否则返回 false
     */
    @Override
    public boolean applicable(State state, Action action) {
        PuzzleBoard board = (PuzzleBoard) state;
        Move move = (Move) action;
        Direction direction = move.getDirection();

        // 获取空白格的当前位置
        int[] blankPos = board.findBlank();
        int blankRow = blankPos[0];
        int blankCol = blankPos[1];
        int size = board.getSize();

        // 根据移动方向判断是否会越界
        return switch (direction) {
            case N -> blankRow > 0;
            case S -> blankRow < size - 1;
            case W -> blankCol > 0;
            case E -> blankCol < size - 1;
            // N-Puzzle 不支持对角线等其他移动方向
            default -> false;
        };
    }


    /**
     * 可视化地展示从初始状态到目标状态的解决方案路径
     * @param path 一个包含从根节点到目标节点的完整解决方案路径的双端队列 (Deque)
     */
    @Override
    public void showSolution(Deque<Node> path) {
        if (path == null || path.isEmpty()) {
            System.out.println("\n找不到解决办法");
            return;
        }

        // 使用迭代器来遍历路径中的每一个节点
        Iterator<Node> iterator = path.iterator();

        // 获取路径的根节点
        Node startNode = iterator.next();
        startNode.getState().draw();

        // 遍历并打印路径中的节点
        while (iterator.hasNext()) {
            Node currentNode = iterator.next();
            // 获取从父节点到当前节点的动作
            Action action = currentNode.getAction();

            // 打印动作指示
            if (action != null) {
                System.out.println("   ↓");
                System.out.printf("   ↓-(#, %s)\n", action);
                System.out.println("   ↓");
            }

            // 打印执行动作后的棋盘状态
            currentNode.getState().draw();
        }
    }
}