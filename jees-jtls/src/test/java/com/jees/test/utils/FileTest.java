package com.jees.test.utils;

import com.jees.tool.utils.FileOperationUtil;
import com.jees.tool.utils.FileUtil;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

@Log4j2
public class FileTest {
    @SneakyThrows
    @Test
    public void test() {
        log.info(FileUtil.path("classpath:com.jees.test/")); // ${classpath}/dict
        log.info(FileUtil.path("classpath*:com.jees.test/")); // ${classpath}/dict
        log.info(FileUtil.path("com.jees.test")); // ${working}/dict
        log.info(FileUtil.path("/com.jees.test")); // ${drive}/dict
        log.info(FileUtil.path("./com.jees.test")); // ${working}/dict
        log.info(FileUtil.path("../com.jees.test")); // ${working.parent}/dict
        log.info(FileUtil.path("../../test")); // ${working.parent.parent}/dict
        log.info(FileUtil.path("E:/com.jees.test")); //  ${drive}/dict
        log.info("-----------------");
        log.info(ResourceUtils.getFile("classpath:dict")); // ${classpath}/dict
        log.info(ResourceUtils.getFile("classpath*:dict")); // ${classpath}/dict
        log.info(ResourceUtils.getFile("dict")); // dict
        log.info(ResourceUtils.getFile("/dict")); // /dict
        log.info(ResourceUtils.getFile("./dict")); // /dict
        log.info(ResourceUtils.getFile("../dict")); // ../dict
        log.info(ResourceUtils.getFile("../../dict")); // ../../dict
        log.info(ResourceUtils.getFile("E:/dict")); //  ${drive}/dict
    }

    @Test
    public void test2() {
//        log.info( FileUtil.path( "classpath:test3/" ) );
//        log.info( FileUtil.path( "E:/test1/test2/test3/" ) );
//        log.info( FileUtil.path( "classpath:test3/", false ) );
//        log.info( FileUtil.path( "classpath:test3/", true ) );

        log.info(FileUtil.load("E:/test1/test2/test3/", false));
        log.info(FileUtil.load("E:/test1/test2/test3/", true));
    }

    @Test
    public void test3() {
        log.info("测试创建文件：D:/test/test.txt>" + FileOperationUtil.createFile("D:/test/test.txt"));
        log.info("测试创建文件夹：D:/test/>" + FileOperationUtil.createFile("D:/test/"));
        log.info("测试创建文件：D:/test/test.txt>" + FileOperationUtil.createFile("D:/test/test.txt"));
        log.info("测试复制文件：D:/test/test.txt->D:/test/test2.txt>" + FileOperationUtil.copyFile("D:/test/test.txt", "D:/test/test2.txt"));
        log.info("测试复制文件夹：D:/test/->D:/test2/>" + FileOperationUtil.copyFile("D:/test/", "D:/test2/"));
        log.info("测试移动文件：D:/test/test2.txt->D:/test2/text3.txt>" + FileOperationUtil.moveFile("D:/test/test2.txt", "D:/test2/test3.txt", false));
        log.info("测试移动文件夹：D:/test/->D:/test2/>" + FileOperationUtil.moveFile("D:/test/", "D:/test3/", true));
        log.info("测试删除文件：D:/test2/text3.txt>" + FileOperationUtil.deleteFile("D:/test/"));
        log.info("测试删除文件夹：D:/test2/>" + FileOperationUtil.deleteFile("D:/test2/"));
        log.info("测试删除文件夹：D:/test3/>" + FileOperationUtil.deleteFile("D:/test3/"));
    }
}
