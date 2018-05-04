package com.jees.test.service;

import com.jees.webs.entity.SuperUser;
import com.jees.webs.support.AbsUserDetailsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Log4j2
public class UserDetailsServiceImpl extends AbsUserDetailsService {

    @Autowired
    DaoServiceImpl              daoService;

    private SuperUser _find_user(String _username ) throws UsernameNotFoundException{
        //TODO 执行数据库查询
        SuperUser user = daoService.selectUserByName( _username );
        log.debug( "--查找登陆用户信息：U=[" + user + "]" );
        if( user == null ) throw new UsernameNotFoundException("用户不存在");
        return user;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername( String _username ) throws UsernameNotFoundException {
        UserDetails user = checkSuperman( _username );

        if( user == null ) user = _find_user( _username );

        log.debug( "--验证登陆用户信息：U=[" + _username + "] P=[" + user.getPassword() + "]" );
        super.build( user );
        log.debug( "--用户信息：U=[" + user + "]" );

        return user;
    }
}
