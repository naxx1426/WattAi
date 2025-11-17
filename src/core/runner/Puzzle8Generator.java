package core.runner;

import algs4.util.StdRandom;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


/**
 * 8数码问题随机生成器（符合指定格式）
 * 格式：3 初始状态(9个数字) 目标状态(9个数字)
 */
public class Puzzle8Generator {

    // 标准目标状态
    private static final int[] GOAL_STATE = {1, 2, 3, 4, 5, 6, 7, 8, 0};

    /**
     * 生成指定数量的随机8数码问题
     *
     * @param outputPath 输出文件路径
     * @param count      生成的问题数量
     */
    public static void generatePuzzles(String outputPath, int count) throws FileNotFoundException {


        PrintWriter writer = new PrintWriter(new File(outputPath));
        System.out.println("开始生成 " + count + " 个随机8数码问题...");

        int solvableCount = 0;
        int attempts = 0;
        int maxAttempts = count * 5; // 防止无限循环

        while (solvableCount < count && attempts < maxAttempts) {
            int[] startState = generateRandomState();
            attempts++;

            if (isSolvable(startState)) {
                writer.print("3 ");
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
        return StdRandom.permutation(9);
    }

    /**
     * 检查8数码状态是否可解
     */
    private static boolean isSolvable(int[] state) {
        int inversionCount = 0;

        // 计算逆序数（不考虑0）
        for (int i = 0; i < state.length; i++) {
            for (int j = i + 1; j < state.length; j++) {
                if (state[i] != 0 && state[j] != 0 && state[i] > state[j]) {
                    inversionCount++;
                }
            }
        }

        // 8数码可解性条件：逆序数为偶数
        return inversionCount % 2 == 0;
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
