package com.jees.tool.utils;

/**
 * @Description:
 * 该工具类使用了 DBSCAN（Density-Based Spatial Clustering of Applications with Noise）算法进行聚类分析，其中：
 *
 * - `dbscanClustering` 方法接受一个数据集、一个半径和一个最小密度，返回聚类结果。该方法首先会遍历数据集中的每个数据点，并获取其密度可达点集合，然后对于每个具有足够密度的数据点，创建一个新的聚类簇，并将其密度可达点集合加入到该聚类簇中。如果某个数据点的密度不足以成为一个聚类簇，则将其标记为噪声点。聚类结果以一个 `Map<Integer, List<int[]>>` 对象形式返回，其中键为聚类簇的索引，值为该聚类簇包含的所有数据点。
 * - `regionQuery` 方法接受一个数据集、一个数据点和一个半径，返回数据集中所有在以该数据点为圆心，半径为该半径的圆内的数据点。该方法遍历数据集中的每个数据点，计算该数据点与给定数据点之间的距离，然后将距离小于等于该半径的数据点加入到一个列表中，并将该列表作为密度可达点集合返回。
 * - `euclideanDistance` 方法接受两个数据点，返回它们之间的欧几里德距离。该方法遍历两个数据点的每个特征维度，计算对应特征之间的差值的平方和，然后返回平方和的平方根。
 * @Package: com.jees.tool.utils
 * @ClassName: DBSCANClustering
 * @Author: 刘甜
 * @Date: 2023/4/20 12:15
 * @Version: 1.0
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBSCANClustering {

    /**
     * 对数据集进行 DBSCAN 聚类分析
     *
     * @param dataset 数据集
     * @param epsilon 半径
     * @param minPts  最小密度
     * @return 聚类结果
     */
    public static Map<Integer, List<int[]>> dbscanClustering(List<int[]> dataset, double epsilon, int minPts) {
        // 初始化聚类簇的索引
        int clusterIdx = 0;
        // 初始化聚类结果
        Map<Integer, List<int[]>> clustering = new HashMap<>();

        // 遍历数据集中的每个数据点
        for (int[] datapoint : dataset) {
            // 如果该数据点已经被访问过，则跳过
            if (datapoint[2] != -1) {
                continue;
            }

            // 获取该数据点的密度可达点集合
            List<int[]> neighborPts = regionQuery(dataset, datapoint, epsilon);

            // 如果该数据点的密度不足以成为一个聚类簇，则标记为噪声点
            if (neighborPts.size() < minPts) {
                datapoint[2] = -2;
                continue;
            }

            // 创建一个新的聚类簇，并将该数据点及其密度可达点集合加入到该聚类簇中
            clusterIdx++;
            clustering.put(clusterIdx, new ArrayList<>());
            clustering.get(clusterIdx).add(datapoint);
            datapoint[2] = clusterIdx;
            for (int[] neighbor : neighborPts) {
                if (neighbor[2] == -2) {
                    neighbor[2] = clusterIdx;
                }
                if (neighbor[2] != -1) {
                    continue;
                }
                clustering.get(clusterIdx).add(neighbor);
                neighbor[2] = clusterIdx;

                // 获取该密度可达点的密度可达点集合，并将其加入到该聚类簇中
                List<int[]> neighborNeighborPts = regionQuery(dataset, neighbor, epsilon);
                if (neighborNeighborPts.size() >= minPts) {
                    neighborPts.addAll(neighborNeighborPts);
                }
            }
        }

        return clustering;
    }

    /**
     * 获取数据集中所有在以该数据点为圆心，半径为 epsilon 的圆内的数据点
     *
     * @param dataset   数据集
     * @param datapoint 数据点
     * @param epsilon   半径
     * @return 密度可达点集合
     */
    private static List<int[]> regionQuery(List<int[]> dataset, int[] datapoint, double epsilon) {
        List<int[]> neighborPts = new ArrayList<>();
        for (int[] neighbor : dataset) {
            double distance = euclideanDistance(datapoint, neighbor);
            if (distance <= epsilon) {
                neighborPts.add(neighbor);
            }
        }
        return neighborPts;
    }

    /**
     * 计算两个数据点之间的欧几里德
     * 距离
     *
     * @param datapoint1 数据点1
     * @param datapoint2 数据点2
     * @return 两个数据点之间的欧几里德距离
     */
    private static double euclideanDistance(int[] datapoint1, int[] datapoint2) {
        double sum = 0.0;
        for (int i = 0; i < datapoint1.length; i++) {
            sum += Math.pow((datapoint1[i] - datapoint2[i]), 2);
        }
        return Math.sqrt(sum);
    }
}
