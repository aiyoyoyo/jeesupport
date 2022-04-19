package com.jees.webs.config;

import com.jees.common.CommonConfig;
import com.jees.core.database.support.IRedisDao;
import com.jees.tool.crypto.MD5Utils;
import com.jees.webs.abs.AbsSuperService;
import com.jees.webs.abs.AbsUserDetailsService;
import com.jees.webs.entity.SuperMenu;
import com.jees.webs.entity.Template;
import com.jees.webs.support.ISupportEL;
import com.jees.webs.support.ITemplateService;
import com.jees.webs.support.IVerifyService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Spring security核心配置项
 * @author aiyoyoyo
 */
@Component
@EnableGlobalMethodSecurity(prePostEnabled=true)
@Log4j2
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
    @Autowired
    IVerifyService     verifyService;
    @Autowired
    ITemplateService   templateService;
    @Autowired
    AbsUserDetailsService userDetailsService;
    @Autowired
    InstallConfig      installConfig;
    @Autowired
    IRedisDao          sDB;
    @Autowired
    AbsSuperService    absSS;
    /**
     * 登陆成功后的处理
     *
     * @return 登陆账号后验证结果
     */
    @Bean
    public AuthenticationSuccessHandler successHandler(){
        return new AuthenticationSuccessHandler(){
            @Override
            public void onAuthenticationSuccess( HttpServletRequest _request, HttpServletResponse _response, Authentication _auth ) throws IOException, ServletException{
                log.debug( "--登陆成功" );

                _request.getSession().setAttribute( ISupportEL.Session_User_EL, _auth.getPrincipal() );
                sessionRegistry().registerNewSession( _request.getSession().getId(), _auth.getPrincipal() );

                RequestCache requestCache = new HttpSessionRequestCache();

                SavedRequest savedRequest = requestCache.getRequest( _request, _response );
                String       url          = null;
                if( savedRequest != null ) url = savedRequest.getRedirectUrl();
                log.debug( "--登陆后转向：" + url );

                if( url == null ) redirectStrategy().sendRedirect( _request, _response, "/" );
                else _response.sendRedirect( url );
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
        return new AuthenticationFailureHandler(){
            @Override
            public void onAuthenticationFailure( HttpServletRequest _request, HttpServletResponse _response, AuthenticationException _e ) throws IOException, ServletException{
                log.debug( "--登陆失败：" + _e.getMessage() );
                _request.getSession().setAttribute("message", _e.getMessage());
                redirectStrategy().sendRedirect( _request, _response,
                                                 "/" + CommonConfig.getString( "jees.webs.login", "login" ) + "?" + ISupportEL.Login_Err );
            }
        };
    }

    /**
     * 访问无权限后的处理，重定向至自定义403页面
     */
    @Bean
    public AccessDeniedHandler deniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest _request, HttpServletResponse _response, AccessDeniedException _e) throws IOException, ServletException {
                log.debug("--重定向页面：" + _e.getMessage());
                _request.getSession().setAttribute("message", _e.getMessage());
                String access_msg = _request.getSession().getAttribute("access_msg").toString();
                if (!access_msg.equals(""))
                    _request.getSession().setAttribute("message", access_msg);
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
            }
        };
    }

    @Bean
    public RedirectStrategy redirectStrategy(){
        return new DefaultRedirectStrategy();
    }

    @Bean
    public SessionRegistry sessionRegistry(){
        return new SessionRegistryImpl();
    }

    private void _configure_dwr_( HttpSecurity _hs ) throws Exception{
        String dwr_url  = CommonConfig.getString( "jees.webs.dwr.url", "/dwr" );
        String csrf_url = CommonConfig.getString( "jees.webs.csrf.url", "/csrf" );
        _hs.csrf().ignoringAntMatchers( dwr_url + "/**" );
        _hs.csrf().ignoringAntMatchers( csrf_url + "/**" );

        _hs.headers().frameOptions().disable();
    }

    private void _configure_tpl_notaccess_( HttpSecurity _hs, String _url, Template _tpl, List< SuperMenu > _menus ) throws Exception{
        _hs.authorizeRequests().antMatchers( _url ).hasAnyAuthority( ISupportEL.ROLE_SUPERMAN );
        final String login_page = "/" + CommonConfig.getString( "jees.webs.login", "login" );
        _tpl.getPages().values().forEach( p->{
            boolean finder = false;
            for( SuperMenu m : _menus ){
                String t = m.getTpl();
                if( templateService.isDefault( t ) ) t = "";
                String menu_url = t + m.getUrl();
                if( menu_url.equalsIgnoreCase( p.getUrl() ) ){
                    finder = true;
                    break;
                }
            }

            if( !finder ){
                try{
                    String p_url = p.getUrl();
                    if( !p.getUrl().equalsIgnoreCase( login_page ) ){
                        _hs.authorizeRequests().antMatchers( p_url ).hasAuthority( ISupportEL.ROLE_SUPERMAN );
                        log.debug( "--配置默认访问路径和权限：URL=[" + p_url + "], ROLE=[" + ISupportEL.ROLE_SUPERMAN + "]" );
                    }
                }catch( Exception e ){
                }
            }
        } );
    }

    private void _configure_tpl_( HttpSecurity _hs ) throws Exception{
        List< String > tpl_names   = templateService.getTemplateNames();
        Template       tpl_default = templateService.getDefaultTemplate();

        for( String tpl : tpl_names ){
            List< SuperMenu > menus      = templateService.loadTemplateMenus( tpl );
            boolean           is_default = tpl_default.getName().equalsIgnoreCase( tpl );
            boolean           tpl_access = CommonConfig.getBoolean( "jees.webs." + tpl + ".access" );
            String            tpl_url    = is_default ? "/" : "/" + tpl + "/";

            log.debug(
                    "--读取各栏目权限：TPL=[" + tpl_url + "], MENU=[" + menus.size() + "], TPL=[" + tpl + "], ACCESS=[" + tpl_access + "]" );
            // 没有栏目配置页面添加默认权限
            if( tpl_access ){
                _configure_tpl_notaccess_( _hs, tpl_url, templateService.getTemplate( tpl ), menus );
            }else _hs.authorizeRequests().antMatchers( tpl_url );
            // 按栏目分配权限
            for( SuperMenu m : menus ){
                tpl_url = is_default ? m.getUrl() : m.getTpl() + "/" + m.getUrl();
                if( m.isPermit() && !tpl_access ){
                    log.debug( "--访问权限：URL=[" + tpl_url + "], ROLE=[]" );
                    _hs.authorizeRequests().antMatchers( tpl_url );
                }else{
                    @SuppressWarnings( "unchecked" )
                    List< String > list = absSS.loadMenuRoles( m.getId() );
                    list.add( ISupportEL.ROLE_SUPERMAN );
                    String[] roles = new String[list.size()];
                    list.toArray( roles );

                    log.debug( "--访问权限：URL=[" + tpl_url + "], ROLE=" + Arrays.toString( roles ) );
                    _hs.authorizeRequests().antMatchers( tpl_url ).hasAnyAuthority( roles );
                }
            }
        }
    }

    /**
     * 通过权限配置文件来进行请求的过滤
     *
     * @param _hs 权限管理器
     * @throws Exception 错误
     */
    private void _configure_base_(HttpSecurity _hs) throws Exception {
        String login_page = "/" + CommonConfig.getString("jees.webs.login", "login");
        String logout_page = "/" + CommonConfig.getString("jees.webs.logout", "logout");
        String l403_page = "/" + CommonConfig.getString("jees.webs.verify.403", "403");

        // 遍历模版放行模版下的资源
        List<String> tpl_names = templateService.getTemplateNames();
        for (String tpl : tpl_names) {
            String tpl_assets = "/" + CommonConfig.getString("jees.webs." + tpl + ".assets", "assets") + "/**";
            String tpl_url = "/" + tpl;
            log.debug("--模板资源放行：TPL=[" + tpl_url + "], PAGE=[" + login_page + ", " + logout_page + ", " + l403_page + "]");
            _hs.authorizeRequests()
                    .antMatchers(
                            tpl_url + tpl_assets,
                            "/dwr/**",
                            "/**/*.ico").permitAll()
                    .antMatchers(
                            tpl_url + login_page,
                            tpl_url + logout_page,
                            tpl_url + l403_page).permitAll();
        }
        _hs.authorizeRequests().antMatchers(login_page, logout_page, l403_page).permitAll();
        _hs.authorizeRequests().anyRequest().access(
                "@accessImpl.hasPath(request, authentication) and " +
                        "@accessImpl.hasBlackIP(request, authentication) and " +
                        "@accessImpl.hasBlackUser(request, authentication)"
        );
    }

    private void _configure_login_( HttpSecurity _hs ) throws Exception{
        String login_page = "/" + CommonConfig.getString( "jees.webs.login", "login" );
        String logout_page = "/" + CommonConfig.getString( "jees.webs.logout", "logout" );
        _hs.authorizeRequests()
                .and().formLogin().loginPage( login_page ).loginProcessingUrl( login_page)
                .successHandler( successHandler() ).failureHandler( failureHandler() ).permitAll()
                .and().logout().logoutUrl( logout_page ).permitAll()
                .and().exceptionHandling().accessDeniedHandler( deniedHandler() );
        _hs.csrf().disable();
    }

    /**
     * 通过栏目包含的权限，来决定所需要的权限
     *
     * @param _hs 权限管理器
     * @throws Exception 错误
     */
    @Override
    protected void configure( HttpSecurity _hs ) throws Exception{
        verifyService.initialize();
        sDB.initialize();

        // 配置文件权限认证启用
        if ( CommonConfig.getBoolean( "jees.webs.verify.enable", false ) ) {
            _configure_base_(_hs);
        } else {
            if( !installConfig.isInstalled() ){
                _hs.authorizeRequests().antMatchers( "/**" ).permitAll();
                _configure_dwr_( _hs );
                return;
            }
            _configure_tpl_( _hs );
        }
        _configure_dwr_(_hs);
        _configure_login_(_hs);

        // 其他
        if( CommonConfig.getBoolean( "jees.webs.header.frameOptions", false ) )
            _hs.headers().frameOptions().disable();

        _hs.sessionManagement().maximumSessions( CommonConfig.getInteger( "jees.webs.maxSession", 1000 ) ).sessionRegistry( sessionRegistry() );

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
                if( CommonConfig.getBoolean( "jees.webs.encodePwd", true ) ){
                    encode = MD5Utils.s_encode( encode );
                }
                log.debug( "--ENCODE PWD: [" + _pwd + "]->ENCODE: [" + encode + "]" );
                return encode;
            }

            @Override
            public boolean matches( CharSequence _pwd, String _encode ){
                String encode =  ( String ) _pwd;
                if( CommonConfig.getBoolean( "jees.webs.encodePwd", true ) ){
                    encode = MD5Utils.s_encode( encode );
                }
                return _encode.equals( encode );
            }
        } );
    }
}