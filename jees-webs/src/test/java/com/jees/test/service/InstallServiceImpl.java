package com.jees.test.service;

import com.jees.test.entity.Menu;
import com.jees.test.entity.Role;
import com.jees.test.entity.User;
import com.jees.webs.abs.AbsInstallService;
import org.springframework.stereotype.Service;

@Service
public class InstallServiceImpl extends AbsInstallService < Menu, User, Role > {
    @Override
    public User buildUser(){
        return new User();
    }

    @Override
    public Role buildRole(){
        return new Role();
    }

    @Override
    public Menu buildMenu(){
        return new Menu();
    }
}
