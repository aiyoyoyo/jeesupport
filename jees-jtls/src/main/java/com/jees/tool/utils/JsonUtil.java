package com.jees.tool.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.Filter;

public class JsonUtil{
    public static String toString( Object _obj ){
        return JSON.toJSONString( _obj );
    }

    public static String toString( Object _obj, String _format, Filter[] _filters, JSONWriter.Feature..._features ){
        return JSON.toJSONString( _obj, _format, _filters, _features );
    }
}
