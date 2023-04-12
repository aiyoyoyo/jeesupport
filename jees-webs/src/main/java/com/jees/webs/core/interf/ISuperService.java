package com.jees.webs.core.interf;

import com.jees.webs.entity.SuperMenu;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;

import javax.servlet.http.HttpServletRequest;

public interface ISuperService<M extends SuperMenu, U extends SuperUser, R extends SuperRole> {

    void initialize();

//    List< M > loadMenus();
//
//    List< U > loadUsers();
//
//    List< R > loadRoles();
//
//    M loadMenu( Serializable _id );
//
//    U loadUser( Serializable _id );
//
//    R loadRole( Serializable _id );
//
//    void saveMenu( M _menu );
//
//    void saveUser( U _user );
//
//    void saveRole( R _role );

    void loadUserMenus(HttpServletRequest _request);

    void loadUserBreadcrumb(HttpServletRequest _request);

    void loadUserActiveMenus(HttpServletRequest request);

    Class<M> getExMenuClass();

    Class<U> getExUserClass();

    Class<R> getExRoleClass();

}
