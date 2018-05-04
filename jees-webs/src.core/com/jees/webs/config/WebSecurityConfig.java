package com.jees.webs.config;

import com.jees.common.CommonConfig;
import com.jees.tool.crypto.MD5Utils;
import com.jees.webs.entity.Template;
import com.jees.webs.entity.SuperMenu;
import com.jees.webs.support.ITemplateService;
import com.jees.webs.support.ISupportEL;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Spring security核心配置项
 * @author aiyoyoyo
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled=true)
@Log4j2
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
    @Autowired
    ITemplateService                templateService;

    @Autowired
    UserDetailsService              userDetailsService;

    @Autowired
    AbsWebConfig                    webConfig;

    /**
     * 登陆成功后的处理
     * @return
     */
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess( HttpServletRequest _request, HttpServletResponse _response, Authentication _auth ) throws IOException, ServletException {
                log.debug( "--登陆成功" );

                _request.getSession().setAttribute( ISupportEL.Session_User_EL, _auth.getPrincipal() );

                sessionRegistry().registerNewSession( _request.getSession().getId(), _auth.getPrincipal() );

                RequestCache requestCache = new HttpSessionRequestCache();

                SavedRequest savedRequest = requestCache.getRequest( _request, _response );
                String url = null;
                if(savedRequest != null) url = savedRequest.getRedirectUrl();
                log.debug( "--登陆后转向：" + url );

                if(url == null) redirectStrategy().sendRedirect( _request, _response, "/" );
                else _response.sendRedirect( url );
            }
        };
    }

    /**
     * 登陆失败后的处理，提示可以通过URL参数或者Session参数获取
     * @return
     */
    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return new AuthenticationFailureHandler(){
            @Override
            public void onAuthenticationFailure( HttpServletRequest _request, HttpServletResponse _response, AuthenticationException _e ) throws IOException, ServletException {
                log.debug( "--登陆失败：" + _e.getMessage() );
                redirectStrategy().sendRedirect( _request, _response,
                        "/" + CommonConfig.getString( "jees.webs.login", "login") + "?" + ISupportEL.Login_Err );
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

    /**
     * 通过栏目包含的权限，来决定所需要的权限
     * @param _hs
     * @throws Exception
     */
    @Override
    protected void configure( HttpSecurity _hs ) throws Exception {
        List<String> tpl_names = templateService.getTemplateNames();
        Template tpl_default = templateService.getDefaultTemplate();

        for( String tpl : tpl_names ) {
            List<SuperMenu> menus = templateService.loadTemplateMenus( tpl );
            boolean is_default = tpl_default.getName().equalsIgnoreCase( tpl );
            boolean tpl_access = CommonConfig.getBoolean( "jees.webs." + tpl + ".access" );
            String  tpl_url    = is_default ? "/" : "/" + tpl + "/";

            log.debug(
                    "--读取各栏目权限：TPL=[" + tpl_url + "], MENU=["+menus.size()+"], TPL=[" + tpl + "], ACCESS=[" + tpl_access + "]" );
            if( tpl_access ) {
                _hs.authorizeRequests().antMatchers( tpl_url ).hasAuthority( ISupportEL.ROLE_SUPERMAN );
                webConfig.getTemplatePages( tpl ).forEach( p -> {
                    Optional<SuperMenu> finder = menus.stream().filter(m -> {
                        String t = m.getTpl();

                        if( templateService.isDefault( t ) ) t = "/";
                        else t += "/";

                        String menu_url = t + m.getUrl();
                        return menu_url.equalsIgnoreCase(p.getUrl() );
                    }).findFirst();

                    if( !finder.isPresent() ) {
                        try {
                            String p_url = p.getUrl();
                            if( !templateService.isDefault( p.getTpl() ) ) p_url = "/" + p_url ;
                            if (!p_url.equalsIgnoreCase("/" + CommonConfig.getString("jees.webs.login", "login"))) {
                                _hs.authorizeRequests().antMatchers(p_url ).hasAuthority(ISupportEL.ROLE_SUPERMAN);
                                log.debug("--未配置的受限访问路径：URL=[" + p_url + "], ROLE=[" + ISupportEL.ROLE_SUPERMAN + "]");
                            }
                        } catch (Exception e) {
                        }
                    }
                } );
            } else _hs.authorizeRequests().antMatchers( tpl_url );

            for( SuperMenu m : menus ){
                tpl_url = is_default ? "/" + m.getUrl() : m.getTpl() + "/" + m.getUrl();
                if( m.isPermit() && !tpl_access ){
                    log.debug( "--访问权限：URL=[" + tpl_url + "], ROLE=[]" );
                    _hs.authorizeRequests().antMatchers( tpl_url );
                }else{
                    @SuppressWarnings("unchecked")
                    List<String> list = m.getRoleNames();
                    list.add( ISupportEL.ROLE_SUPERMAN );
                    String[] roles = new String[list.size()];
                    list.toArray( roles );

                    log.debug( "--访问权限：URL=[" + tpl_url + "], ROLE=" + Arrays.toString( roles )  );
                    _hs.authorizeRequests().antMatchers( tpl_url ).hasAnyAuthority( roles );
                }
            }
        }
        String dwr_url = CommonConfig.getString("jees.webs.dwr.url", "/dwr" );
        _hs.csrf().ignoringAntMatchers( dwr_url + "/**");

        _hs.authorizeRequests()
                .and().formLogin().loginPage( "/" + CommonConfig.getString( "jees.webs.login", "login") )
                .successHandler( successHandler() ).failureHandler( failureHandler() ).permitAll()
                .and().logout().logoutUrl("/" + CommonConfig.getString( "jees.webs.logout", "logout" )).permitAll();

        _hs.sessionManagement().maximumSessions( CommonConfig.getInteger( "jees.webs.maxSession", 1000 ) ).sessionRegistry( sessionRegistry() );

    }

    /**
     * 通过userDetailsService来读取用户账号，密码，权限
     * */
    @Override
    protected void configure( AuthenticationManagerBuilder _auth ) throws Exception {
        _auth.userDetailsService( userDetailsService ).passwordEncoder(new PasswordEncoder() {
            @Override
            public String encode( CharSequence _pwd ) {
                log.debug( "--ENCODE PWD: [" + _pwd + "]");
                String encode = MD5Utils.s_encode( (String) _pwd );
                log.debug( "--ENCODE PWD: [" + encode + "]");
                return encode;
            }
            @Override
            public boolean matches(CharSequence _pwd , String _encode ) {
                String encode_pwd = MD5Utils.s_encode( (String)_pwd );
                log.debug( "--MATCHES PWD: [" + _pwd+ "]->[" + encode_pwd + "]");
                log.debug( "--MATCHES ENCODE: [" + _encode + "]");
                boolean matches = _encode.equals( encode_pwd );
                return matches;
            }
        });
    }
}
