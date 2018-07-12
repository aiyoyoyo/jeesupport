package com.jees.webs.support;

import com.jees.core.database.dao.BaseDao;
import com.jees.tool.crypto.MD5Utils;
import com.jees.webs.entity.SuperMenu;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
public abstract class AbsInstallService< M extends SuperMenu, U extends SuperUser, R extends SuperRole > {
    @Autowired
    ITemplateService    templateService;
    @Autowired
    BaseDao             dao;
    @Autowired
    ResourcePatternResolver resourcePatternResolver;

    @SuppressWarnings( "unchecked" )
    public U installUsers( String _db, U _u ){
        U u = ( U ) _u.build();

        u.setUsername( _u.getUsername() );
        u.setPassword( MD5Utils.s_encode( _u.getPassword() ) );
        u.setEnabled( _u.isEnabled() );
        u.setLocked( _u.isLocked() );

        dao.insert( _db, u );
        dao.commit();

        return u;
    }

    @SuppressWarnings( "unchecked" )
    public List< M > installMenus( String _db, M _m ){
        List<M> ms = new ArrayList<>();
        templateService.getTemplateAll().forEach( t -> {
            t.getPages().values().forEach( p -> {
                M m = ( M ) _m.build();
                m.setUrl( p.getUrl() );
                m.setName( p.getUrl() );
                m.setVisible( 0 );
                m.setIndex( 0 );
                m.setParentId( 0 );
                m.setTpl( p.getTpl() );

                ms.add( m );
            } );
        } );

        dao.insert( _db, ms );
        dao.commit();

        return ms;
    }

    @SuppressWarnings( "unchecked" )
    public R installRoles( String _db, R _r ){
        R r = ( R ) _r.build();
        r.setName( _r.getName() );

        dao.insert( _db, r );
        dao.commit();

        return r;
    }

    @SuppressWarnings( "unchecked" )
    public void installDefaultUserAndRole( String _db, U _u, List<M> _ms, R _r ){
        Map roles = new HashMap<>();
        roles.put( _r.getId(), _r );
        _u.setRoles( roles );

        Map menus = _ms.stream().collect( Collectors.toMap( M::getId, Function.identity() ) );

        _r.setMenus( menus );

        dao.update( _db, _u );
        dao.update( _db, _r );
        dao.commit();
    }
    /**
     * 由项目独立实现初始化相关配置
     */
    public abstract void install();

    public boolean doInstall() {
        File file = getInstallFile();
        if( file.exists() ){
            log.warn( "--安装程序已执行" );
            return true;
        }

        install();

        try {
            file.createNewFile();
            return true;
        } catch (IOException e) {
            log.error( "创建安装记录文件失败：PATH=[" + file.getAbsoluteFile() +  "]" );
        }

        return false;
    }

    @SuppressWarnings( "unchecked" )
    public void doRefresh( String _db, M _m ){
        List<M> ms = new ArrayList<>();
        templateService.getTemplateAll().forEach( t -> {
            t.getPages().values().forEach( p -> {
                M m = ( M ) _m.build();
                m.setUrl( p.getUrl() );
                m.setName( p.getUrl() );
                m.setVisible( 0 );
                m.setIndex( 0 );
                m.setParentId( 0 );
                m.setTpl( p.getTpl() );

                ms.add( m );
            } );
        } );

        List< ? extends SuperMenu > list = dao.select( _db, _m.getClass() );


        for( SuperMenu sm : list ){
            Iterator< M > it = ms.iterator();
            while( it.hasNext() ){
                M m = it.next();
                if( sm.getUrl().equals( m.getUrl() ) ) {
                    it.remove();
                    break;
                }
            }
        }
        dao.insert( _db, ms );
        dao.commit();
    }

    public File getInstallFile(){
        String root_tpl = "classpath:/";

        String tmp_path = "";
        try {
            tmp_path = resourcePatternResolver.getResource( root_tpl ).getURL().getPath();
            log.debug( ".install文件路径：PATH=[" + tmp_path + "]" );
        } catch (IOException e) {
            log.error( ".install文件未找到：PATH=[" + root_tpl + "]" );
        }

        return new File( tmp_path + ".install" );
    }
}
