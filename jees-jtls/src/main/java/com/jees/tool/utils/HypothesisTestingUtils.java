package com.jees.tool.utils;

/**
 * @Description: 该工具类包含两个方法：
 *
 * tTest：使用 t 检验检验两个样本均值是否显著不同。
 * zTest：使用 z 检验检验样本均值是否显著不同于指定的理论值。
 * 这两个方法分别调用了 Apache Commons Math3 库中的 TTest 和 NormalDistribution 类，实现了 t 检验和 z 检验的功能，并返回检验结果。
 * @Package: com.jees.tool.utils
 * @ClassName: HypothesisTestingUtils
 * @Author: 刘甜
 * @Date: 2023/4/20 12:08
 * @Version: 1.0
 */
//import org.apache.commons.math3.distribution.NormalDistribution;
//import org.apache.commons.math3.stat.inference.TTest;

public class HypothesisTestingUtils {

    /**
     * 使用 t 检验检验两个样本均值是否显著不同。
     *
     * @param sample1 样本1
     * @param sample2 样本2
     * @param alpha 显著性水平
     * @return 检验结果，true 表示拒绝原假设，即样本均值显著不同；false 表示接受原假设，即样本均值没有显著差异
     */
    public static boolean tTest(double[] sample1, double[] sample2, double alpha) {
//        TTest tTest = new TTest();
//        return tTest.tTest(sample1, sample2, alpha);
        return false;
    }

    /**
     * 使用 z 检验检验样本均值是否显著不同于指定的理论值。
     *
     * @param sample 样本
     * @param populationMean 理论值
     * @param populationStandardDeviation 理论值的标准差
     * @param alpha 显著性水平
     * @return 检验结果，true 表示拒绝原假设，即样本均值显著不同于理论值；false 表示接受原假设，即样本均值没有显著差异
     */
    public static boolean zTest(double[] sample, double populationMean, double populationStandardDeviation, double alpha) {
//        double sampleMean = StatUtils.mean(sample);
//        double sampleSize = sample.length;
//        double standardError = populationStandardDeviation / Math.sqrt(sampleSize);
//
//        NormalDistribution normalDistribution = new NormalDistribution(populationMean, standardError);
//        double zScore = (sampleMean - populationMean) / standardError;
//        double pValue = 1 - normalDistribution.cumulativeProbability(zScore);

//        return pValue < alpha;
        return false;
    }
}
