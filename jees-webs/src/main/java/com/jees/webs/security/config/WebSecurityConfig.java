package com.jees.webs.security.config;

import com.jees.common.CommonConfig;
import com.jees.tool.crypto.MD5Utils;
import com.jees.webs.core.interf.ISupportEL;
import com.jees.webs.core.service.ExUserDetailsService;
import com.jees.webs.modals.dwr.config.DwrConfig;
import com.jees.webs.modals.templates.service.TemplateService;
import com.jees.webs.security.service.SecurityService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
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
                .sessionRegistry( sessionRegistry() );

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
                String encode =  ( String ) _pwd;
                if( CommonConfig.getBoolean( "jees.webs.security.encodePwd", true ) ){
                    encode = MD5Utils.s_encode( encode );
                }
                log.debug( "--ENCODE PWD: [" + _pwd + "]->ENCODE: [" + encode + "]" );
                return encode;
            }

            @Override
            public boolean matches( CharSequence _pwd, String _encode ){
                String encode =  ( String ) _pwd;
                if( CommonConfig.getBoolean( "jees.webs.security.encodePwd", true ) ){
                    encode = MD5Utils.s_encode( encode );
                }
                return _encode.equals( encode );
            }
        } );
    }

    @Bean
    public SessionRegistry sessionRegistry(){
        if( sessionRegistry == null ){
            sessionRegistry = new SessionRegistryImpl();
        }
        return sessionRegistry;
    }

    /**
     * 登陆成功后的处理
     *
     * @return 登陆账号后验证结果
     */
    @Bean
    public AuthenticationSuccessHandler successHandler(){
        return (_request, _response, _auth) -> {
            log.debug( "--登陆成功" );

            _request.getSession().setAttribute( ISupportEL.Session_User_EL, _auth.getPrincipal() );
            sessionRegistry().registerNewSession( _request.getSession().getId(), _auth.getPrincipal() );

            RequestCache requestCache = new HttpSessionRequestCache();

            SavedRequest savedRequest = requestCache.getRequest( _request, _response );
            String       url          = null;
            String       err_url      = "/" + CommonConfig.getString( "jees.webs.login", "login" ) + "?" + ISupportEL.Login_Err;
            if( savedRequest != null && !savedRequest.getRedirectUrl().contains(err_url) ) url = savedRequest.getRedirectUrl();
            log.debug( "--登陆后转向：" + url );

            if( url == null ) redirectStrategy().sendRedirect( _request, _response, "/" );
            else _response.sendRedirect( url );
        };
    }

    /**
     * 登陆失败后的处理，提示可以通过URL参数或者Session参数获取
     *
     * @return 验证函数
     */
    @Bean
    public AuthenticationFailureHandler failureHandler(){
        return (_request, _response, _e) -> {
            log.debug( "--登陆失败：" + _e.getMessage() );
            _request.getSession().setAttribute("message", _e.getMessage());
            redirectStrategy().sendRedirect( _request, _response,
                    "/" + CommonConfig.getString( "jees.webs.login", "login" ) + "?" + ISupportEL.Login_Err );
        };
    }

    /**
     * 访问无权限后的处理，重定向至自定义403页面
     */
    @Bean
    public AccessDeniedHandler deniedHandler() {
        return (_request, _response, _e) -> {
            log.debug("--重定向页面：" + _e.getMessage());
            _request.getSession().setAttribute("message", _e.getMessage());
            Object access_msg = _request.getSession().getAttribute("access_msg");
            if (access_msg != null)
                _request.getSession().setAttribute("message", access_msg.toString());
            String uri = _request.getRequestURI();
            String login_page = "/" + CommonConfig.getString("jees.webs.login", "login");
            String l403_page = "/" + CommonConfig.getString("jees.webs.verify.403", "403");

            if (uri.equals("/")) {
                // 登录页面无权限则重定向至登录页面
                _response.sendRedirect(login_page);
            } else {
                // 其他页面无权限则重定向至自定义/403
                _response.sendRedirect(l403_page);
            }
        };
    }

    @Bean
    public RedirectStrategy redirectStrategy(){
        return new DefaultRedirectStrategy();
    }
}