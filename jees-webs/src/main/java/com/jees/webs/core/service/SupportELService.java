package com.jees.webs.core.service;

import com.jees.common.CommonConfig;
import com.jees.webs.core.abs.AbsSupportEL;
import com.jees.webs.core.interf.ISupportEL;
import com.jees.webs.modals.templates.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class SupportELService extends AbsSupportEL {
    @Autowired
    TemplateService templateService;

    /**
     * 请求页面时
     * @param _request
     * @param _response
     * @param _handler
     */
    public void onPreHandle(HttpServletRequest _request, HttpServletResponse _response, Object _handler) {
        String app_path = CommonConfig.getString( "server.servlet.context-path", "/" );
        if( app_path.endsWith( "/" ) ){
            app_path = app_path.substring( 0, app_path.length() - 1 );
        }
        _request.setAttribute( App_EL, app_path );

        templateService.setRequestEL( _request );

//        session.setAttribute( Session_Templates_EL, templates.values() );


//        log.debug( "Load template el: app=[" + app_path + "]" );
//        _request.setAttribute( Assets_Current_EL, app_path + "/" + template.getName() + "/" + template.getAssets() );
//        _request.setAttribute( App_EL, app_path );
    }

    /**
     * 有权限访问时，存入用户（SuperUser）信息到session中
     * @param _request
     * @see com.jees.webs.entity.SuperUser
     */
    public void registerLoginSession(HttpServletRequest _request, HttpServletResponse _response, Authentication _auth) {
        _request.getSession().setAttribute( ISupportEL.Session_User_EL, _auth.getPrincipal() );
    }
}
