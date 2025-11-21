package stud.g01.solver.algorithm.heuristic;

import core.problem.State;
import core.solver.algorithm.heuristic.Predictor;
import stud.g01.problem.npuzzle.PuzzleBoard;
import core.problem.Action;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DisjointPatternDatabasePredictor implements Predictor {

    // 静态数据库缓存，避免重复构建
    private static final Map<Integer, DisjointPatternDatabase> DATABASE_CACHE = new HashMap<>();

    @Override
    public int heuristics(State state, State goal) {
        PuzzleBoard current = (PuzzleBoard) state;
        PuzzleBoard goalBoard = (PuzzleBoard) goal;
        int size = current.getSize();

        validatePuzzleSize(size);

        // 延迟加载数据库
        DisjointPatternDatabase database = DATABASE_CACHE.computeIfAbsent(size,
                s -> new DisjointPatternDatabase(s, createOptimalGrouping(s)));

        return database.calculateHeuristic(current, goalBoard);
    }

    /**
     * 验证拼图规模支持
     */
    private void validatePuzzleSize(int size) {
        if (size != 3 && size != 4) {
            throw new IllegalArgumentException("仅支持3x3(8-puzzle)和4x4(15-puzzle)规模的拼图");
        }
    }

    /**
     * 创建最优分组方案（基于文献推荐的分组策略）
     */
    private List<List<Integer>> createOptimalGrouping(int size) {
        List<List<Integer>> groups = new ArrayList<>();

        if (size == 3) {
            // 8-puzzle: 4-4分组（上半部分和下半部分）
            groups.add(Arrays.asList(1, 2, 3, 4));  // 上半部分
            groups.add(Arrays.asList(5, 6, 7, 8));  // 下半部分
        } else {
            // 15-puzzle: 6-6-3分组（文献最优分组）
            groups.add(Arrays.asList(1, 2, 3, 4));    // 左上区域
            groups.add(Arrays.asList(5, 6, 7, 8)); // 右上区域
            groups.add(Arrays.asList(9, 10, 11, 12));
            groups.add(Arrays.asList(13, 14, 15)); // 底部区域
        }

        return groups;
    }

    /* ===================== 分离模式数据库内部类 ====================== */
    private static class DisjointPatternDatabase {
        private final int size;
        private final List<List<Integer>> disjointGroups;
        private final List<Map<String, Integer>> patternDatabases;
        private final String cachePrefix;

        DisjointPatternDatabase(int size, List<List<Integer>> groups) {
            this.size = size;
            this.disjointGroups = groups;
            this.patternDatabases = new ArrayList<>();
            this.cachePrefix = "disjoint-pdb-" + size + "x" + size + "-";

            initializeDatabases();
        }

        /**
         * 初始化所有模式数据库
         */
        private void initializeDatabases() {
            PuzzleBoard goal = createGoalBoard(size);

            for (int i = 0; i < disjointGroups.size(); i++) {
                List<Integer> group = disjointGroups.get(i);
                Map<String, Integer> db = loadOrBuildDatabase(goal, group, i);
                patternDatabases.add(db);

                System.out.printf("模式数据库 %d: 分组 %s, 状态数 %d%n",
                        i + 1, group, db.size());
            }
        }

        /**
         * 计算启发式值（核心算法）- 可加性原理
         */
        int calculateHeuristic(PuzzleBoard current, PuzzleBoard goal) {
            int totalHeuristic = 0;

            // 对每个不相交模式分别计算并累加（可加性定理）
            for (int i = 0; i < disjointGroups.size(); i++) {
                List<Integer> group = disjointGroups.get(i);
                Map<String, Integer> db = patternDatabases.get(i);

                int patternCost = getPatternCost(current, group, db);
                totalHeuristic += patternCost;
            }

            return totalHeuristic;
        }

        /**
         * 获取单个模式的代价
         */
        private int getPatternCost(PuzzleBoard board, List<Integer> group,
                                   Map<String, Integer> database) {
            String stateCode = encodePatternState(board, group);
            Integer cost = database.get(stateCode);

            // 如果数据库中不存在，使用分组曼哈顿距离作为回退
            return cost != null ? cost : calculateGroupManhattan(board, group);
        }

        /* ===================== 数据库构建算法 ===================== */

        /**
         * 加载或构建数据库
         */
        private Map<String, Integer> loadOrBuildDatabase(PuzzleBoard goal,
                                                         List<Integer> group, int groupIndex) {
            String cacheFile = cachePrefix + "group" + groupIndex + ".cache";

            // 尝试从缓存加载
            Map<String, Integer> cachedDb = loadDatabaseFromCache(cacheFile);
            if (cachedDb != null) {
                System.out.println("加载缓存数据库: " + cacheFile);
                return cachedDb;
            }

            // 重新构建数据库（BFS算法）
            System.out.println("构建分离模式数据库: " + group);
            long startTime = System.nanoTime();

            Map<String, Integer> db = buildPatternDatabase(goal, group);
            saveDatabaseToCache(db, cacheFile);

            long endTime = System.nanoTime();
            System.out.printf("数据库构建完成: %d states, 耗时: %.3f秒\n",
                    db.size(), (endTime - startTime) / 1e9);

            return db;
        }

        /**
         * 构建单个模式数据库（BFS算法）
         * 关键：只关心模式内瓷砖的移动，忽略其他瓷砖
         */
        private Map<String, Integer> buildPatternDatabase(PuzzleBoard goal, List<Integer> group) {
            Map<String, Integer> database = new HashMap<>();
            Queue<Node> queue = new ArrayDeque<>();
            Set<String> visited = new HashSet<>();

            // 从目标状态开始BFS
            String goalCode = encodePatternState(goal, group);
            database.put(goalCode, 0);
            queue.offer(new Node(goal, 0));
            visited.add(goalCode);

            int statesExplored = 0;

            while (!queue.isEmpty()) {
                Node currentNode = queue.poll();
                PuzzleBoard current = currentNode.board;
                int currentCost = currentNode.cost;

                statesExplored++;

                // 扩展所有可能的移动
                for (Object actionObj : current.actions()) {
                    Action action = (Action) actionObj;
                    PuzzleBoard next = (PuzzleBoard) current.next(action);
                    if (next == null) continue;

                    String nextCode = encodePatternState(next, group);

                    if (!visited.contains(nextCode)) {
                        visited.add(nextCode);
                        int nextCost = currentCost + 1;
                        database.put(nextCode, nextCost);
                        queue.offer(new Node(next, nextCost));
                    }
                }
            }

            System.out.printf("  探索状态: %d, 数据库大小: %d%n", statesExplored, database.size());
            return database;
        }

        /* ===================== 状态编码算法 ===================== */

        /**
         * 编码模式状态（核心编码方法）
         * 关键：只关心模式内瓷砖的位置，其他位置用'*'表示
         */
        private String encodePatternState(PuzzleBoard board, List<Integer> group) {
            int[][] grid = board.getGrid();
            StringBuilder sb = new StringBuilder();

            // 编码整个棋盘：模式内瓷砖显示数字，其他显示'*'
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int value = grid[i][j];
                    if (value == 0) {
                        sb.append('0'); // 空格特殊处理
                    } else if (group.contains(value)) {
                        sb.append(value).append(','); // 模式内瓷砖
                    } else {
                        sb.append('*').append(',');   // 不关心的瓷砖
                    }
                }
            }

            return sb.toString();
        }

        /* ===================== 工具方法 ===================== */

        /**
         * 创建目标棋盘
         */
        private PuzzleBoard createGoalBoard(int size) {
            int[][] goalGrid = new int[size][size];
            int value = 1;

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (i == size - 1 && j == size - 1) {
                        goalGrid[i][j] = 0; // 空白格
                    } else {
                        goalGrid[i][j] = value++;
                    }
                }
            }

            return new PuzzleBoard(goalGrid);
        }

        /**
         * 计算分组曼哈顿距离（回退启发式）
         */
        private int calculateGroupManhattan(PuzzleBoard board, List<Integer> group) {
            int[][] grid = board.getGrid();
            int heuristic = 0;

            // 创建目标位置映射
            Map<Integer, int[]> targetPositions = createTargetPositions(group);

            // 只计算分组内数字的曼哈顿距离
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int value = grid[i][j];
                    if (group.contains(value) && value != 0) {
                        int[] targetPos = targetPositions.get(value);
                        if (targetPos != null) {
                            heuristic += Math.abs(i - targetPos[0]) + Math.abs(j - targetPos[1]);
                        }
                    }
                }
            }

            return heuristic;
        }

        /**
         * 创建分组内瓷砖的目标位置映射
         */
        private Map<Integer, int[]> createTargetPositions(List<Integer> group) {
            Map<Integer, int[]> positions = new HashMap<>();
            PuzzleBoard goal = createGoalBoard(size);
            int[][] goalGrid = goal.getGrid();

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int value = goalGrid[i][j];
                    if (group.contains(value) && value != 0) {
                        positions.put(value, new int[]{i, j});
                    }
                }
            }

            return positions;
        }

        /* ===================== 缓存管理 ===================== */

        private Map<String, Integer> loadDatabaseFromCache(String filename) {
            try {
                Path path = Paths.get(filename);
                if (!Files.exists(path)) return null;

                try (ObjectInputStream ois = new ObjectInputStream(
                        Files.newInputStream(path))) {
                    return (Map<String, Integer>) ois.readObject();
                }
            } catch (Exception e) {
                System.out.println("缓存加载失败: " + e.getMessage());
                return null;
            }
        }

        private void saveDatabaseToCache(Map<String, Integer> database, String filename) {
            try {
                try (ObjectOutputStream oos = new ObjectOutputStream(
                        Files.newOutputStream(Paths.get(filename)))) {
                    oos.writeObject(database);
                }
            } catch (Exception e) {
                System.out.println("缓存保存失败: " + e.getMessage());
            }
        }
    }

    /* ===================== 辅助类 ===================== */

    /**
     * BFS节点类，用于模式数据库构建
     */
    private static class Node {
        final PuzzleBoard board;
        final int cost;

        Node(PuzzleBoard board, int cost) {
            this.board = board;
            this.cost = cost;
        }
    }

}