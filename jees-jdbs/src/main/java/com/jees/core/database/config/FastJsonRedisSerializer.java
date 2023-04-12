package com.jees.core.database.config;

import com.alibaba.fastjson2.JSON;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    @Override
    public byte[] serialize(T _data) throws SerializationException {
        if (null == _data) {
            return new byte[0];
        }

        String json_data = JSON.toJSONString(_data);
        return json_data.getBytes(DEFAULT_CHARSET);
    }

    @Override
    public T deserialize(byte[] _data) throws SerializationException {
        if (null == _data || _data.length <= 0) {
            return null;
        }
        String str = new String(_data, DEFAULT_CHARSET);
        return (T) JSON.parse(str);
    }
}
