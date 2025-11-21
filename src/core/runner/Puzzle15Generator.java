package core.runner;

import algs4.util.StdRandom;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


/**
 * 15数码问题随机生成器（符合指定格式）
 * 格式：4 初始状态(16个数字) 目标状态(16个数字)
 */
public class Puzzle15Generator {

    // 标准目标状态
    private static final int[] GOAL_STATE = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0};

    /**
     * 生成指定数量的随机15数码问题
     *
     * @param outputPath 输出文件路径
     * @param count      生成的问题数量
     */
    public static void generatePuzzles(String outputPath, int count) throws FileNotFoundException {


        PrintWriter writer = new PrintWriter(new File(outputPath));
        System.out.println("开始生成 " + count + " 个随机15数码问题...");

        int solvableCount = 0;
        int attempts = 0;
        int maxAttempts = count * 10; // 防止无限循环

        while (solvableCount < count && attempts < maxAttempts) {
            int[] startState = generateRandomState();
            attempts++;

            if (isSolvable(startState)) {
                writer.print("4 ");
                for (int i = 0; i < startState.length; i++) {
                    writer.print(startState[i]);
                    if (i < startState.length - 1) writer.print(" ");
                }
                writer.print(" ");
                for (int i = 0; i < GOAL_STATE.length; i++) {
                    writer.print(GOAL_STATE[i]);
                    if (i < GOAL_STATE.length - 1) writer.print(" ");
                }
                writer.println();
                solvableCount++;
            }
        }

        writer.close();
    }

    /**
     * 生成随机状态（使用StdRandom.permutation）
     */
    private static int[] generateRandomState() {
        return StdRandom.permutation(16);
    }

    /**
     * 检查8数码状态是否可解
     */
    private static boolean isSolvable(int[] state) {
        int inversionCount = 0;
        int blankRow = 0; // 空白格子所在的行（从下往上数，0开始）

        // 计算逆序数（不考虑0）
        for (int i = 0; i < state.length; i++) {
            if (state[i] == 0) {
                // 记录空白格子的行位置（4x4网格中，从下往上数）
                blankRow = 3 - (i / 4); // 转换为从下往上数的行号
                continue;
            }
            for (int j = i + 1; j < state.length; j++) {
                if (state[j] != 0 && state[i] > state[j]) {
                    inversionCount++;
                }
            }
        }

        // 15数码可解性条件：
        // 1. 如果网格宽度为奇数，逆序数必须为偶数
        // 2. 如果网格宽度为偶数，逆序数 + 空白行号(从下往上数) 必须为奇数
        // 对于4x4网格（宽度为4，偶数）：
        return (inversionCount + blankRow) % 2 != 1;
    }

    /**
     * 将问题写入文件
     */

    /**
     * 将数组转换为字符串
     * 不确定需不需要？？？？？？
     */
//    private static String arrayToString(int[] array) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < array.length; i++) {
//            sb.append(array[i]);
//            if (i < array.length - 1) sb.append(" ");
//        }
//        return sb.toString();
//    }
}
