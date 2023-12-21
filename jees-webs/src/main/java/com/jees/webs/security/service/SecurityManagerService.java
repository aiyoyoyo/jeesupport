package com.jees.webs.security.service;

import com.jees.webs.core.service.SupportELService;
import com.jees.webs.security.handler.SecurityAccessDeniedHandler;
import com.jees.webs.security.handler.SecurityAuthenticationFailureHandler;
import com.jees.webs.security.handler.SecurityAuthenticationSuccessHandler;
import com.jees.webs.security.handler.SecurityLogoutSuccessHandler;
import lombok.Getter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

@Service
@Getter
public class SecurityManagerService {
    SessionRegistry sessionRegistry = new SessionRegistryImpl();
    SupportELService supportELService = new SupportELService();
    RedirectStrategy defaultRedirectStrategy = new DefaultRedirectStrategy();
    LogoutSuccessHandler securityLogoutSuccessHandler = new SecurityLogoutSuccessHandler();
    AuthenticationSuccessHandler securityAuthenticationSuccessHandler = new SecurityAuthenticationSuccessHandler();
    AuthenticationFailureHandler securityAuthenticationFailureHandler = new SecurityAuthenticationFailureHandler();
    AccessDeniedHandler accessDeniedHandler = new SecurityAccessDeniedHandler();
}
