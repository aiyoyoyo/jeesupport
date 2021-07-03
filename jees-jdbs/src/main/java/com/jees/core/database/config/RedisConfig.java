package com.jees.core.database.config;

import com.jees.common.CommonConfig;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

import static org.apache.commons.pool2.impl.BaseObjectPoolConfig.DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT_MILLIS;
import static org.apache.commons.pool2.impl.BaseObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS;
import static org.apache.commons.pool2.impl.GenericObjectPoolConfig.*;

@Configuration
@EnableConfigurationProperties( RedisProperties.class)
public class RedisConfig{

    @Bean( destroyMethod = "shutdown" )
    public ClientResources clientResources(){
        return DefaultClientResources.create();
    }

    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration(){
        String host = CommonConfig.getString( "spring.redis.host", "127.0.0.1" );
        int port = CommonConfig.getInteger( "spring.redis.port", 6379 );

        RedisStandaloneConfiguration rsc = new RedisStandaloneConfiguration(host, port);

        String pwd = CommonConfig.getString( "spring.redis.password", "" );
        if( !pwd.isEmpty() ){
            rsc.setPassword( pwd );
        }
        return rsc;
    }

    @Bean
    public ClientOptions clientOptions(){
        return ClientOptions.builder()
                .disconnectedBehavior( ClientOptions.DisconnectedBehavior.REJECT_COMMANDS )
                .autoReconnect( true )
                .build();
    }

    @Bean
    public LettucePoolingClientConfiguration lettucePoolConfig( ClientOptions _co, ClientResources _cr ){
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
        int database = CommonConfig.getInteger( "spring.redis.com.jees.core.database", 0 );

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
        // value值的序列化采用fastJsonRedisSerializer
        template.setValueSerializer( new FastJsonRedisSerializer<T>() );
        template.setHashValueSerializer( new FastJsonRedisSerializer<T>() );
        // key的序列化采用StringRedisSerializer
        template.setKeySerializer( new FastJsonRedisSerializer<T>() );
        template.setHashKeySerializer( new FastJsonRedisSerializer<T>() );

        template.setConnectionFactory( _rcf );
        // 开启事务
        template.setEnableTransactionSupport( true );
        template.afterPropertiesSet();

        return template;
    }
}
