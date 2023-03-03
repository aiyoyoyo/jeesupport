package com.jees.tool.utils;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Description: 新的文件操作类
 * @Package: com.jees.tool.utils
 * @ClassName: FileOperationUtil
 * @Author: 刘甜
 * @Date: 2023/3/3 17:05
 * @Version: 1.0
 */
@Log4j2
public class FileOperationUtil {

    /**
     * 创建一个新文件
     *
     * @param filePath 文件路径
     * @return true：文件创建成功，false：文件创建失败
     */
    public static boolean createFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return false;
        }
        if (filePath.endsWith("/")) {
            return file.mkdir();
        }
        if (file.isDirectory()) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 复制文件或文件夹
     *
     * @param sourcePath 原文件路径
     * @param targetPath 目标文件路径
     * @return true：复制成功，false：复制失败
     */
    public static boolean copyFile(String sourcePath, String targetPath) {
        File sourceFile = new File(sourcePath);
        File targetFile = new File(targetPath);
        if (!sourceFile.exists()) {
            return false;
        }
        if (sourceFile.isFile()) {
            try (FileInputStream fis = new FileInputStream(sourceFile);
                 FileOutputStream fos = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }
            File[] files = sourceFile.listFiles();
            for (File file : files) {
                String fileName = file.getName();
                String newSourcePath = sourcePath + File.separator + fileName;
                String newTargetPath = targetPath + File.separator + fileName;
                if (!copyFile(newSourcePath, newTargetPath)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 移动文件或文件夹
     *
     * @param sourcePath 原文件路径
     * @param targetPath 目标文件路径
     * @param deleteSource 移动成功是否删除源文件
     * @return true：移动成功，false：移动失败
     */
    public static boolean moveFile(String sourcePath, String targetPath, boolean deleteSource) {
        if (copyFile(sourcePath, targetPath)) {
            if( deleteSource ){
                deleteFile(sourcePath);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除文件或文件夹
     *
     * @param filePath 文件路径
     * @return true：删除成功，false：删除失败
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            return file.delete();
        } else {
            File[] files = file.listFiles();
            for (File subFile : files) {
                if (!deleteFile(subFile.getAbsolutePath())) {
                    return false;
                }
            }
            return file.delete();
        }
    }

}
