package com.jees.test.utils;

import com.jees.tool.utils.FileUtil;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

@Log4j2
public class FileTest {
    @SneakyThrows
    @Test
    public void test() {
        log.info( FileUtil.path( "classpath:test/" ) ); // ${classpath}/dict
        log.info( FileUtil.path( "classpath*:test/" ) ); // ${classpath}/dict
        log.info( FileUtil.path( "test" ) ); // ${working}/dict
        log.info( FileUtil.path( "/test" ) ); // ${drive}/dict
        log.info( FileUtil.path( "./test" ) ); // ${working}/dict
        log.info( FileUtil.path( "../test" ) ); // ${working.parent}/dict
        log.info( FileUtil.path( "../../test" ) ); // ${working.parent.parent}/dict
        log.info( FileUtil.path( "E:/test" ) ); //  ${drive}/dict
        log.info( "-----------------" );
        log.info( ResourceUtils.getFile( "classpath:dict" ) ); // ${classpath}/dict
        log.info( ResourceUtils.getFile( "classpath*:dict" ) ); // ${classpath}/dict
        log.info( ResourceUtils.getFile( "dict" ) ); // dict
        log.info( ResourceUtils.getFile( "/dict" ) ); // /dict
        log.info( ResourceUtils.getFile( "./dict" ) ); // /dict
        log.info( ResourceUtils.getFile( "../dict" ) ); // ../dict
        log.info( ResourceUtils.getFile( "../../dict" ) ); // ../../dict
        log.info( ResourceUtils.getFile( "E:/dict" ) ); //  ${drive}/dict

    }
}
