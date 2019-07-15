package com.jees.test.service;

import com.jees.test.entity.User;
import com.jees.webs.abs.AbsUserDetailsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class UserDetailsServiceImpl extends AbsUserDetailsService<User> {
    @Override
    public Class< User > superClass(){
        return User.class;
    }
}
