package com.jees.core.database.support;

import com.jees.tool.utils.StringUtil;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
public class SQLBuilder {
    private StringBuilder sql = new StringBuilder();

    String dbType;
    public SQLBuilder(String _dbType){
        this.dbType = _dbType;
    }

    public SQLBuilder select(String... columns) {
        sql.append("SELECT ");
        if (columns != null && columns.length > 0) {
            sql.append(String.join(", ", columns));
        } else {
            sql.append("*");
        }
        return this;
    }

    public SQLBuilder from(String table) {
        sql.append(" FROM ").append(table);
        return this;
    }

    public SQLBuilder where(Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            sql.append(" WHERE 1=1 ");
            AtomicReference<StringBuffer> sb = new AtomicReference<>(new StringBuffer());
            params.forEach((key, value) -> {
                String tmp_key = key.trim();
                if (!"orderBy".equalsIgnoreCase(tmp_key) && !"groupBy".equalsIgnoreCase(tmp_key)) {
                    if (value != null) {
                        // 标记 AND|OR 组合
                        if( tmp_key.equalsIgnoreCase("BEGIN AND") || tmp_key.equalsIgnoreCase("BEGIN OR")){
                            if( tmp_key.equalsIgnoreCase("BEGIN AND") ){
                                sql.append( " AND ( ");
                                sb.get().append( " AND ");
                            }else if ( tmp_key.equalsIgnoreCase("BEGIN OR") ){
                                sql.append( " OR ( ");
                                sb.get().append( " OR ");
                            }
                        }else if( tmp_key.equalsIgnoreCase("END AND") || tmp_key.equalsIgnoreCase("END OR") ){
                            sql.append( " ) ");
                            sb.set(new StringBuffer());
                        }else {
                            if (value instanceof String) {
                                this._append_where_key(tmp_key);
                                this._append_where_string_value((String) value);
                            } else if (value instanceof List || value instanceof Set || value.getClass().isArray()) {
                                List list = toList(value);
                                if( list.isEmpty() ){
                                    return;
                                }
                                if( !this._append_where_key( sb.get().toString() + tmp_key) ){
                                    sql.append( " IN ");
                                }
                                this._append_where_array_value(toList(value));
                            } else {
                                // 仅限整型和布尔型
                                this._append_where_key(" " + tmp_key);
                                sql.append(" = ").append(" ").append(value).append(" ");
                            }
                        }
                    }
                }
            });
        }
        return this;
    }

    private void _append_where_array_value(List<Object> _list) {
        if (_list.isEmpty()) {
            return;
        }

        StringBuilder tmp_o = new StringBuilder();

        for (Object o : _list) {
            if (o == null) {
                continue;
            }
            if (o instanceof String) {
                tmp_o.append("'").append(o).append("',");
            } else if (o instanceof Number || o instanceof Boolean) {
                tmp_o.append(o).append(",");
            } else {
                log.warn("--未支持的数据类型：" + o);
            }
        }
        if (tmp_o.length() > 0) {
            tmp_o.deleteCharAt(tmp_o.length() - 1); // 移除最后一个逗号
        }
        sql.append(" (").append(tmp_o).append(") ");
    }

    private void _append_where_string_value(String _value) {
        String tmp_val = _value.trim();
        // 特殊字符开头的处理
        if (tmp_val.startsWith("%") || tmp_val.endsWith("%") || tmp_val.startsWith("!%")) {
            if (tmp_val.startsWith("!%")) {
                sql.append(" NOT LIKE ").append( _append_where_value( tmp_val, true ) );
            } else {
                sql.append(" LIKE ").append( _append_where_value( tmp_val, true ) );
            }
        } else if (tmp_val.startsWith(">") || tmp_val.startsWith("<") || tmp_val.startsWith("=") || tmp_val.startsWith("!=")
                || tmp_val.toLowerCase().startsWith("not")) {
            // 不做处理直接拼接，但是需要判定结尾是否合法 防止sql注入
            // not like 要重新
            // > >= < <= <> = 都需要限定是数字或者时间
            sql.append( _append_where_value( tmp_val, false ) );
        } else {
            sql.append(" = ").append( this._append_where_value( tmp_val, true ) );
        }
    }

    private String _append_where_value(String _value, boolean _fixed){
        // TODO 处理字符串中的特殊符号 # ' " 等
        String value = _value.trim();
        if( value.startsWith("'") && value.endsWith("'") ){
            return value;
        }
        return _fixed ? " '" + value + "' " : value;
    }

    private boolean _append_where_key(String _key) {
        // 使用正则表达式替换所有的 AND 和 OR 连接方式
        boolean match_AND_OR = _key.matches("(?i).*\\b(?:AND|OR)\\b.*");
        boolean match_IN_NOT_IN = _key.matches("(?i).*\\b(?:IN|NOT\\s+IN)\\b.*");

        String key = _key;
        if (match_AND_OR || match_IN_NOT_IN) {
            key = _key.replaceAll("(?i)\\b(AND|OR|IN|NOT\\s+IN)\\b", "");
            _key = _key.replace( key, " {KEY} ");
            if( match_IN_NOT_IN && !match_AND_OR ){
                sql.append(" AND ");
            }
        }else{
            sql.append(" AND ");
        }

        if ("mysql".equalsIgnoreCase(dbType)) {
            key = "`" + key.trim() + "`";
        }

        if (match_AND_OR || match_IN_NOT_IN) {
            sql.append(_key.replace( "{KEY}", key));
        }else{
            sql.append(key.trim());
        }

        return match_AND_OR || match_IN_NOT_IN;
    }

    public SQLBuilder groupBy(String column) {
        if (StringUtil.isNotEmpty(column)) {
            sql.append(" GROUP BY ").append(column);
        }
        return this;
    }

    public SQLBuilder orderBy(String column) {
        if (StringUtil.isNotEmpty(column)) {
            sql.append(" ORDER BY ").append(column);
        }
        return this;
    }

    public String build() {
        String tmp = sql.toString();
        tmp = tmp.replace("AND (  ", "AND ( ")
                .replace( "OR (  ", "OR ( ")
        ;
        return tmp
                .replace(" AND ( OR ", " AND ( ").replace(" AND ( AND ", " AND ( ")
                .replace(" OR ( OR ", " OR ( ").replace(" OR ( AND ", " OR ( ")
                ;
    }

    @SuppressWarnings({"unchecked", "rawtypes", "DataFlowIssue"})
    public static List<Object> toList(Object val) {
        List<Object> list = new ArrayList();
        if (val instanceof String[]) {
            String[] arr = (String[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof Integer[]) {
            Integer[] arr = (Integer[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof int[]) {
            int[] arr = (int[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof Long[]) {
            Long[] arr = (Long[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof long[]) {
            long[] arr = (long[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof Float[]) {
            Float[] arr = (Float[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof float[]) {
            float[] arr = (float[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof Double[]) {
            Double[] arr = (Double[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof double[]) {
            double[] arr = (double[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof Boolean[]) {
            Boolean[] arr = (Boolean[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof boolean[]) {
            int[] arr = (int[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof Byte[]) {
            Byte[] arr = (Byte[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof byte[]) {
            byte[] arr = (byte[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof List) {
            list = (List<Object>) val;
        } else if (val instanceof Set) {
            Set tmps = (Set) val;
            list.addAll(tmps);
        } else {
            throw new RuntimeException("can't add to Array");
        }
        return list;
    }


    public static void main(String[] args) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put( "BEGIN AND", "");
//        params.put("AA", "张三");
//        params.put("BB", 18);
        params.put("OR CC ", 66);
        params.put("OR DD ", 77);
//        params.put("AND DD", "bb");
//        params.put("AA1", Arrays.asList("cc1", "cc2"));
//        params.put("AA IN", Arrays.asList("cc1", "cc2"));
//        params.put("DD NOT IN", new String[]{"dd1", "dd2"});
//        params.put("AND CC IN", Arrays.asList("cc1", "cc2"));
//        params.put("OR DD NOT IN", new String[]{"dd1", "dd2"});
//        params.put("BEGINEE1", "%EE");
//        params.put("ANDEE2", "EE%");
//        params.put("OREE3", "%EE%");
//        params.put("FF1IN", "> 10");
//        params.put("FF2NOT_IN", "< 10");

//        params.put("FF3", "= 10");
//        params.put("FF4", "= '10'");
//        params.put("GG ", "not like 'GG'");
//        params.put("groupBy", " GG,FF");
//        params.put("orderBy", " GG ASC");
        params.put( "END AND", "");
//        params.put( "AA", new HashSet(){{
//        }});
//        params.put( "orderBy", "ORDERBY");
        System.out.println(new SQLBuilder("mysql").where(params).groupBy((String) params.get("groupBy"))
                .orderBy((String) params.get("orderBy")).build());
    }

}
