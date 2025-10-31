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
    // ����հ׸��λ�ã������ظ�����������Ч��
    private int[] blankPosition;

    public PuzzleBoard(int[][] grid) {
        this.grid = grid;
        this.size = grid.length;
        this.blankPosition = findBlank();
    }

    /**
     * Ѱ�ҿհ׸�(0)��λ��
     * @return һ������[row, col]������
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
        return new int[]{-1, -1}; // �����ϲ��ᷢ��
    }

    /**
     * ��ӡ���̵�����
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
     * ִ��һ������������һ���µ�״̬ (ʹ�� Move �� Direction)
     */
    @Override
    public State next(Action action) {
        // �Ƚ�������ת��
        Move move = (Move) action;
        Direction direction = move.getDirection();

        // ����һ���µĶ�ά���飬���ڴ洢��״̬
        int[][] newGrid = new int[size][];
        for (int i = 0; i < size; i++) {
            newGrid[i] = Arrays.copyOf(grid[i], size);
        }

        int blankRow = blankPosition[0];
        int blankCol = blankPosition[1];

        // ʹ�� Direction.offset ������Ŀ����Ƭ��λ��
        int[] offset = Direction.offset(direction);
        int targetRow = blankRow + offset[1]; // ע�� offset ���������
        int targetCol = blankCol + offset[0];

        // ������Ƭ�Ϳհ׸�
        newGrid[blankRow][blankCol] = grid[targetRow][targetCol];
        newGrid[targetRow][targetCol] = 0;

        return new PuzzleBoard(newGrid);
    }

    /**
     * ���ɵ�ǰ״̬�����п��ܵĶ���
     */
    @Override
    public Iterable<? extends Action> actions() {
        ArrayList<Action> possibleActions = new ArrayList<>();
        int row = blankPosition[0];
        int col = blankPosition[1];

        // ����Ƿ��������
        if (row > 0) {
            possibleActions.add(new Move(Direction.N));
        }
        // ����Ƿ��������
        if (row < size - 1) {
            possibleActions.add(new Move(Direction.S));
        }
        // ����Ƿ��������
        if (col > 0) {
            possibleActions.add(new Move(Direction.W));
        }
        // ����Ƿ��������
        if (col < size - 1) {
            possibleActions.add(new Move(Direction.E));
        }
        return possibleActions;
    }

    /**
     * �ж���������״̬�Ƿ���ͬ
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PuzzleBoard that = (PuzzleBoard) o;
        return Arrays.deepEquals(grid, that.grid);
    }

    /**
     * Ϊ����״̬���ɹ�ϣ�룬������HashSet��HashMap�п��ٲ���
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
