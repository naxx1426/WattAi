package stud.g01.solver.algorithm.heuristic;

import core.problem.State;
import core.solver.algorithm.heuristic.Predictor;
import stud.g01.problem.npuzzle.PuzzleBoard;

import java.util.HashMap;
import java.util.Map;


public class ManhattanDistancePredictor implements Predictor {

    // 缓存，用于存储不同目标状态 (goal state) 的滑块位置查找表
    private final Map<State, Map<Integer, int[]>> goalCache;

    /**
     * 构造实例
     */
    public ManhattanDistancePredictor() {
        this.goalCache = new HashMap<>();
    }

    /**
     * 获取或创建指定目标状态的滑块位置查找表
     * 如果该目标状态的查找表已被计算过，则从缓存中直接返回，避免重复计算。
     * @param goal 目标状态
     * @return 包含所有滑块及其目标位置的查找表
     */
    private Map<Integer, int[]> getGoalPositions(State goal) {
        // 检查缓存中是否已存在该目标状态的查找表
        if (goalCache.containsKey(goal)) {
            return goalCache.get(goal);
        }

        // 如果缓存中没有，则为该目标状态创建一个新的查找表
        Map<Integer, int[]> positions = new HashMap<>();
        PuzzleBoard goalBoard = (PuzzleBoard) goal;
        int size = goalBoard.getSize();
        int[][] goalGrid = goalBoard.getGrid();

        // 遍历目标棋盘，记录每个滑块 (非 0) 的坐标
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int value = goalGrid[i][j];
                if (value != 0) {
                    positions.put(value, new int[]{i, j});
                }
            }
        }

        // 将新创建的查找表存入缓存，以备后续使用
        goalCache.put(goal, positions);
        return positions;
    }

    /**
     * 计算给定状态到目标状态的曼哈顿距离总和
     * @param state 需要被评估的当前状态
     * @param goal  目标状态
     * @return 计算出的曼哈顿距离总和
     */
    @Override
    public int heuristics(State state, State goal) {
        // 获取目标位置查找表 (此方法会自动处理缓存)
        Map<Integer, int[]> goalPositions = getGoalPositions(goal);

        PuzzleBoard currentBoard = (PuzzleBoard) state;
        int[][] currentGrid = currentBoard.getGrid();
        int size = currentBoard.getSize();
        int totalDistance = 0;

        // 遍历当前棋盘，累加每个滑块的曼哈顿距离
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int value = currentGrid[i][j];
                // 空白格 (0) 不计算距离
                if (value != 0) {
                    int[] targetPos = goalPositions.get(value);
                    // 健壮性检查，防止因输入问题导致程序崩溃
                    if (targetPos == null) continue;

                    int targetRow = targetPos[0];
                    int targetCol = targetPos[1];
                    // 累加当前滑块的曼哈顿距离
                    totalDistance += Math.abs(i - targetRow) + Math.abs(j - targetCol);
                }
            }
        }
        return totalDistance;
    }
}