package com.jees.tool.utils;

/**
 * @Description: TODO
 * @Package: com.jees.tool.utils
 * @ClassName: VarianceAnalysisUtils
 * @Author: 刘甜
 * @Date: 2023/4/20 12:02
 * @Version: 1.0
 */
//import org.apache.commons.math3.stat.inference.OneWayAnova;
//import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;

public class VarianceAnalysisUtils {
//  计算给定数据的 F 值。该方法接受一个 List<double[]> 类型的数据集，其中每个 double[] 数组表示一个组的样本数据。
    public static double calculateFValue(List<double[]> data) {
//        int k = data.size(); // 组数
//        int n = 0; // 总样本数
//        for (double[] d : data) {
//            n += d.length;
//        }
//
//        // 计算总体均值和总体方差
//        double[] allValues = new double[n];
//        int i = 0;
//        for (double[] d : data) {
//            for (double v : d) {
//                allValues[i++] = v;
//            }
//        }
//        DescriptiveStatistics stats = new DescriptiveStatistics(allValues);
//        double grandMean = stats.getMean();
//        double grandVariance = stats.getVariance();
//
//        // 计算组内平均方差
//        double[] groupVariances = new double[k];
//        int j = 0;
//        for (double[] d : data) {
//            DescriptiveStatistics groupStats = new DescriptiveStatistics(d);
//            groupVariances[j++] = groupStats.getVariance();
//        }
//        double meanWithinGroups = DescriptiveStatistics.of(groupVariances).getMean();
//
//        // 计算组间平均方差
//        double meanBetweenGroups = 0;
//        for (double[] d : data) {
//            DescriptiveStatistics groupStats = new DescriptiveStatistics(d);
//            double groupMean = groupStats.getMean();
//            meanBetweenGroups += (groupMean - grandMean) * (groupMean - grandMean);
//        }
//        meanBetweenGroups /= (k - 1);

        // 计算 F 值
//        double fValue = meanBetweenGroups / meanWithinGroups;

//        return fValue;
        return 0.D;
    }

//    使用给定的显著性水平 alpha（通常为 0.05）判断数据是否具有显著差异性。该方法返回一个布尔值，表示数据是否显著。
    public static boolean isSignificant(List<double[]> data, double alpha) {
//        double fValue = calculateFValue(data);
//        int k = data.size(); // 组数
//        int n = 0; // 总样本数
//        for (double[] d : data) {
//            n += d.length;
//        }
//        int dfBetween = k - 1;
//        int dfWithin = n - k;
//        OneWayAnova anova = new OneWayAnova();
//        double pValue = anova.anovaFValue(dfBetween, dfWithin, fValue);
//
//        return pValue < alpha;
        return false;
    }
}
