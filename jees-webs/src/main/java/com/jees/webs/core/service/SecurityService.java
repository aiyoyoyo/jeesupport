package com.jees.webs.core.service;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.tool.crypto.MD5Utils;
import com.jees.webs.core.interf.ICodeDefine;
import com.jees.webs.core.interf.ISuperLogin;
import com.jees.webs.core.interf.ISupportEL;
import com.jees.webs.core.struct.ServerMessage;
import com.jees.webs.security.service.VerifyService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class SecurityService {
    public enum SecurityModel{
        NONE,
        LOCAL,
        DATABASE,
        MIXED;
        public static SecurityModel cast( String _val ){
            switch( _val ) {
                case "mixed": return MIXED;
                case "database": return DATABASE;
                case "local": return LOCAL;
                case "none":
                default:
                    return NONE;
            }
        }
    }

    boolean encodePwd;
    boolean cross;
    SecurityModel model;
    @Autowired
    VerifyService verifyService;
    @Autowired
    SupportELService supportELService;

    SessionRegistry sessionRegistry;

    public SecurityService(){
        this.model = SecurityModel.cast( CommonConfig.getString( "jees.webs.security.model", "local" ) );
        this.cross = CommonConfig.getBoolean( "jees.webs.security.cross", false );
        this.encodePwd = CommonConfig.getBoolean( "jees.webs.security.encodePwd", true );
        log.info( "权限配置方案：" + model );
        CommonContextHolder.getBean( VerifyService.class ).initialize( this.model );
    }

    public boolean isEnable(){
        return this.model != SecurityService.SecurityModel.NONE;
    }

    public void setHttpSecurity(HttpSecurity _hs) throws Exception {
        String login_page = "/" + CommonConfig.getString( "jees.webs.security.login", "login" );
        String logout_page = "/" + CommonConfig.getString( "jees.webs.security.logout", "logout" );
        _hs.authorizeRequests()
                .and().formLogin().loginPage( login_page ).loginProcessingUrl( login_page )
                .successHandler( successHandler() ).failureHandler( failureHandler() )
                .permitAll()
                .and().exceptionHandling().accessDeniedHandler( deniedHandler() )
                .and().logout().logoutUrl( logout_page ).permitAll()
                .and().authorizeRequests().anyRequest().access( "@verifyService.validate( request, authentication )" );
    }

    public String encodePwd(String _pwd){
        if( CommonConfig.getBoolean( "jees.webs.security.encodePwd", true ) ){
            _pwd = MD5Utils.s_encode( _pwd );
        }
        return _pwd;
    }

    @Bean
    public SessionRegistry getSessionRegistry(){
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
            _request.getSession().setAttribute( ISupportEL.Message_EL, null );

            getSessionRegistry().registerNewSession( _request.getSession().getId(), _auth.getPrincipal() );
            supportELService.registerLoginSession( _request, _response, _auth );

            // TODO 登录限制，重复登录，账号锁定等在这里判断
            ISuperLogin login_impl = CommonContextHolder.getBean( ISuperLogin.class );
            if( login_impl != null ){
                login_impl.success( _request, _response, _auth );
            }
            // TODO 记录未登录的路径，登录成功后自动跳转
            String cache_url = null;
//            RequestCache requestCache = new HttpSessionRequestCache();
//            SavedRequest savedRequest = requestCache.getRequest( _request, _response );
//            String       err_url      = "/" + CommonConfig.getString( "jees.webs.login", "login" ) + "?" + ISupportEL.Login_Err;
//            if( savedRequest != null && !savedRequest.getRedirectUrl().contains(err_url) ) url = savedRequest.getRedirectUrl();
//            log.debug( "--登陆后转向：" + url );
//
            if( cache_url == null ){// 默认跳转首页
                redirectStrategy().sendRedirect( _request, _response, "/" );
            }else{
                _response.sendRedirect( cache_url );
            }
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
            ISuperLogin login_impl = CommonContextHolder.getBean( ISuperLogin.class );
            if( login_impl != null ){
                login_impl.failure( _request, _response, _e );
            }
            ServerMessage msg = new ServerMessage();
            msg.setCode( ICodeDefine.Login_PasswordInvalid );
            _request.getSession().setAttribute( ISupportEL.Message_EL, msg );
            String login_page = "/" + CommonConfig.getString("jees.webs.login", "login");
            _response.sendRedirect(login_page);
        };
    }

    /**
     * 访问无权限后的处理，重定向至自定义403页面
     */
    @Bean
    public AccessDeniedHandler deniedHandler() {
        return (_request, _response, _e) -> {
            String uri = _request.getRequestURI();
            String login_page = "/" + CommonConfig.getString("jees.webs.login", "login");
            String error_page = "/error";
            if (uri.equals("/")) {
                // 登录页面无权限则重定向至登录页面
                _response.sendRedirect(login_page);
            } else {
                // 其他页面无权限则重定向至自定义错误页面
                _response.sendRedirect(error_page);
            }
        };
    }

    @Bean
    public RedirectStrategy redirectStrategy(){
        return new DefaultRedirectStrategy();
    }
}
