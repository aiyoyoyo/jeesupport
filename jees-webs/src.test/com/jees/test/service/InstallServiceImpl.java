package com.jees.test.service;

import com.jees.test.entity.Menu;
import com.jees.test.entity.Role;
import com.jees.test.entity.User;
import com.jees.webs.support.AbsInstallService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstallServiceImpl extends AbsInstallService < Menu, User, Role > {
    @Override
    public void install() {
        User u = new User();
        u.setUsername( "administrator" );
        u.setPassword( "123456" );
        u.setEnabled( true );
        u.setLocked( false );

        Role r = new Role();
        r.setName( "系统管理员" );

        u = installUsers( DaoServiceImpl.DB_Default, u );
        List< Menu > ms = installMenus( DaoServiceImpl.DB_Default, new Menu() );
        r = installRoles( DaoServiceImpl.DB_Default, r );

        installDefaultUserAndRole( DaoServiceImpl.DB_Default, u, ms, r );
    }
}
