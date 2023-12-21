package com.jees.webs.security.handler;

import com.jees.common.CommonConfig;
import com.jees.webs.core.interf.ICodeDefine;
import com.jees.webs.core.interf.ISupportEL;
import com.jees.webs.core.struct.ServerMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityAccessDeniedHandler implements AccessDeniedHandler {
    /**
     * 访问无权限后的处理，重定向至自定义403页面
     */
    @Override
    public void handle(HttpServletRequest _request, HttpServletResponse _response, AccessDeniedException _accessDeniedException) throws IOException, ServletException {
        String uri = _request.getRequestURI();
        String error_page = "/error";
        String logout_page = "/" + CommonConfig.getString("jees.webs.logout", "logout");
        if (uri.equals("/")) {
            // 首页页面无权限则重定向至登录页面
            _response.sendRedirect(logout_page);
        } else {
            // 可能超时
            if (_request.getSession().getAttribute(ISupportEL.Session_User_EL) == null) {
                _response.sendRedirect(logout_page);
            } else {
                // 其他页面无权限则重定向至自定义错误页面
                _request.setAttribute(ISupportEL.Message_EL, new ServerMessage(ICodeDefine.User_IsDeny));
                _request.getRequestDispatcher(error_page).forward(_request, _response);
            }
        }
    }
}
