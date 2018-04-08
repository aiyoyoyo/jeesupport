package com.jees.test.service;

import com.jees.common.CommonLogger;
import com.jees.tool.crypto.MD5Utils;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;
import com.jees.webs.support.AbsUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl extends AbsUserDetailsService {

    @Autowired
    DaoServiceImpl              daoService;

    private SuperUser _find_user(String _username ) throws UsernameNotFoundException{
        //TODO 执行数据库查询
        SuperUser user = daoService.selectUserByName( _username );
        CommonLogger.getLogger( this.getClass() ).debug( "--查找登陆用户信息：U=[" + user + "]" );
        if( user == null ) throw new UsernameNotFoundException("用户不存在");
        return user;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername( String _username ) throws UsernameNotFoundException {
        UserDetails user = checkSuperman( _username );

        if( user == null ) user = _find_user( _username );

        CommonLogger.getLogger( this.getClass() ).debug( "--验证登陆用户信息：U=[" + _username + "] P=[" + user.getPassword() + "]" );
        super.build( user );
        CommonLogger.getLogger( this.getClass() ).debug( "--用户信息：U=[" + user + "]" );

        return user;
    }
}
