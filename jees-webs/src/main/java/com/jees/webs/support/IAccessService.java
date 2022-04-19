package com.jees.webs.support;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface IAccessService {

    boolean hasPath(HttpServletRequest _request, Authentication _auth);

    boolean hasBlackIP(HttpServletRequest _request, Authentication _auth);

    boolean hasBlackUser(HttpServletRequest _request, Authentication _auth);

}
