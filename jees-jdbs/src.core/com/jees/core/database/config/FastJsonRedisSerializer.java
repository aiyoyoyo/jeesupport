package com.jees.core.database.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {
    public static final Charset DEFAULT_CHARSET = Charset.forName( "UTF-8" );

    SerializeFilter[] filters = new SerializeFilter[]{};
    int features = SerializerFeature.config( JSON.DEFAULT_GENERATE_FEATURE, SerializerFeature.WriteEnumUsingName, false );

    @Override
    public byte[] serialize ( T _data ) throws SerializationException {
        if ( null == _data ) {
            return new byte[ 0 ];
        }

        String json_data = JSON.toJSONString( _data, SerializeConfig.globalInstance, filters, null, features,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteClassName,
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullListAsEmpty );

        return json_data.getBytes(DEFAULT_CHARSET);
    }

    @Override
    public T deserialize ( byte[] _data ) throws SerializationException {
        if ( null == _data || _data.length <= 0 ) {
            return null;
        }
        String str = new String( _data, DEFAULT_CHARSET );
        return ( T ) JSON.parse( str );
    }
}
