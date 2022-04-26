package com.jees.webs.support;

import com.jees.webs.entity.SuperMenu;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public interface IVerifyService<M extends SuperMenu,U extends SuperUser,R extends SuperRole> {

    void initialize();

    UserDetails findUserByUsername(String _username);

    List< M > mergeMenus();

    List< U > mergeUsers();

    List< R > mergeRoles();

    void loadUserMenus(HttpServletRequest _request);

}
