package com.jees.webs.security.interf;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 应用自定义登录接口
 * @Package: com.jees.webs.core.interf
 * @ClassName: ISuperLogin
 * @Author: 刘甜
 * @Date: 2023/2/6 13:30
 * @Version: 1.0
 */
public interface IVerifyLogin {
    /**
     * 登录成功的自定义处理
     *
     * @param _request
     * @param _response
     * @param _auth
     */
    void success(HttpServletRequest _request, HttpServletResponse _response, Authentication _auth);

    /**
     * 登录失败的自定义处理
     *
     * @param _request
     * @param _response
     * @param _ex
     */
    void failure(HttpServletRequest _request, HttpServletResponse _response, AuthenticationException _ex);

    /**
     * 自定义登录密码校验，需要开启
     *
     * @param _username
     * @param _pwd
     * @return
     */
    boolean matches(String _username, CharSequence _pwd);

    void logout( HttpServletRequest _request, HttpServletResponse _response );
}
