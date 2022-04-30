package com.jees.webs.abs;

import com.jees.common.CommonConfig;
import com.jees.core.database.support.AbsRedisDao;
import com.jees.tool.crypto.MD5Utils;
import com.jees.webs.dao.IdentityDao;
import com.jees.webs.entity.SuperMenu;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;
import com.jees.webs.support.ISuperService;
import com.jees.webs.support.ISupportEL;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * AbsSuperService抽象实现类，包含核心修改内容：
 * 1: 读取用户权限及栏目
 * 2: 编辑栏目\角色\权限
 * @author aiyoyoyo
 */
@Log4j2
public abstract class AbsSuperService<M extends SuperMenu,U extends SuperUser,R extends SuperRole > implements ISuperService, ISupportEL{
    @Autowired
    AbsRedisDao sDB;
    @Autowired
    IdentityDao iDB;

    @Getter
    @Setter
    Map< Integer, M > menus = new HashMap<>();
    @Getter
    @Setter
    Map< Integer, R > roles = new HashMap<>();
    @Getter
    @Setter
    Map< Object, U >  users = new HashMap<>();


    @Override
    public void initialize(){
        menus = sDB.findHashAll( getExMenuClass() );
        roles = sDB.findHashAll( getExRoleClass() );
        users = sDB.findHashAll( getExUserClass() );

        Set< M > menus_sets = new HashSet<>( menus.values() );
        for( M m : menus_sets ){
            if( !m.isRoot() ){
                M sm = menus.get( m.getParentId() );
                sm.addMenu( m );
            }
        }
    }

    public List< String > loadMenuRoles( int _menu ){
        if( roles.isEmpty() ){
            this.initialize();
        }

        List< String > datas = new ArrayList<>();
        roles.values().forEach( r->{
            r.getMenus().forEach( m->{
                if( ( Integer ) m == _menu ){
                    if( !datas.contains( r.getName() ) ){
                        datas.add( r.getName() );
                    }
                }
            } );
        } );
        return datas;
    }

    public Set< SimpleGrantedAuthority > loadUserRoles( U u ){
        if( roles.isEmpty() ){
            this.initialize();
        }

        Iterator< Integer > r_it = u.getRoles().iterator();
        while( r_it.hasNext() ){
            Integer r_id = r_it.next();
            R       r    = roles.getOrDefault( r_id, null );
            if( r != null ){
                SimpleGrantedAuthority sga = new SimpleGrantedAuthority( r.getName() );
                if( !u.getAuthorities().contains( sga ) ){
                    u.getAuthorities().add( sga );
                }
            }
        }

        return u.getAuthorities();
    }

    private Map< String, M > _load_user_menus_( Set< Integer > _roles ){
        Map< String, M > user_menus = new HashMap<>();

        for( Integer rid : _roles ){
            R r = roles.getOrDefault( rid, null );
            if( r != null ){
                for( Object id : r.getMenus() ){
                    M m = menus.get( id );
                    if( m != null && m.isRoot() )
                        user_menus.put( m.getUrl(), m );
                }
            }
        }
        return user_menus;
    }


    @Override
    public void loadUserMenus( HttpServletRequest _request ){
        HttpSession session = _request.getSession();
        U           user    = ( U ) session.getAttribute( Session_User_EL );
        if( user == null ) return;

        Map< String, M > user_menus = ( Map ) session.getAttribute( Session_Menus_EL );
//        if( user_menus == null ){
        user_menus = _load_user_menus_( user.getRoles() );
//        }

        List<Map.Entry<String, M>> list = new LinkedList<>(user_menus.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, M>>() {
            @Override
            public int compare(Map.Entry<String, M> _m1, Map.Entry<String, M> _m2) {
                return _m1.getValue().getIndex() - _m2.getValue().getIndex();
            }
        });

        Map<String, M> sort_menus = new LinkedHashMap<>();
        for (Map.Entry<String, M> entry : list) {
            sort_menus.put(entry.getKey(), entry.getValue());
        }

        session.setAttribute( Session_Menus_EL, sort_menus );
    }

    @Override
    public void loadUserBreadcrumb( HttpServletRequest _request ){
        String uri = _request.getRequestURI();
        if( uri.isEmpty() ) uri = "/";
        _request.setAttribute( Request_Page_EL, uri );

        List< M > breads = ( List< M > ) _request.getSession().getAttribute( Session_Breadcrumb_EL );
        if( breads == null ) breads = new ArrayList<>();
        _request.getSession().setAttribute( Session_Breadcrumb_EL, breads );

        Iterator< M > it = breads.iterator();
        while( it.hasNext() ){
            if( it.next().getUrl().equals( uri ) )
                it.remove();
        }

        U user = ( U ) _request.getSession().getAttribute( Session_User_EL );
        if( user == null ) return;

        String        menu_uri = uri;
        Optional< M > finder   = menus.values().stream().filter( m->( m.getUrl().equals( menu_uri ) ) ).findFirst();

        if( finder.isPresent() ){
            M menu = finder.get();
            breads.add( menu );
            _request.setAttribute( Request_Menu_EL, menu );
        }

        if( breads.size() > 3 ) breads.remove( 0 );
    }

    private M _check_url_( M _m, String _uri ){
        if( _m.getUrl().equals( _uri ) ){
            return _m;
        }

        Iterator< M > m_it = _m.getMenus().iterator();

        while( m_it.hasNext() ){
            M m = _check_url_( m_it.next(), _uri );
            if( m != null ) return m;
        }
        return null;
    }

    @Override
    public void loadUserActiveMenus( HttpServletRequest _request ){
        String           uri        = ( String ) _request.getAttribute( Request_Page_EL );
        Map< String, M > user_menus = ( Map ) _request.getSession().getAttribute( Session_Menus_EL );
        if( user_menus == null ){
            _request.setAttribute( Request_Actives_EL, new HashSet< M >() );
            return;
        }

        Iterator< M > m_it = user_menus.values().iterator();
        M             menu = null;
        while( m_it.hasNext() ){
            M m = _check_url_( m_it.next(), uri );
            if( m != null ){
                menu = m;
                break;
            }
        }

        if( menu == null ){
            _request.setAttribute( Request_Actives_EL, new HashSet< M >() );
            return;
        }

        Set< String > actives = new HashSet<>();
        actives.add( menu.getUrl() );

        M parent_menu = null;
        if( !menu.isRoot() ){
            parent_menu = menus.getOrDefault( menu.getParentId(), null );
            if( parent_menu != null ){
                actives.add( parent_menu.getUrl() );
            }
        }
        while( parent_menu != null ){
            if( !parent_menu.isRoot() ){
                parent_menu = menus.getOrDefault( parent_menu.getParentId(), null );
                if( parent_menu != null ){
                    actives.add( parent_menu.getUrl() );
                }
            }else parent_menu = null;
        }

        _request.setAttribute( Request_Actives_EL, actives );
    }

    // SuperMenu Manager

    public void save( M _menu ){
        M m = menus.get( _menu.getId() );
        m.setName( _menu.getName() );

        if( m.getId() != _menu.getParentId() && m.getParentId() != _menu.getParentId() ){
            M old_parent_m = menus.get( m.getParentId() );
            old_parent_m.getMenus().remove( m );

            m.setParentId( _menu.getParentId() );

            M new_parent_m = menus.get( m.getParentId() );
            new_parent_m.getMenus().add( m );
        }else{
            m.setParentId( _menu.getParentId() );
        }

        sDB.update( m );
    }

    public void remove( M _menu ) throws Exception{
        M m = menus.get( _menu.getId() );
        menus.remove( _menu.getId() );
        sDB.delete( m );
    }

    // SuperUser Manager

    public void save( R _role ){
        R r = roles.get( _role.getId() );

        r.setName( _role.getName() );
        r.setMenus( _role.getMenus() );

        sDB.update( r );
    }

    public void remove( R _role ) throws Exception{
        R r = roles.get( _role.getId() );
        roles.remove( r.getId() );
        sDB.delete( r );
    }

    public void add( R _role ) throws Exception{
        _role.setId( ( int ) iDB.build( IdentityDao.IdentityType.RID ) );
        roles.put( _role.getId(), _role );
        sDB.insert( _role );
    }

    // SuperUser Manager

    public void save( U _user ){
        U      u   = users.get( _user.getId() );
        String pwd = _user.getPassword();
        if( CommonConfig.getBoolean( "jees.webs.encodePwd", true ) ){
            if( pwd == null ){
                pwd = u.getPassword();
            }else{
                pwd = MD5Utils.s_encode( pwd );
            }
        }
        if(!u.getPassword().equals( pwd ) ){
            u.setPassword( pwd );
        }
        u.setLocked( _user.isLocked() );
        u.setEnabled( _user.isEnabled() );
        u.setUsername( _user.getUsername() );
        u.setRoles( _user.getRoles() );

        sDB.update( u );
    }

    public void remove( U _user ) throws Exception{
        U u = users.get( _user.getId() );
        users.remove( u.getId() );
        sDB.delete( u );
    }

    public void add( U _user ) throws Exception{
        _user.setId( iDB.build( IdentityDao.IdentityType.UID ) );
        users.put( _user.getId(), _user );
        sDB.insert( _user );
    }

    public < T > T build( Class< T > _cls ) throws IllegalAccessException, InstantiationException{
        T t = _cls.newInstance();
        return t;
    }
}
