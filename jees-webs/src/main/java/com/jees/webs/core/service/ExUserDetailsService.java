package com.jees.webs.core.service;

import com.jees.common.CommonConfig;
import com.jees.tool.crypto.MD5Utils;
import com.jees.tool.utils.FileUtil;
import com.jees.tool.utils.RandomUtil;
import com.jees.webs.core.interf.ISupportEL;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * UserDetailsService抽象实现类，超级账号验证，普通账号验证通过继承该类自定义实现。
 * @author aiyoyoyo
 */
@Log4j2
@Service
public class ExUserDetailsService<U extends SuperUser> implements UserDetailsService, ISupportEL{
    private final String USER_SUPERMAN = "sUpermAn";
    private       String supermanPassword;

    /**
     * 超级账号在启动时随机生成密码，可以通过日志查询。
     */
    public ExUserDetailsService(){
        int len = CommonConfig.getInteger( "jees.webs.security.superpwd", 6 );
        if( len < 6 ) len = 6;
        if( len > 20 ) len = 20;
        if( supermanPassword == null ){
            supermanPassword = RandomUtil.s_random_string( len );
            log.debug( "--超级密码：" + supermanPassword );
            try {
                FileUtil.write( supermanPassword, "classpath:.pw" , false );
            } catch (IOException e) {
            }
            if( CommonConfig.getBoolean( "jees.webs.security.encodePwd", true ) ){
                supermanPassword = MD5Utils.s_encode( supermanPassword );
            }
        }
    }

    /**
     * 这里仅验证超级账号和密码
     * @param _username  用户名
     * @return 用户信息
     */
    @SuppressWarnings( "unchecked" )
    protected UserDetails checkSuperman( String _username ){
        SuperUser user = new SuperUser();
        if( _username.equals( CommonConfig.getString( "jees.webs.security.superman", USER_SUPERMAN ) ) ){
            log.warn( "--使用超级账号登陆。" );
            user.setId( 0 );
            user.setUsername( _username );
            user.setPassword( supermanPassword );
            user.setEnabled( true );
            user.setLocked( false );

            SuperRole role = new SuperRole();
            role.setName( ROLE_SUPERMAN );

            user.addRole( role );
            return user;
        }

        return null;
    }

    public UserDetails loadUserByUsername( String _username ) throws UsernameNotFoundException{
        UserDetails user = checkSuperman(_username);
        if (CommonConfig.getBoolean("jees.jdbs.enable", false)) {
            // redis中查找用户
            if (user == null) user = findUserByUsername(_username);
        } else {
            // 配置文件中查找用户
//            if (user == null) user = absVS.findUserByUsername(_username);
        }
        log.debug("--验证登陆用户信息：U=[" + _username + "] P=[" + user.getPassword() + "]");
        build(user);
        log.debug("--用户信息：U=[" + user + "]");
        return user;
    }

    protected U findUserByUsername( String _username ){
        log.debug( "--查找登陆用户信息：U=[" + _username + "]" );
//        List< U > list = sDB.findByEquals( "username", _username, superClass() );
//        if( list.size() == 1 ){
//            U u = list.get( 0 );
////            absSS.loadUserRoles( u );
//            return u;
//        }
        throw new UsernameNotFoundException( "用户不存在" );
    }

    protected void build( UserDetails _user ){
        User.withUserDetails( _user ).build();
    }

    protected Class< U > superClass(){
        return null;
    }
}
