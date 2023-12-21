package com.jees.webs.security.handler;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.webs.core.interf.ISupportEL;
import com.jees.webs.security.interf.IVerifyLogin;
import com.jees.webs.security.service.SecurityManagerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Log4j2
public class SecurityAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    /**
     * 登陆成功后的处理
     * 登陆账号后验证结果
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest _request, HttpServletResponse _response, Authentication _authentication) throws IOException, ServletException {
        log.debug("--登陆成功");
        _request.getSession().setAttribute(ISupportEL.Message_EL, null);

        SecurityManagerService service = CommonContextHolder.getBean(SecurityManagerService.class);
        Objects.requireNonNull(service).getSessionRegistry().registerNewSession(_request.getSession().getId(), _authentication.getPrincipal());
        service.getSupportELService().registerLoginSession(_request, _response, _authentication);

        // TODO 登录限制，重复登录，账号锁定等在这里判断
        IVerifyLogin login_impl = CommonContextHolder.getBean(IVerifyLogin.class);
        if (login_impl != null) {
            login_impl.success(_request, _response, _authentication);
        }
        // TODO 记录未登录的路径，登录成功后自动跳转
        String redirect_url = null;
        RequestCache requestCache = new HttpSessionRequestCache();
        SavedRequest savedRequest = requestCache.getRequest(_request, _response);
        String err_url = "/error";
        if (savedRequest != null && !savedRequest.getRedirectUrl().contains(err_url)) {
            redirect_url = savedRequest.getRedirectUrl();
            log.debug("--登陆后转向：" + redirect_url);
        }
        if (redirect_url == null) {// 默认跳转首页
            service.getDefaultRedirectStrategy().sendRedirect(_request, _response, CommonConfig.get("jees.webs.security.index", "/"));
        } else {
            _response.sendRedirect(redirect_url);
        }
    }
}
