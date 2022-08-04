package com.jees.webs.support;

import com.jees.webs.entity.SuperMenu;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;

public interface IVerifyService<M extends SuperMenu,U extends SuperUser,R extends SuperRole> {

    void initialize();

    UserDetails findUserByUsername(String _username);

    void loadUserMenus(HttpServletRequest _request);

}
