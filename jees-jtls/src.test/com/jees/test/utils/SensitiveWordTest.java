package com.jees.test.utils;

import com.jees.tool.utils.CustomSystemUtil;
import com.jees.tool.utils.SensitiveWordUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.util.Set;

@Log4j2
public class SensitiveWordTest {
    @Test
    public void test(){
        SensitiveWordUtil.initialize( "classpath:dict/" );

        String text = "测试验证试验测试验测试测";

        Set< String > sets = SensitiveWordUtil.check( text, true );

        log.info(  CustomSystemUtil.INTRANET_IP + "->" + CustomSystemUtil.INTERNET_IP + "找到敏感词：" + sets );
    }
}
