package com.sidney.coin.helper;

import java.util.Objects;
import java.util.Random;

public class StringHelper {

    public static String genRandomStr(int length) {
        char[] charPool = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        int count = charPool.length;
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(count);
            builder.append(charPool[index]);
        }
        return builder.toString();
    }

    /**
     * 计算两个字符串的编辑距离
     */
    public static int calculateEditDistance(String str1, String str2) {
        //        if (StringHelper.isEmpty(str1) && StringHelper.isEmpty(str2)) {
        //            return 0;
        //        }
        //        if (StringHelper.isEmpty(str1) && StringHelper.isNotEmpty(str2)) {
        //            return str2.length();
        //        }
        //        if (StringHelper.isNotEmpty(str1) && StringHelper.isEmpty(str2)) {
        //            return str1.length();
        //        }
        //计算两个字符串的长度。
        int len1 = str1.length();
        int len2 = str2.length();
        //建立上面说的数组，比字符长度大一个空间
        int[][] dif = new int[len1 + 1][len2 + 1];
        //赋初值，步骤B。
        for (int i = 0; i <= len1; i++) {
            dif[i][0] = i;
        }
        for (int i = 0; i <= len2; i++) {
            dif[0][i] = i;
        }
        //计算两个字符是否一样，计算左上的值
        int temp;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                //取三个值中最小的
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1, dif[i - 1][j] + 1);
            }
        }
        //取数组右下角的值，同样不同位置代表不同字符串的比较
        return dif[len1][len2];
    }

    /**
     * 两个字符串相似度
     *
     * @param str1
     * @param str2
     * @return
     */
    public static double calculateSimilar(String str1, String str2) {
        if (Objects.equals(str1, str2)) return 1;
        int distance = calculateEditDistance(str1, str2);
        return 1 - (double) distance / Math.max(str1.length(), str2.length());
    }

    //得到最小值
    private static int min(int... is) {
        int min = Integer.MAX_VALUE;
        for (int i : is) {
            if (min > i) {
                min = i;
            }
        }
        return min;
    }

    public static String lpadding(int length, String input) {
        String format = "%0" + length + "d";
        return String.format(format, input);
    }

    public static String lpadding(int length, int input) {
        String format = "%0" + length + "d";
        return String.format(format, input);
    }

    public static String lpadding(int length, long input) {
        String format = "%0" + length + "d";
        return String.format(format, input);
    }

}
