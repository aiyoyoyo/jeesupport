package com.jees.test.utils;

import com.jees.tool.utils.RuleUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Log4j2
public class RuleTest {
    @Test
    public void test() throws UnsupportedEncodingException {

        Map<String, Set<String>> skips = new HashMap<>();
        Map<String, Set<String>> match = new HashMap<>();

        String ignore = "MatchB*(B);IgnoreA*(B,C*)"; //特殊内容：(*)，因为跳过会优先判断，所以等于覆盖所有
        String matchs = "Match*[*]";//默认全匹配 *[*]
        RuleUtil.build(ignore, matchs, skips, match);

        log.debug("匹配字符内容(MatchAll.*通过):" + RuleUtil.isMatch("MatchAll", "*", skips, match));
        log.debug("匹配字符内容(MatchA0.A通过):" + RuleUtil.isMatch("MatchA0", "A", skips, match));
        log.debug("匹配字符内容(MatchA1.B通过):" + RuleUtil.isMatch("MatchA1", "B", skips, match));
        log.debug("匹配字符内容(MatchB0.A通过):" + RuleUtil.isMatch("MatchB0", "A", skips, match));
        log.debug("匹配字符内容(MatchB1.B不通过):" + RuleUtil.isMatch("MatchB1", "B", skips, match));
        log.debug("匹配字符内容(MatchB1.B1通过):" + RuleUtil.isMatch("MatchB1", "B1", skips, match));
        log.debug("匹配字符内容(IgnoreAll.*通过):" + RuleUtil.isMatch("IgnoreAll", "*", skips, match));
        log.debug("匹配字符内容(IgnoreA0.*通过):" + RuleUtil.isMatch("IgnoreA0", "*", skips, match));
        log.debug("匹配字符内容(IgnoreA1.A通过):" + RuleUtil.isMatch("IgnoreA1", "A", skips, match));
        log.debug("匹配字符内容(IgnoreA2.B不通过):" + RuleUtil.isMatch("IgnoreA2", "B", skips, match));
        log.debug("匹配字符内容(IgnoreA3.C不通过):" + RuleUtil.isMatch("IgnoreA3", "C", skips, match));
        log.debug("匹配字符内容(IgnoreA4.C1不通过):" + RuleUtil.isMatch("IgnoreA4", "C1", skips, match));
        log.debug("匹配字符内容(IgnoreA5.C2不通过):" + RuleUtil.isMatch("IgnoreA5", "C2", skips, match));
        log.debug("匹配字符内容(ignorea6.c3通过):" + RuleUtil.isMatch("ignorea6", "c3", skips, match));
        log.debug("匹配字符内容(ignorea7.*通过):" + RuleUtil.isMatch("ignorea7", "*", skips, match));
    }
}
