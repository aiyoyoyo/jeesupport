package com.jees.tool.utils;

import lombok.extern.log4j.Log4j2;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Log4j2
public class RuleUtil {
    /**
     * 构建匹配规则的Map数据，大规则用“;”号，小规则用“,”号
     * @param _skips 特殊内容：(*)，因为跳过会优先判断，所以等于覆盖所有匹配项
     * @param _match 默认全匹配 *[*]
     * @param _skipMap
     * @param _matchMap
     */
    public static void build(String _skips, String _match, Map<String, Set<String>> _skipMap, Map<String, Set<String>> _matchMap ){
        if( _match.isEmpty() ){
            _match = "*[*]";//默认全匹配
        }
        String[] rules = _skips.split(";");
        // 解析匹配规则
        for (String r : rules) {
            if (r.indexOf("[") != -1) _init_rules(r, "[", "]", _matchMap);
            if (r.indexOf("(") != -1) _init_rules(r, "(", ")", _skipMap);
        }
        rules = _match.split(";");
        // 解析跳过规则
        for (String r : rules) {
            if (r.indexOf("[") != -1) _init_rules(r, "[", "]", _matchMap);
            if (r.indexOf("(") != -1) _init_rules(r, "(", ")", _skipMap);
        }
        log.info("初始化自动填入规则: SKIPS=" + _skipMap.size() + ", MATCH=" + _matchMap.size() );
    }

    private static void _init_rules( String _rule, String _char0, String _char1, Map _rules ){
        int idx = _rule.indexOf( _char0 );
        int end = _rule.indexOf( _char1 );
        String tab;
        if( idx != -1 && end != -1 ){
            tab = _rule.substring( 0, idx );
            Set cols = (Set) _rules.getOrDefault( tab, new HashSet() );
            // 解析待自动填充的字段
            String[] str_cols = _rule.substring( idx + 1, end ).split(",");
            for( String col : str_cols ){
                if( !cols.contains( col) ){
                    cols.add( col );
                }
            }
            int s = tab.indexOf( _char0.equals("(") ? "[" : "(" );
            int e = tab.indexOf( _char1.equals(")") ? "]" : ")");
            if( s != -1 && e != -1 ){
                tab = tab.substring( 0, s );
            }
            _rules.put( tab, cols );
        }else{
            log.warn( _char0 + _char1 + "未配置正确: " + _rule );
        }
    }

    /**
     * 判断是否匹配规则
     * @param _key0 字符串1（括号外内容）
     * @param _key1 字符串2（括号内内容）
     * @param _skips
     * @param _match
     * @return
     */
    public static boolean isMatch( String _key0, String _key1, Map<String, Set<String>> _skips, Map<String, Set<String>> _match ){
        if( _key0 == null || _key1 == null ) return false;
        if( !_match_rule( _key0, _key1, _skips ) ){
            return _match_rule( _key0, _key1, _match );
        }
        return false;
    }

    private static boolean _match_rule( String _table, String _key, Map<String, Set<String>> _rules ){
        boolean tab_match = false;
        boolean col_match = false;
        Iterator<String> tab_it = _rules.keySet().iterator();
        while( tab_it.hasNext() ){
            String tab = tab_it.next();
            tab_match = _match_rule_with( tab, _table );

            if( tab_match ){
                Set<String> cols = _rules.get(tab);
                for( String col : cols ) {
                    if( _match_rule_with(col, _key) ){
                        col_match = true;
                        break;
                    }
                }
                if( !col_match ) tab_match = false;
                else break;
            }
        }
        return tab_match && col_match;
    }
    // _str 匹配规则 _char 待匹配的文字
    private static boolean _match_rule_with( String _str, String _char ){
        boolean match = false;
        if( _str.equals( "*" ) ) {
            match = true;
        }else{
            int idx = _str.indexOf( "*" );
            if( idx == 0 && _char.endsWith( _str.substring(idx + 1) ) ){
                match = true;
            }else if( idx > 0 && _char.startsWith( _str.substring(0, idx) ) ){
                match = true;
            }else if( _str.equalsIgnoreCase( _char ) ){
                match = true;
            }
        }
        return match;
    }
}
