package com.jees.webs.core.service;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.webs.core.abs.AbsSupportEL;
import com.jees.webs.core.interf.ISupportEL;
import com.jees.webs.modals.templates.service.TemplateService;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@SuppressWarnings("unused")
public class SupportELService extends AbsSupportEL {
    /**
     * 请求页面时
     *
     */
    public void onPreHandle(HttpServletRequest _request, HttpServletResponse _response, Object _handler) {
        String app_path = CommonConfig.getString("server.servlet.context-path", "/");
        if (app_path.endsWith("/")) {
            app_path = app_path.substring(0, app_path.length() - 1);
        }
        _request.setAttribute(App_EL, app_path);

        TemplateService templateService = CommonContextHolder.getBean(TemplateService.class);
        Objects.requireNonNull(templateService).setRequestEL(_request);
    }

    /**
     * 有权限访问时，存入用户（SuperUser）信息到session中
     *
     * @see com.jees.webs.entity.SuperUser
     */
    public void registerLoginSession(HttpServletRequest _request, HttpServletResponse _response, Authentication _auth) {
        _request.getSession().setAttribute(ISupportEL.Session_User_EL, _auth.getPrincipal());
    }
}
