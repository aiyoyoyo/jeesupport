package com.jees.webs.security.config;

import com.jees.common.CommonConfig;
import com.jees.webs.core.service.ExUserDetailsService;
import com.jees.webs.core.service.SupportELService;
import com.jees.webs.modals.dwr.config.DwrConfig;
import com.jees.webs.modals.templates.service.TemplateService;
import com.jees.webs.security.service.SecurityService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Spring security核心配置项
 * @author aiyoyoyo
 */
@Component
@EnableGlobalMethodSecurity(prePostEnabled=true)
@Log4j2
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
    @Autowired
    ExUserDetailsService userDetailsService;
    @Autowired
    DwrConfig           dwrConfig;
    @Autowired
    SecurityService     securityService;
    @Autowired
    TemplateService     templateService;
    @Autowired
    SupportELService    supportELService;

    @Autowired
    SessionRegistry sessionRegistry;
    /**
     * 通过栏目包含的权限，来决定所需要的权限
     *
     * @param _hs 权限管理器
     * @throws Exception 错误
     */
    @Override
    protected void configure( HttpSecurity _hs ) throws Exception{
        if( CommonConfig.getBoolean( "jees.webs.security.header.frameOptions", false ) ) {
            _hs.headers().frameOptions().disable();
        }
        _hs.sessionManagement().maximumSessions( CommonConfig.getInteger( "jees.webs.maxSession", 1000 ) )
                .sessionRegistry( sessionRegistry );

        dwrConfig.setHttpSecurity( _hs );
        if( securityService.isEnable() ){
            templateService.setHttpSecurity( _hs );
            securityService.setHttpSecurity( _hs );
        }else{
            log.warn( "服务器未启用安全访问，默认可以访问所有页面文件！" );
            _hs.authorizeRequests().antMatchers("/**").permitAll();
        }
    }

    /**
     * 通过userDetailsService来读取用户账号，密码，权限
     * @param _auth 权限
     */
    @Override
    protected void configure( AuthenticationManagerBuilder _auth ) throws Exception{
        _auth.userDetailsService( userDetailsService ).passwordEncoder( new PasswordEncoder(){
            @Override
            public String encode( CharSequence _pwd ){
                return securityService.encodePwd((String) _pwd);
            }

            @Override
            public boolean matches( CharSequence _pwd, String _encode ){
                return _encode.equals( securityService.encodePwd((String) _pwd) );
            }
        } );
    }
}