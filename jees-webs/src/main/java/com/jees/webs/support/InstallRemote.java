package com.jees.webs.support;

import com.jees.core.database.config.RedisConfig;
import com.jees.core.database.support.IRedisDao;
import com.jees.tool.utils.JsonUtil;
import com.jees.tool.utils.RandomUtil;
import com.jees.webs.config.InstallConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Service;

import javax.persistence.Id;
import java.io.IOException;

@Log4j2
@Service
@RemoteProxy
public class InstallRemote{

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Test{
        @Id
        int id;

        @Override
        public String toString(){
            return JsonUtil.toString( this );
        }
    }

    @Autowired
    IRedisDao     sDB;
    @Autowired
    InstallConfig installConfig;

    @RemoteMethod
    public InstallConfig.InstallStep step(){
        return installConfig.getInstallStep();
    }

    @RemoteMethod
    public void redis( String _host, int _port, String _password, int _db ) throws Exception{
        try{
            RedisConfig rc = new RedisConfig();

            RedisStandaloneConfiguration rsc = rc.redisStandaloneConfiguration();
            rsc.setHostName( _host );
            rsc.setPort( _port );
            rsc.setPassword( _password );
            rsc.setDatabase( _db );

            LettuceConnectionFactory lcf = ( LettuceConnectionFactory )
                    rc.connectionFactory( rsc, rc.lettucePoolConfig( rc.clientOptions(), rc.clientResources() ) );
            lcf.setDatabase( _db );
            lcf.afterPropertiesSet();

            sDB.changeDatabase( lcf );

            Test t = new Test( RandomUtil.s_random_integer( 100 ) );
            sDB.insert( t );
            sDB.delete( t );

            installConfig.installRedis( _host, _port,_password, _db );
        }catch ( Exception e ){
            installConfig.installFailure( InstallConfig.InstallRedisSucc );
            throw new Exception( "连接失败" );
        }
    }

    @RemoteMethod
    public void admin( String _account, String _password, String _group ) throws Exception {
        try {
            installConfig.installAdmin( _account, _password, _group );
        } catch (IOException e) {
            throw new Exception( "初始化管理员账号失败!" );
        }
    }

    @RemoteMethod
    public void other( int _port, String _path ) throws Exception {
        try {
            installConfig.installServer( _port, _path );
        } catch (IOException e) {
            throw new Exception( "初始化服务器配置失败！" );
        }
    }

    @RemoteMethod
    public String finish(){
        return installConfig.finish();
    }
}
