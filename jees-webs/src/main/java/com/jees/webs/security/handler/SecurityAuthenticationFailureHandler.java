package com.jees.webs.security.handler;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.webs.core.interf.ICodeDefine;
import com.jees.webs.core.interf.ISupportEL;
import com.jees.webs.core.struct.ServerMessage;
import com.jees.webs.security.exception.RequestException;
import com.jees.webs.security.interf.IVerifyLogin;
import com.jees.webs.security.service.SecurityManagerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
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
public class SecurityAuthenticationFailureHandler implements AuthenticationFailureHandler {
    /**
     * 登陆失败后的处理，提示可以通过URL参数或者Session参数获取
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest _request, HttpServletResponse _response, AuthenticationException _exception) throws IOException, ServletException {
        IVerifyLogin login_impl = CommonContextHolder.getBean(IVerifyLogin.class);
        if (login_impl != null) {
            login_impl.failure(_request, _response, _exception);
        }
        ServerMessage msg;
        if (_exception instanceof RequestException) {
            msg = ((RequestException) _exception).getServerMessage();
        } else {
            msg = new ServerMessage();
            msg.setCode(ICodeDefine.ErrorCode);
        }

        String login_page = "/" + CommonConfig.getString("jees.webs.login", "login");
        _request.getSession().setAttribute(ISupportEL.Message_EL, msg);
        _response.sendRedirect(login_page);
    }
}
