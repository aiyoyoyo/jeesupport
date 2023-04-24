package com.jees.tool.utils;

/**
 * @Description:
 * 这个工具类实现了 K-means 算法，该算法是一种常见的聚类分析算法。该工具类中的 `kMeansClustering` 方法接收一个数据集、一个聚类数目和一个最大迭代次数，并返回聚类结果。聚类结果是一个 `Map`，键为聚类簇的索引，值为属于该聚类簇的数据点列表。在实现过程中，我们使用了欧几里德距离作为数据点之间的距离度量，使用随机生成的初始聚类簇中心点，并使用迭代的方式逐步优化聚类结果，直到满足迭代次数限制或聚类簇中心点不再发生改变为止。
 * @Package: com.jees.tool.utils
 * @ClassName: ClusteringUtils
 * @Author: 刘甜
 * @Date: 2023/4/20 12:12
 * @Version: 1.0
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ClusteringUtils {

    /**
     * 使用 K-means 算法对数据进行聚类。
     *
     * @param data          数据集
     * @param k             聚类数目
     * @param maxIterations 最大迭代次数
     * @return 聚类结果，键为聚类簇的索引，值为属于该聚类簇的数据点列表
     */
    public static Map<Integer, List<double[]>> kMeansClustering(List<double[]> data, int k, int maxIterations) {
        int numDimensions = data.get(0).length;
        int numDataPoints = data.size();

        // 初始化聚类簇的中心点
        List<double[]> clusterCentroids = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < k; i++) {
            double[] centroid = new double[numDimensions];
            for (int j = 0; j < numDimensions; j++) {
                centroid[j] = random.nextDouble();
            }
            clusterCentroids.add(centroid);
        }

        // 进行 K-means 聚类
        Map<Integer, List<double[]>> clusters = new HashMap<>();
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            for (int i = 0; i < numDataPoints; i++) {
                double[] dataPoint = data.get(i);
                int nearestCentroidIndex = 0;
                double nearestCentroidDistance = Double.MAX_VALUE;
                for (int j = 0; j < k; j++) {
                    double[] centroid = clusterCentroids.get(j);
                    double distance = euclideanDistance(dataPoint, centroid);
                    if (distance < nearestCentroidDistance) {
                        nearestCentroidIndex = j;
                        nearestCentroidDistance = distance;
                    }
                }
                if (!clusters.containsKey(nearestCentroidIndex)) {
                    clusters.put(nearestCentroidIndex, new ArrayList<>());
                }
                clusters.get(nearestCentroidIndex).add(dataPoint);
            }

            // 更新聚类簇的中心点
            boolean centroidsUpdated = false;
            for (int i = 0; i < k; i++) {
                List<double[]> cluster = clusters.get(i);
                if (cluster != null && !cluster.isEmpty()) {
                    double[] newCentroid = new double[numDimensions];
                    for (int j = 0; j < numDimensions; j++) {
                        double sum = 0.0;
                        for (double[] dataPoint : cluster) {
                            sum += dataPoint[j];
                        }
                        newCentroid[j] = sum / cluster.size();
                    }
                    if (!centroidsUpdated && euclideanDistance(clusterCentroids.get(i), newCentroid) > 0.001) {
                        centroidsUpdated = true;
                    }
                    clusterCentroids.set(i, newCentroid);
                }
            }

            if (!centroidsUpdated) {
                break;
            }

            // 清空旧的聚类结果
            clusters.clear();
        }

        return clusters;
    }

    /**
     * 计算两个向量之间的欧几里德距离。
     *
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 向量1和向量2之间的欧几里德距离
     */
    private static double euclideanDistance(double[] vector1, double[] vector2) {
        double sum = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            double diff = vector1[i] - vector2[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
}
