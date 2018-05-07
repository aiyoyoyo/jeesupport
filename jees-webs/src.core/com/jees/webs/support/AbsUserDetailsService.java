package com.jees.webs.support;

import com.jees.common.CommonConfig;
import com.jees.tool.crypto.MD5Utils;
import com.jees.tool.utils.RandomUtil;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * UserDetailsService抽象实现类，超级账号验证，普通账号验证通过继承该类自定义实现。
 * @author aiyoyoyo
 */
@Log4j2
public abstract class AbsUserDetailsService implements UserDetailsService, ISupportEL {
    private final String    USER_SUPERMAN       = "sUpermAn";

    private String          supermanPassword;

    /**
     * 超级账号在启动时随机生成密码，可以通过日志查询。
     */
    public AbsUserDetailsService(){
        int len = CommonConfig.getInteger( "jees.webs.superpwd" , 6 );
        if( len < 6 ) len = 6;
        if( len > 20 ) len = 20;
        if( supermanPassword == null ) supermanPassword = RandomUtil.s_random_string( len );
        log.debug( "--超级密码：" + supermanPassword );
    }

    /**
     * 这里仅验证超级账号和密码
     */
    @SuppressWarnings("unchecked")
    protected UserDetails checkSuperman(String _username ){
        SuperUser user = new SuperUser();
        if( _username.equals( CommonConfig.getString("jees.webs.superman", USER_SUPERMAN ) ) ){
            log.warn("--使用超级账号登陆。" );

            user.setUsername( _username );
            user.setPassword( MD5Utils.s_encode( supermanPassword ) );
            user.setEnabled( true );
            user.setLocked( false );

            SuperRole role = new SuperRole();
            role.setName( ROLE_SUPERMAN );

            user.addRole( role );
            return user;
        }

        return null;
    }

    protected void build(UserDetails _user){
        User.withUserDetails( _user ).build();
    }
}
