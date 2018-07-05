package com.jees.webs.support;

import com.jees.webs.entity.SuperMenu;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

public interface ISuperService {
    <M extends SuperMenu >  List< M > loadMenus();
    <U extends SuperUser >  List< U > loadUsers();
    <R extends SuperRole >  List< R > loadRoles();

    <M extends SuperMenu > M loadMenu( Serializable _id );
    <U extends SuperUser > U loadUser( Serializable _id );
    <R extends SuperRole > R loadRole( Serializable _id );

    <M extends SuperMenu > void saveMenu( M _menu );
    <U extends SuperUser > void saveUser( U _user );
    <R extends SuperRole > void saveRole( R _role );

    void loadUserMenus( HttpServletRequest _request );

    void loadUserBreadcrumb( HttpServletRequest _request );

    void loadUserActiveMenus( HttpServletRequest request );
}
