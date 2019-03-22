package com.jees.core.database.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ConditionalOnClass( RedisOperations.class)
@EnableConfigurationProperties( RedisProperties.class)
public class RedisConfig {
    @Bean
    @ConditionalOnMissingBean( StringRedisTemplate.class )
    @Primary
    public <T> RedisTemplate<String, T> redisTemplate (
            RedisConnectionFactory redisConnectionFactory ) {
        RedisTemplate<String, T> template = new RedisTemplate<>();

        //使用fastjson序列化
        FastJsonRedisSerializer<T> fastJsonRedisSerializer = new FastJsonRedisSerializer();
        // value值的序列化采用fastJsonRedisSerializer
        template.setValueSerializer( fastJsonRedisSerializer );
        template.setHashValueSerializer( fastJsonRedisSerializer );
        // key的序列化采用StringRedisSerializer
        template.setKeySerializer( new StringRedisSerializer() );
        template.setHashKeySerializer( new StringRedisSerializer() );

        template.setConnectionFactory( redisConnectionFactory );

        // 开启事务
        template.setEnableTransactionSupport( true );

        return template;
    }

    @ConditionalOnMissingBean( {StringRedisTemplate.class} )
    @Bean
    public StringRedisTemplate stringRedisTemplate( RedisConnectionFactory redisConnectionFactory ) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory( redisConnectionFactory );
        return template;
    }
}
