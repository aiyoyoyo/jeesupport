package com.jees.core.database.config;

import com.jees.common.CommonConfig;
import com.jees.core.database.support.AbsRedisDao;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static org.apache.commons.pool2.impl.GenericObjectPoolConfig.*;

@Configuration
@EnableConfigurationProperties( RedisProperties.class)
public class RedisConfig{

    @Autowired
    AbsRedisDao dao;

    @Bean( destroyMethod = "shutdown" )
    ClientResources clientResources(){
        return DefaultClientResources.create();
    }

    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration(){
        String host = CommonConfig.getString( "spring.redis.host", "127.0.0.1" );
        int port = CommonConfig.getInteger( "spring.redis.port", 6379 );
        return new RedisStandaloneConfiguration( host, port );
    }

    @Bean
    public ClientOptions clientOptions(){
        return ClientOptions.builder()
                .disconnectedBehavior( ClientOptions.DisconnectedBehavior.REJECT_COMMANDS )
                .autoReconnect( true )
                .build();
    }

    @Bean
    LettucePoolingClientConfiguration lettucePoolConfig( ClientOptions _co, ClientResources _cr ){
        GenericObjectPoolConfig gopc = new GenericObjectPoolConfig();

        gopc.setMaxIdle( CommonConfig.getInteger( "spring.redis.pool.max-idle", DEFAULT_MAX_IDLE ) );
        gopc.setMinIdle( CommonConfig.getInteger( "spring.redis.pool.min-idle", DEFAULT_MIN_IDLE ) );
        gopc.setMaxTotal( CommonConfig.getInteger( "spring.redis.pool.max-active", DEFAULT_MAX_TOTAL ) );
        gopc.setMaxWaitMillis( CommonConfig.getLong( "spring.redis.pool.max-wait", DEFAULT_MAX_WAIT_MILLIS ) );
        gopc.setEvictorShutdownTimeoutMillis( CommonConfig.getLong( "spring.redis.pool.timeout",
                                                                    DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT_MILLIS ) );

        return LettucePoolingClientConfiguration.builder()
                .poolConfig( gopc )
                .clientOptions( _co )
                .clientResources( _cr )
                .build();
    }

    @Bean
    public RedisConnectionFactory connectionFactory( RedisStandaloneConfiguration _rsc,
                                                     LettucePoolingClientConfiguration _lpcc ){
        int database = CommonConfig.getInteger( "spring.redis.database", 0 );

        LettuceConnectionFactory lcf = new LettuceConnectionFactory( _rsc, _lpcc );
        lcf.setShareNativeConnection( false );
        lcf.setDatabase( database );
        return lcf;
    }

    @Bean
    @ConditionalOnMissingBean( name = "redisTemplate" )
    @Primary
    public < T > RedisTemplate< String, T > redisTemplate( RedisConnectionFactory _rcf ){
        RedisTemplate< String, T > template = new RedisTemplate<>();

        //使用fastjson序列化
        FastJsonRedisSerializer< T > fastJsonRedisSerializer = new FastJsonRedisSerializer();
        // value值的序列化采用fastJsonRedisSerializer
        template.setValueSerializer( fastJsonRedisSerializer );
        template.setHashValueSerializer( fastJsonRedisSerializer );
        // key的序列化采用StringRedisSerializer
        template.setKeySerializer( new StringRedisSerializer() );
        template.setHashKeySerializer( new StringRedisSerializer() );

        template.setConnectionFactory( _rcf );

        // 开启事务
        template.setEnableTransactionSupport( true );
        template.afterPropertiesSet();

        return template;
    }
}
