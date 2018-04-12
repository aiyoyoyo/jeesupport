package com.jees.webs.config;

import com.jees.common.CommonConfig;
import com.jees.common.CommonLogger;
import com.jees.webs.support.ITemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 路径访问控制，需配合WebConfig
 * @author aiyoyoyo
 */
public abstract class AbsController {

    @Autowired
    SessionRegistry     sessionRegistry;
    @Autowired
    ITemplateService    templateService;

    @RequestMapping( "/${jees.webs.logout}")
    String logout( HttpServletRequest _request, HttpServletResponse _response ) throws IOException {
        CommonLogger.getLogger( this.getClass() ).debug( "--用户登出" );

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null)
            new SecurityContextLogoutHandler().logout( _request, _response, auth);

        sessionRegistry.removeSessionInformation( _request.getSession().getId() );

        return "redirect:/";
    }

    /**
     * 拦截器，只有AbsControllerConfig被继承，且被扫描到后生效。
     * @return
     */
    @Bean
    public HandlerInterceptor handlerInterceptor(){
        CommonLogger.getLogger( this.getClass() ).debug( "--应用AbsControllerConfing拦截器。");
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle( HttpServletRequest _request, HttpServletResponse _response, Object _handler ) throws Exception {
                String uri = _request.getRequestURI();
                if( !uri.equals("/") && uri.startsWith( "/" ) ) uri = uri.substring( 1 );
                int idx = uri.indexOf( "/" );
                if( idx != -1 ) uri = uri.substring( 0, idx );

                templateService.loadTemplate( uri, _request );

                return true;
            }
        };
    }
}
