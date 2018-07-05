package com.jees.test.service;

import com.jees.test.entity.Menu;
import com.jees.test.entity.Role;
import com.jees.test.entity.User;
import com.jees.webs.entity.SuperMenu;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;
import com.jees.webs.support.ISuperService;
import com.jees.webs.support.ISupportEL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

@Service
public class SuperServiceImpl implements ISuperService, ISupportEL {
    @Autowired
    DaoServiceImpl      daoService;

    @Override
    public List< Menu > loadMenus() {
        List<Menu> list = daoService.select( Menu.class );
        list.forEach( m -> m.getRoles().size() );
        return list;
    }
    @Override
    public List< User > loadUsers() {
        List<User> list = daoService.select( User.class );
        return list;
    }

    @Override
    public List< Role > loadRoles() {
        List<Role> list = daoService.select( Role.class );
        list.forEach( r -> r.getMenus().size() );
        return list;
    }

    @Override
    public < M extends SuperMenu > M loadMenu( Serializable _id ) {
        return (M)daoService.selectById( DaoServiceImpl.DB_Default, Menu.class, _id );
    }

    @Override
    public < U extends SuperUser > U loadUser( Serializable _id ) {
        return (U)daoService.selectById( DaoServiceImpl.DB_Default, User.class, _id );
    }

    @Override
    public < R extends SuperRole > R loadRole( Serializable _id ) {
        return (R)daoService.selectById( DaoServiceImpl.DB_Default, Role.class, _id );
    }



    @Override
    public <M extends SuperMenu > void saveMenu( M _menu ) {
        daoService.update( DaoServiceImpl.DB_Default, _menu );
        daoService.commit();
    }

    @Override
    public < U extends SuperUser > void saveUser( U _user ) {
        daoService.update( DaoServiceImpl.DB_Default, _user );
        daoService.commit();
    }

    @Override
    public < R extends SuperRole > void saveRole( R _role ) {
        daoService.update( DaoServiceImpl.DB_Default, _role );
        daoService.commit();
    }

    public void loadUserMenus( HttpServletRequest _request ) {
        SuperUser user = ( SuperUser ) _request.getSession().getAttribute( Session_User_EL );
        if( user == null ) return;

        Map< String, SuperMenu> user_menus = ( Map ) _request.getSession().getAttribute( Session_Menus_EL );
        if( user_menus == null ){
            user_menus = new HashMap<>();
            Set<SuperMenu> sets1 = user.getMenus();
            Set<SuperMenu> sets2 = new HashSet<>( sets1 );

            for( SuperMenu m2 : sets2 ){
                for( SuperMenu m1 : sets1 ){
                    if( m2.getParentId() == ( Integer ) m1.getId() ){
                        m1.addMenu( m2 );
                        break;
                    }
                    if( m1.isRoot() )
                        user_menus.putIfAbsent( m1.getUrl(), m1 );
                }
            }
        }

        _request.getSession().setAttribute( Session_Menus_EL, user_menus );
    }

    public void loadUserBreadcrumb( HttpServletRequest _request ) {
        String uri = _request.getRequestURI();
        if( uri.isEmpty() ) uri = "/";
        _request.setAttribute( Request_Page_EL, uri );

        List<SuperMenu> breads = ( List< SuperMenu > ) _request.getSession().getAttribute( Session_Breadcrumb_EL );
        if( breads == null ) breads = new ArrayList<>();
        _request.getSession().setAttribute( Session_Breadcrumb_EL, breads );

        Iterator< SuperMenu > it = breads.iterator();

        while( it.hasNext() ){
            if( it.next().getUrl().equals( uri ) )
                it.remove();
        }

        SuperUser user = ( SuperUser ) _request.getSession().getAttribute( Session_User_EL );
        if( user == null ) return;

        String menu_uri = uri;
        Optional<Menu> finder = user.getMenus().stream().filter( m -> ( ( Menu ) m ).getUrl().equals( menu_uri ) ).findFirst();

        if( finder.isPresent() ){
            SuperMenu menu = finder.get();
            breads.add( menu );
            _request.setAttribute( Request_Menu_EL, menu );
        }

        if( breads.size() > 3 ) breads.remove( 0 );
    }

    public void loadUserActiveMenus( HttpServletRequest _request ){
        String uri = ( String ) _request.getAttribute( Request_Page_EL );

        SuperUser user = ( SuperUser ) _request.getSession().getAttribute( Session_User_EL );
        if( user == null ) return;

        Optional<Menu> finder = user.getMenus().stream().filter( m -> ( ( Menu ) m ).getUrl().equals( uri ) ).findFirst();

        SuperMenu menu = null;
        if( finder.isPresent() ) menu = finder.get();
        if( menu == null ) return;

        Set<String> actives = new HashSet<>();
        actives.add( menu.getUrl() );

        SuperMenu parent_menu = null;
        if( menu.getParentId() != 0 ){
            int pid = menu.getParentId();
            finder = user.getMenus().stream().filter( m -> ( ( Menu ) m ).getId().intValue() == pid ).findFirst();
            if( finder.isPresent() ) {
                parent_menu = finder.get();
                actives.add( parent_menu.getUrl() );
            }
        }
        while( parent_menu != null ){
            int pid = parent_menu.getParentId();
            if( pid != 0 ){
                finder = user.getMenus().stream().filter( m -> ( ( Menu ) m ).getId().intValue() == pid ).findFirst();
                if( finder.isPresent() ) {
                    parent_menu = finder.get();
                    actives.add( parent_menu.getUrl() );
                }else parent_menu = null;
            }else parent_menu = null;
        }

        _request.setAttribute( Request_Actives_EL, actives );
    }
}
