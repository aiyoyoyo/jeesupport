package com.jees.test.service;

import com.jees.test.entity.Menu;
import com.jees.test.entity.Role;
import com.jees.test.entity.User;
import com.jees.webs.abs.AbsSuperService;
import org.springframework.stereotype.Service;

@Service
public class SuperServiceImpl extends AbsSuperService<Menu,User,Role>{

    @Override
    public Class<Menu> getExMenuClass(){
        return Menu.class;
    }
    @Override
    public Class<User> getExUserClass(){
        return User.class;
    }
    @Override
    public Class<Role> getExRoleClass(){
        return Role.class;
    }
}
