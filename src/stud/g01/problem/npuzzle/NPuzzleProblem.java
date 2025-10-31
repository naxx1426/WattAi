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
    public NPuzzleProblem(State initialState, State goal) {
        super(initialState, goal);
    }

    public NPuzzleProblem(State initialState, State goal, int size) {
        super(initialState, goal, size);
    }

    /**
     * 判断可不可解
     * @return 可解return true，不可解return false
     */
    @Override
    public boolean solvable() {
        State initialState = getInitialState();
        PuzzleBoard board = (PuzzleBoard) initialState;
        int size = board.getSize();
        int[][] grid = board.getGrid();

        int inversions = getInversionCount(grid);

        // 由于目标状态的华容道奇偶性为偶，
        // 所以如果一个华容道, 其奇偶性如果是偶，那么就可以移动到目标状态, 即为有解。
        // 如果一个华容道状态的奇偶性是奇，那么它一定不能移动到一个偶的华容道状态(目标状态), 即是无解
        // 数字华容道，必然有解，只存在于如下3个细分情形
        // 若格子列数为奇数，则逆序数必须为偶数；
        // 若格子列数为偶数，且逆序数为偶数，则当前空格所在行数与初始空格所在行数的差为偶数；
        // 若格子列数为偶数，且逆序数为奇数，则当前空格所在行数与初始空格所在行数的差为奇数。
        if (size % 2 == 1) {
            // 3. N 为奇数 (如 3x3)
            // 逆序数必须为偶数
            return (inversions % 2 == 0);
        } else {
            int blankRowFromBottom = findBlankRowFromBottom(grid);
            /*
            !!!!!!!!!望有缘人测试一下这个逻辑正不正确，还是说要把==改成!=？!!!!!!!!!!!!
             */
            return (inversions % 2 == blankRowFromBottom % 2);
        }
    }

    /**
     * 计算逆序数函数
     * @return 一个int类型的华容道的逆序数
     */
    private int getInversionCount(int[][] grid) {
        int size = grid.length;

        // 扁平化数字格数组的值，平摊到一维上
        int[] flatArray = new int[size * size];
        int k = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                flatArray[k++] = grid[i][j];
            }
        }

        int inversionCount = 0;
        for (int i = 0; i < flatArray.length - 1; i++) {
            for (int j = i + 1; j < flatArray.length; j++) {
                // 跳过空白格，不计算空白格的逆序数
                if (flatArray[i] != 0 && flatArray[j] != 0 && flatArray[i] > flatArray[j]) {
                    inversionCount++;
                }
            }
        }
        return inversionCount;
    }

    /**
     * 寻找空白格所在的行
     * @return 一个int类型的目标状态空格所在行数与初始空格所在行数的行差
     */
    private int findBlankRowFromBottom(int[][] grid) {
        int size = grid.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == 0) {
                    return size - i;
                }
            }
        }
        return -1;
    }

    /**
     * 覆盖 Action 的 stepCost() 行为。
     * 无论 Move.stepCost() 返回什么，对于 NPuzzleProblem，成本永远是 1。
     */
    @Override
    public int stepCost(State state, Action action) {
        return 1;
    }

    /**
     * 检查一个动作在当前状态下是否可以执行
     */
    @Override
    public boolean applicable(State state, Action action) {
        PuzzleBoard board = (PuzzleBoard) state;
        Move move = (Move) action;
        Direction direction = move.getDirection();

        int[] blankPos = board.findBlank();
        int blankRow = blankPos[0];
        int blankCol = blankPos[1];
        int size = board.getSize();

        switch (direction) {
            case N: return blankRow > 0;
            case S: return blankRow < size - 1;
            case W: return blankCol > 0;
            case E: return blankCol < size - 1;
            default: // 对于 NE, SW 等不适用于 N-Puzzle 的方向，返回 false(尽管不太可能出现这种情况
                // ，如果在我的代码没有修改的情况下)
                return false;
        }
    }


    /**
     * ！！！！！！！！！！！！！！施工中by雷祥宁！！！！！！！！！！！！！
     * 按照指定的格式，可视化地展示从初始状态到目标状态的解决方案路径。
     * @param path 一个包含解决方案路径上所有节点的双端队列 (Deque)。
     */
    @Override
    public void showSolution(Deque<Node> path) {
        if (path == null || path.isEmpty()) {
            System.out.println("\n找不到解决办法");
            return;
        }

        Iterator<Node> iterator = path.iterator();

        Node startNode = iterator.next();
        startNode.getState().draw();

        int step = 1;
        while (iterator.hasNext()) {
            Node currentNode = iterator.next();
            Action action = currentNode.getAction();

            // 打印箭头
            if (action != null) {
                System.out.println("   ↓");
                System.out.printf("   ↓-(#, %s)\n", action.toString());
                System.out.println("   ↓");
            }

            // 打印动作执行后的棋盘状态
            currentNode.getState().draw();
        }

        System.out.println("启发函数: " + "占位先" + "，解路径长度：" + (path.size() - 1)
                + "，执行了" + "执行时间" + "，共生成了" + "占位" + "个结点，扩展了" + "占位" + "个结点");
    }
}
