package com.jees.webs.core.controller;

import com.jees.common.CommonConfig;
import com.jees.webs.core.interf.ISupportEL;
import com.jees.webs.modals.templates.service.TemplateService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 路径访问控制，需配合WebConfig
 * @author aiyoyoyo
 */
@Log4j2
@Controller
public class DefaultController implements ISupportEL{

    @Autowired
    SessionRegistry     sessionRegistry;
    @Autowired
    TemplateService     templateService;

    @RequestMapping( "/${jees.webs.security.logout:logout}" )
    public String logout( HttpServletRequest _request, HttpServletResponse _response ){
        log.debug( "--用户登出" );

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if( auth != null )
            new SecurityContextLogoutHandler().logout( _request, _response, auth );

        sessionRegistry.removeSessionInformation( _request.getSession().getId() );

        return "redirect:/";
    }

    @RequestMapping( "/tpl/{_tpl}" )
    public String changeTemplate( @PathVariable String _tpl,
                                  HttpServletRequest _request,
                                  HttpServletResponse _response ){
        log.debug( "--切换模版 ==> " + _tpl );
        templateService.setDefTemplate( _tpl );
        return "redirect:/";
    }

    /**
     * 自定义的路径会覆盖默认请求路径
     */
    @RequestMapping( "/test" )
    public String test( HttpServletRequest _request, HttpServletResponse _response ){
//        return "default/test2";// 覆盖默认路径
//        return "default/test.html";// 覆盖默认路径
        return templateService.getTemplatePath("/test", _request );
    }

    private void _load_menus(HttpServletRequest _request) {
        if (CommonConfig.getBoolean("jees.jdbs.enable", false)) {
//            iSS.loadUserMenus( _request );
//            iSS.loadUserBreadcrumb( _request );
//            iSS.loadUserActiveMenus( _request );
        } else {
//            verifyService.loadUserMenus( _request );
        }
    }

}
