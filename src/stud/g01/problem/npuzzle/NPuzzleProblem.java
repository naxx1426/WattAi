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
     * �жϿɲ��ɽ�
     * @return �ɽ�return true�����ɽ�return false
     */
    @Override
    public boolean solvable() {
        State initialState = getInitialState();
        PuzzleBoard board = (PuzzleBoard) initialState;
        int size = board.getSize();
        int[][] grid = board.getGrid();

        int inversions = getInversionCount(grid);

        // ����Ŀ��״̬�Ļ��ݵ���ż��Ϊż��
        // �������һ�����ݵ�, ����ż�������ż����ô�Ϳ����ƶ���Ŀ��״̬, ��Ϊ�н⡣
        // ���һ�����ݵ�״̬����ż�����棬��ô��һ�������ƶ���һ��ż�Ļ��ݵ�״̬(Ŀ��״̬), �����޽�
        // ���ֻ��ݵ�����Ȼ�н⣬ֻ����������3��ϸ������
        // ����������Ϊ������������������Ϊż����
        // ����������Ϊż������������Ϊż������ǰ�ո������������ʼ�ո����������Ĳ�Ϊż����
        // ����������Ϊż������������Ϊ��������ǰ�ո������������ʼ�ո����������Ĳ�Ϊ������
        if (size % 2 == 1) {
            // 3. N Ϊ���� (�� 3x3)
            // ����������Ϊż��
            return (inversions % 2 == 0);
        } else {
            int blankRowFromBottom = findBlankRowFromBottom(grid);
            /*
            !!!!!!!!!����Ե�˲���һ������߼�������ȷ������˵Ҫ��==�ĳ�!=��!!!!!!!!!!!!
             */
            return (inversions % 2 == blankRowFromBottom % 2);
        }
    }

    /**
     * ��������������
     * @return һ��int���͵Ļ��ݵ���������
     */
    private int getInversionCount(int[][] grid) {
        int size = grid.length;

        // ��ƽ�����ָ������ֵ��ƽ̯��һά��
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
                // �����հ׸񣬲�����հ׸��������
                if (flatArray[i] != 0 && flatArray[j] != 0 && flatArray[i] > flatArray[j]) {
                    inversionCount++;
                }
            }
        }
        return inversionCount;
    }

    /**
     * Ѱ�ҿհ׸����ڵ���
     * @return һ��int���͵�Ŀ��״̬�ո������������ʼ�ո������������в�
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
     * ���� Action �� stepCost() ��Ϊ��
     * ���� Move.stepCost() ����ʲô������ NPuzzleProblem���ɱ���Զ�� 1��
     */
    @Override
    public int stepCost(State state, Action action) {
        return 1;
    }

    /**
     * ���һ�������ڵ�ǰ״̬���Ƿ����ִ��
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
            default: // ���� NE, SW �Ȳ������� N-Puzzle �ķ��򣬷��� false(���ܲ�̫���ܳ����������
                // ��������ҵĴ���û���޸ĵ������)
                return false;
        }
    }


    /**
     * ����������������������������ʩ����by��������������������������������
     * ����ָ���ĸ�ʽ�����ӻ���չʾ�ӳ�ʼ״̬��Ŀ��״̬�Ľ������·����
     * @param path һ�������������·�������нڵ��˫�˶��� (Deque)��
     */
    @Override
    public void showSolution(Deque<Node> path) {
        if (path == null || path.isEmpty()) {
            System.out.println("\n�Ҳ�������취");
            return;
        }

        Iterator<Node> iterator = path.iterator();

        Node startNode = iterator.next();
        startNode.getState().draw();

        int step = 1;
        while (iterator.hasNext()) {
            Node currentNode = iterator.next();
            Action action = currentNode.getAction();

            // ��ӡ��ͷ
            if (action != null) {
                System.out.println("   ��");
                System.out.printf("   ��-(#, %s)\n", action.toString());
                System.out.println("   ��");
            }

            // ��ӡ����ִ�к������״̬
            currentNode.getState().draw();
        }

        System.out.println("��������: " + "ռλ��" + "����·�����ȣ�" + (path.size() - 1)
                + "��ִ����" + "ִ��ʱ��" + "����������" + "ռλ" + "����㣬��չ��" + "ռλ" + "�����");
    }
}
