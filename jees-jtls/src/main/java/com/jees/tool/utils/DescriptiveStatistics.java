package com.jees.tool.utils;

/**
 * @Description: TODO
 * @Package: com.jees.tool.utils
 * @ClassName: DescriptiveStatistics
 * @Author: 刘甜
 * @Date: 2023/4/20 12:21
 * @Version: 1.0
 */
import java.util.Arrays;

public class DescriptiveStatistics {

    /**
     * 计算给定数组的算术平均数
     *
     * @param data 数组
     * @return     算术平均数
     */
    public static double mean(double[] data) {
        double sum = 0.0;
        for (double d : data) {
            sum += d;
        }
        return sum / data.length;
    }

    /**
     * 计算给定数组的中位数
     *
     * @param data 数组
     * @return     中位数
     */
    public static double median(double[] data) {
        Arrays.sort(data);
        int middle = data.length / 2;
        if (data.length % 2 == 0) {
            return (data[middle - 1] + data[middle]) / 2;
        } else {
            return data[middle];
        }
    }

    /**
     * 计算给定数组的众数
     *
     * @param data 数组
     * @return     众数数组
     */
    public static double[] mode(double[] data) {
        double[] copy = Arrays.copyOf(data, data.length);
        Arrays.sort(copy);

        int maxCount = 1;
        int currentCount = 1;
        double currentElement = copy[0];
        double maxElement = copy[0];

        for (int i = 1; i < copy.length; i++) {
            if (copy[i] == currentElement) {
                currentCount++;
            } else {
                if (currentCount > maxCount) {
                    maxCount = currentCount;
                    maxElement = currentElement;
                }
                currentElement = copy[i];
                currentCount = 1;
            }
        }

        if (currentCount > maxCount) {
            maxCount = currentCount;
            maxElement = currentElement;
        }

        int modeCount = 0;
        for (int i = 0; i < copy.length; i++) {
            if (copy[i] == maxElement) {
                modeCount++;
            }
        }

        double[] modes = new double[modeCount];
        int j = 0;
        for (int i = 0; i < copy.length; i++) {
            if (copy[i] == maxElement) {
                modes[j] = copy[i];
                j++;
            }
        }

        return modes;
    }

    /**
     * 计算给定数组的总体方差
     *
     * @param data 数组
     * @return     总体方差
     */
    public static double populationVariance(double[] data) {
        double mean = mean(data);
        double sum = 0.0;
        for (double d : data) {
            sum += Math.pow((d - mean), 2);
        }
        return sum / data.length;
    }

    /**
     * 计算给定数组的样本方差
     *
     * @param data 数组
     * @return     样本方差
     */
    public static double sampleVariance(double[] data) {
        double mean = mean(data);
        double sum = 0.0;
        for (double d : data) {
            sum += Math.pow((d - mean), 2);
        }
        return sum / (data.length - 1);
    }
/**
 * 计算给定数组的总体标准差
 *
 * @param data 数组
 * @return 总体标准差
 */
        public static double populationStandardDeviation(double[] data) {
            return Math.sqrt(populationVariance(data));
        }

    /**
     * 计算给定数组的样本标准差
     *
     * @param data 数组
     * @return     样本标准差
     */
    public static double sampleStandardDeviation(double[] data) {
        return Math.sqrt(sampleVariance(data));
    }

    /**
     * 计算给定数组的最小值
     *
     * @param data 数组
     * @return     最小值
     */
    public static double minimum(double[] data) {
        double min = data[0];
        for (double d : data) {
            if (d < min) {
                min = d;
            }
        }
        return min;
    }

    /**
     * 计算给定数组的最大值
     *
     * @param data 数组
     * @return     最大值
     */
    public static double maximum(double[] data) {
        double max = data[0];
        for (double d : data) {
            if (d > max) {
                max = d;
            }
        }
        return max;
    }

    /**
     * 计算给定数组的极差
     *
     * @param data 数组
     * @return     极差
     */
    public static double range(double[] data) {
        return maximum(data) - minimum(data);
    }

}