package com.jees.test.utils;

import com.jees.tool.utils.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

@Log4j2
public class StringTest {
    @Test
    public void test() {
        log.debug(StringUtil.cover2boolean("false", true));
        log.debug(StringUtil.cover2boolean("false1", true));
        log.debug(StringUtil.cover2byte("1", (byte) 2));
        log.debug(StringUtil.cover2byte("a", (byte) 2));
        log.debug(StringUtil.cover("1", Integer.class));
        log.debug(StringUtil.cover("2", Integer.class, 3));
        log.debug(StringUtil.cover("a", Integer.class, 4));
    }
}
