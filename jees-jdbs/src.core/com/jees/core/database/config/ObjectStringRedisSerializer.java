package com.jees.core.database.config;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ObjectStringRedisSerializer implements RedisSerializer<Object>{
    private final       Charset               charset;
    public static final StringRedisSerializer US_ASCII;
    public static final StringRedisSerializer ISO_8859_1;
    public static final StringRedisSerializer UTF_8;

    public ObjectStringRedisSerializer() {
        this( StandardCharsets.UTF_8);
    }

    public ObjectStringRedisSerializer(Charset charset) {
        Assert.notNull( charset, "Charset must not be null!");
        this.charset = charset;
    }

    public String deserialize(@Nullable byte[] bytes) {
        return bytes == null ? null : new String(bytes, this.charset);
    }

    public byte[] serialize(@Nullable Object object) {
        return object == null ? null : object.toString().getBytes(this.charset);
    }

    static {
        US_ASCII = new StringRedisSerializer(StandardCharsets.US_ASCII);
        ISO_8859_1 = new StringRedisSerializer(StandardCharsets.ISO_8859_1);
        UTF_8 = new StringRedisSerializer(StandardCharsets.UTF_8);
    }
}
