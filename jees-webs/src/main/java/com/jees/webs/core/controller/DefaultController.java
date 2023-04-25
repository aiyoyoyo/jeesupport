package com.jees.webs.core.controller;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.webs.core.interf.ISupportEL;
import com.jees.webs.modals.templates.service.TemplateService;
import com.jees.webs.security.interf.IVerifyLogin;
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
 *
 * @author aiyoyoyo
 */
@Log4j2
@Controller
public class DefaultController implements ISupportEL {
    @Autowired
    SessionRegistry sessionRegistry;

    @RequestMapping("/${jees.webs.security.logout:logout}")
    public String logout(HttpServletRequest _request, HttpServletResponse _response) {
        log.debug("--用户登出");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(_request, _response, auth);
        }
        IVerifyLogin login = CommonContextHolder.getBean( IVerifyLogin.class );
        if( login != null ) {
            login.logout( _request, _response );
        }
        sessionRegistry.removeSessionInformation(_request.getSession().getId());
        return "redirect:/";
    }
}
