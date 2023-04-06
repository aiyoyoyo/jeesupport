package com.jees.webs.core.service;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.tool.crypto.MD5Utils;
import com.jees.tool.utils.FileUtil;
import com.jees.tool.utils.RandomUtil;
import com.jees.webs.core.interf.ICodeDefine;
import com.jees.webs.core.interf.ISupportEL;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;
import com.jees.webs.security.exception.RequestException;
import com.jees.webs.security.interf.IVerifyUser;
import com.jees.webs.security.service.VerifyService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @Autowired
    VerifyService verifyService;

    /**
     * 超级账号在启动时随机生成密码，可以通过日志查询。
     */
    public ExUserDetailsService(){
        int len = CommonConfig.getInteger( "jees.webs.security.superPwd", 6 );
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
    protected SuperUser checkSuperman( String _username ){
        SuperUser user = new SuperUser();
        if( _username.equals( CommonConfig.getString( "jees.webs.security.superman", USER_SUPERMAN ) ) ){
            log.warn( "--使用超级账号登陆。" );
            user.setId( "superman" );
            user.setUsername( _username );
            user.setPassword( supermanPassword );
            user.setEnabled( true );
            user.setAccountNonExpired( true );
            user.setCredentialsNonExpired( true );
            user.setAccountNonLocked( true );

            SuperRole role = new SuperRole();
            role.setName( ROLE_SUPERMAN );

            user.addRole( role.getName() );
            return user;
        }

        return null;
    }
    @Override
    public UserDetails loadUserByUsername( String _username ) throws UsernameNotFoundException{
        SuperUser user = checkSuperman(_username);
        if( user == null ){
            // 一般用户入库
            user = verifyService.findUserByUsername( _username );
        }
        if( user == null ){
            log.debug( "用户[" + _username + "]不存在！" );
            throw new RequestException(ICodeDefine.Login_NotFoundUser);
        }
        log.debug("--验证登陆用户信息：U=[" + _username + "] P=[" + user.getPassword() + "]");
        return user;
    }

    public void build( UserDetails _user ){
        SuperUser user = (SuperUser) _user;
        User.withUserDetails( _user ).roles( user.getRoles() ).build();

        // TIPS 用户默认为禁用，通过实现自定义属性加载，来控制账号有效状态
        IVerifyUser user_impl = CommonContextHolder.getBean( IVerifyUser.class );
        if( user_impl != null ){
            user_impl.loadUser( user );
        }
    }

    protected Class< U > superClass(){
        return null;
    }
}
