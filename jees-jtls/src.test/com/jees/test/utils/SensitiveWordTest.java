package com.jees.test.utils;

import com.jees.tool.utils.SensitiveWordUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Set;

@Log4j2
public class SensitiveWordTest {
    @Test
    public void test() throws FileNotFoundException{
        SensitiveWordUtil.initialize( "classpath:dict/" );

        String        text = "xAxBxCxABxBCxABCx";
        Set< String > sets = SensitiveWordUtil.check( text, false );
        log.debug(  "找到敏感词：" + sets );

        text = SensitiveWordUtil.replace( text, "*", false );
        log.debug(  "替换敏感词：" + text );
    }
}
