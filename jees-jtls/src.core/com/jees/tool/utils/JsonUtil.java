package com.jees.tool.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonUtil{
    public static String toString( Object _obj ){
        return toString( _obj, filters );
    }

    public static String toString( Object _obj, SerializeFilter[] _filters ){
        return toString( _obj, _filters, features );
    }

    public static String toString( Object _obj, SerializeFilter[] _filters, int _features ){
        return toString( _obj, _filters, _features,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullListAsEmpty );
    }

    public static String toString( Object _obj, SerializeFilter[] _filters, int _features, SerializerFeature..._serializerFeatures ){
        return JSON.toJSONString( _obj, SerializeConfig.globalInstance, _filters, null, _features, _serializerFeatures );
    }

    static SerializeFilter[] filters = new SerializeFilter[]{};
    static int features = SerializerFeature.config( JSON.DEFAULT_GENERATE_FEATURE, SerializerFeature.WriteEnumUsingName, false );
}
