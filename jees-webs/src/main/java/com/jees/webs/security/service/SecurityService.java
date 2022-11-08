package com.jees.webs.security.service;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.tool.crypto.MD5Utils;
import com.jees.webs.core.interf.ISupportEL;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

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
    VerifyModelService verifyModelService;

    SessionRegistry sessionRegistry;

    public SecurityService(){
        this.model = SecurityModel.cast( CommonConfig.getString( "jees.webs.security.model", "local" ) );
        this.cross = CommonConfig.getBoolean( "jees.webs.security.cross", false );
        this.encodePwd = CommonConfig.getBoolean( "jees.webs.security.encodePwd", true );
        log.info( "权限配置方案：" + model );
        CommonContextHolder.getBean( VerifyModelService.class ).initialize( this.model );
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
                .and().authorizeRequests().anyRequest().access( "@securityService.validate( request, authentication )" );
    }

    public String encodePwd(String _pwd){
        if( CommonConfig.getBoolean( "jees.webs.security.encodePwd", true ) ){
            _pwd = MD5Utils.s_encode( _pwd );
        }
        return _pwd;
    }

    /**
     * 验证用户页面授权
     * @param request
     * @param authentication
     * @return
     */
    public boolean validate(HttpServletRequest request, Authentication authentication){
        String uri = request.getRequestURI();
        Object principal = authentication.getPrincipal();
        log.debug( "验证用户访问权限，访问地址=" + uri + "， 用户=" + principal );
        boolean result = verifyModelService.validateBlack( request, authentication );
        if( result ){
            result = verifyModelService.validateAdministrator(request, authentication);
        }else {
            if (principal == null || ISupportEL.ROLE_ANONYMOUS.equals(principal)) {
                // 判断是否访问匿名页面
                boolean is_anonymous = verifyModelService.validateAnonymous(uri);
                if (is_anonymous) {
                    result = true;
                }
            }
            if (!result) {
                result = verifyModelService.velidateRequest(request, authentication);
            }
        }

        log.debug( "验证结果：" + result );
        return result;
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
            getSessionRegistry().registerNewSession( _request.getSession().getId(), _auth.getPrincipal() );
//            supportELService.onSuccessHandler( _request, _response, _auth );

//            RequestCache requestCache = new HttpSessionRequestCache();
//            SavedRequest savedRequest = requestCache.getRequest( _request, _response );
            String       url          = null;
//            String       err_url      = "/" + CommonConfig.getString( "jees.webs.login", "login" ) + "?" + ISupportEL.Login_Err;
//            if( savedRequest != null && !savedRequest.getRedirectUrl().contains(err_url) ) url = savedRequest.getRedirectUrl();
//            log.debug( "--登陆后转向：" + url );
//
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
