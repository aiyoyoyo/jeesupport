package com.jees.webs.abs;

import com.jees.common.CommonConfig;
import com.jees.tool.crypto.MD5Utils;
import com.jees.webs.dao.IdentityDao;
import com.jees.webs.entity.SuperMenu;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
public abstract class AbsInstallService< M extends SuperMenu, U extends SuperUser, R extends SuperRole > {
    @Autowired
    IdentityDao      identityDao;

    public void install( String _account, String _password, String _group ) throws Exception{
        U u = buildUser();

        if( CommonConfig.getBoolean( "jees.webs.security.encodePwd", true ) ){
            _password = MD5Utils.s_encode( _password );
        }

        u.setId( identityDao.build( IdentityDao.IdentityType.UID,
                                    CommonConfig.getLong( "jees.webs.identity", 0L ) ) );
        u.setUsername( _account );
        u.setPassword( _password );
        u.setEnabled( true );
//        u.setLocked( false );

        R r = buildRole();
        u.setId( identityDao.build( IdentityDao.IdentityType.RID, 0 ) );
        r.setName( _group );

//        List< Template > tpls = templateService.getTemplateAll();
//
//        String            index_page = "/";
//        String            login_page = "/" + CommonConfig.getString( "jees.webs.login", "login" );
//        Map< Integer, M > m_maps     = new HashMap<>();
//
//        for( Template t : tpls ){
//            Collection< Page > pages = t.getPages().values();
//            for( Page p : pages ){
//                if( p.getUrl().equals( login_page ) ) continue;
//
//                M m = buildMenu();
//                m.setId( ( int ) identityDao.build( IdentityDao.IdentityType.MID, -1 ) );
//                m.setUrl( p.getUrl() );
//                m.setName( p.getUrl() );
//                m.setVisible( 0 );
//                m.setIndex( 0 );
//                m.setParentId( m.getId() );
//                m.setTpl( p.getTpl() );
//
//                if( m.getUrl().equals( index_page ) && m.getId() != 0 ){
//                    M zero_m = m_maps.get( 0 );
//                    zero_m.setId( m.getId() );
//                    zero_m.setParentId( zero_m.getId() );
//                    r.addMenu( zero_m );
//                    m_maps.put( zero_m.getId(), zero_m );
//                    m.setId( 0 );
//                    m.setParentId( 0 );
//                }
//
//                m_maps.put( m.getId(), m );
//                r.addMenu( m );
//            }
//        }
//
//        m_maps.forEach( ( k1, v1 )->
//            m_maps.forEach( ( k2, v2 )->{
//                if( !v1.getUrl().equals( index_page ) && !v1.getUrl().equals( login_page ) ){
//                    if( v2.getId() != v1.getId() && v2.getUrl().indexOf( v1.getUrl() ) != -1 ){
//                        v2.setParentId( v1.getId() );
//                    }
//                }
//            } )
//        );
//
//        sDB.insertMap( m_maps, buildMenu().getClass() );
//
//        u.addRole( r );
//        sDB.insert( u );
//        sDB.insert( r );
    }

    public void refresh() throws Exception{
//        List< Template > tpls = templateService.getTemplateAll();
//
//        String            index_page = "/";
//        String            login_page = "/" + CommonConfig.getString( "jees.webs.login", "login" );
//        Map< Integer, M > m_maps     = new HashMap<>();
//
//        for( Template t : tpls ){
//            Collection< Page > pages = t.getPages().values();
//            for( Page p : pages ){
//                if( p.getUrl().equals( login_page ) ) continue;
//
//                M m = buildMenu();
//                m.setId( ( int ) identityDao.build( IdentityDao.IdentityType.MID, -1 ) );
//                m.setUrl( p.getUrl() );
//                m.setName( p.getUrl() );
//                m.setVisible( 0 );
//                m.setIndex( 0 );
//                m.setParentId( m.getId() );
//                m.setTpl( p.getTpl() );
//
//                m_maps.put( m.getId(), m );
//            }
//        }
//
//        m_maps.forEach( ( k1, v1 )->
//            m_maps.forEach( ( k2, v2 )->{
//                if( !v1.getUrl().equals( index_page ) && !v1.getUrl().equals( login_page ) ){
//                    if( v2.getId() != v1.getId() && v2.getUrl().indexOf( v1.getUrl() ) != -1 ){
//                        v2.setParentId( v1.getId() );
//                    }
//                }
//            } )
//        );
//
//        List< M > list = null;
////        ( List< M > ) absSS.getMenus().values().stream().collect( Collectors.toList() );
//
//        for( SuperMenu sm : list ){
//            Iterator< Integer > k_it = m_maps.keySet().iterator();
//            while( k_it.hasNext() ){
//                int k = k_it.next();
//                M   v = m_maps.get( k );
//                if( v.getUrl().equals( sm.getUrl() ) ){
//                    k_it.remove();
//                    break;
//                }
//            }
//        }
//        log.debug( "插入新的栏目数量：" + m_maps.size() );
//        if( m_maps.size() > 0 ){
//            sDB.insert( m_maps );
//        }
    }

    public abstract U buildUser();
    public abstract R buildRole();
    public abstract M buildMenu();
}
