package com.jees.webs.security.handler;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.webs.security.interf.IVerifyLogin;
import com.jees.webs.security.service.SecurityManagerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Log4j2
public class SecurityLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest _request, HttpServletResponse _response, Authentication _authentication) throws IOException, ServletException {
        log.debug("--用户登出");
        IVerifyLogin login = CommonContextHolder.getBean( IVerifyLogin.class );
        if( login != null ) {
            login.logout( _request, _response );
        }
        SecurityManagerService service = CommonContextHolder.getBean( SecurityManagerService.class );
        Objects.requireNonNull(service).getSessionRegistry().removeSessionInformation(_request.getSession().getId());

        // 解决登出不跳转的问题
        _response.sendRedirect("/" + CommonConfig.get("jees.web.security.login", "login"));
    }
}
