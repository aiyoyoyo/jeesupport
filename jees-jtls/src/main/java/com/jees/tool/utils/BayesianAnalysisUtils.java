package com.jees.tool.utils;

/**
 * @Description: 该工具类包含一个方法：
 *
 * calculatePosteriorProbabilities：计算给定的先验概率和似然函数，以确定后验概率。该方法接受两个 Map<String, Double> 类型的参数，分别表示先验概率和似然函数，返回一个 Map<String, Double> 类型的后验概率。
 * 该工具类的实现方式简单易懂。在 calculatePosteriorProbabilities 方法中，首先计算证据（即分母），然后分别计算每个假设的后验概率，并将结果存储在 posteriorProbabilities 中。
 * @Package: com.jees.tool.utils
 * @ClassName: BayesianAnalysisUtils
 * @Author: 刘甜
 * @Date: 2023/4/20 12:07
 * @Version: 1.0
 */
import java.util.HashMap;
import java.util.Map;

public class BayesianAnalysisUtils {

    /**
     * 计算给定的先验概率和似然函数，以确定后验概率。
     *
     * @param priorProbabilities 先验概率
     * @param likelihoods 似然函数
     * @return 后验概率
     */
    public static Map<String, Double> calculatePosteriorProbabilities(Map<String, Double> priorProbabilities, Map<String, Double> likelihoods) {
        Map<String, Double> posteriorProbabilities = new HashMap<>();
        double evidence = 0.0;

        // 计算证据
        for (Map.Entry<String, Double> entry : priorProbabilities.entrySet()) {
            String hypothesis = entry.getKey();
            double prior = entry.getValue();
            double likelihood = likelihoods.get(hypothesis);
            evidence += prior * likelihood;
        }

        // 计算后验概率
        for (Map.Entry<String, Double> entry : priorProbabilities.entrySet()) {
            String hypothesis = entry.getKey();
            double prior = entry.getValue();
            double likelihood = likelihoods.get(hypothesis);
            double posterior = (prior * likelihood) / evidence;
            posteriorProbabilities.put(hypothesis, posterior);
        }

        return posteriorProbabilities;
    }
}
