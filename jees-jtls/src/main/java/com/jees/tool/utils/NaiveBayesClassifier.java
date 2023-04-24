package com.jees.tool.utils;

import java.util.*;
/**
 * @Description: 基于朴素贝叶斯算法的文本分类器
 * @Package: com.jees.tool.utils
 * @ClassName: NaiveBayesClassifier
 * @Author: 刘甜
 * @Date: 2023/4/20 11:59
 * @Version: 1.0
 */
public class NaiveBayesClassifier {
    private Map<String, Double> priors;  // 先验概率
    private Map<String, Map<String, Double>> likelihoods;  // 似然概率
    private Set<String> vocabulary;  // 词汇表

    public NaiveBayesClassifier() {
        priors = new HashMap<>();
        likelihoods = new HashMap<>();
        vocabulary = new HashSet<>();
    }

    public void train(List<Document> documents) {
        // 计算先验概率和似然概率
        Map<String, Integer> classCounts = new HashMap<>();
        Map<String, Map<String, Integer>> featureCounts = new HashMap<>();

        for (Document doc : documents) {
            String label = doc.getLabel();
            priors.put(label, priors.getOrDefault(label, 0.0) + 1);
            classCounts.put(label, classCounts.getOrDefault(label, 0) + 1);

            List<String> features = doc.getFeatures();
            for (String feature : features) {
                vocabulary.add(feature);
                Map<String, Integer> counts = featureCounts.computeIfAbsent(feature, k -> new HashMap<>());
                counts.put(label, counts.getOrDefault(label, 0) + 1);
            }
        }

        double totalDocs = documents.size();
        for (String label : priors.keySet()) {
            double count = classCounts.get(label);
            priors.put(label, count / totalDocs);

            Map<String, Double> likelihoodsForLabel = new HashMap<>();
            for (String feature : vocabulary) {
                Map<String, Integer> counts = featureCounts.getOrDefault(feature, new HashMap<>());
                double countForLabel = counts.getOrDefault(label, 0);
                double likelihood = (countForLabel + 1) / (count + vocabulary.size());
                likelihoodsForLabel.put(feature, likelihood);
            }
            likelihoods.put(label, likelihoodsForLabel);
        }
    }

    public String predict(Document doc) {
        // 计算后验概率并预测类别
        Map<String, Double> posteriors = new HashMap<>();

        for (String label : priors.keySet()) {
            double prior = priors.get(label);
            double posterior = Math.log(prior);

            List<String> features = doc.getFeatures();
            for (String feature : features) {
                Map<String, Double> likelihoodsForFeature = likelihoods.get(label);
                Double likelihood = likelihoodsForFeature.get(feature);
                if (likelihood != null) {
                    posterior += Math.log(likelihood);
                }
            }
            posteriors.put(label, posterior);
        }

        String bestLabel = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (Map.Entry<String, Double> entry : posteriors.entrySet()) {
            String label = entry.getKey();
            double score = entry.getValue();
            if (bestLabel == null || score > bestScore) {
                bestLabel = label;
                bestScore = score;
            }
        }
        return bestLabel;
    }

    public static class Document {
        private String label;
        private List<String> features;

        public Document(String label, List<String> features) {
            this.label = label;
            this.features = features;
        }

        public String getLabel() {
            return label;
        }

        public List<String> getFeatures() {
            return features;
        }
    }
}