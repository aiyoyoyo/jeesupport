package com.jees.webs.security.service;

import com.jees.webs.core.interf.ICodeDefine;
import com.jees.webs.core.service.ExUserDetailsService;
import com.jees.webs.core.service.SecurityService;
import com.jees.webs.security.exception.RequestException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Service;

/**
 * @Description: TODO
 * @Package: com.jees.webs.security.provider
 * @ClassName: AuthenticationProvider
 * @Author: 刘甜
 * @Date: 2023/2/23 13:36
 * @Version: 1.0
 */
@Log4j2
@Service
public class AuthenticationProviderService implements AuthenticationProvider {
    @Autowired
    ExUserDetailsService userDetailsService;
    @Autowired
    SecurityService securityService;
    @Autowired
    UserDetailsChecker checker;
    @Override
    public Authentication authenticate(Authentication _auth) throws AuthenticationException {
        String username = _auth.getName();
        String password = _auth.getCredentials().toString();
        UserDetails user = userDetailsService.loadUserByUsername(username);
        if( !securityService.matches( password, user.getPassword() ) ){
            log.debug("本地用户名密码不匹配！");
            user = securityService.thirdMatches( username, password );
            if( user == null ){
                log.debug("第三方系统用户名密码不匹配！");
                throw new RequestException(ICodeDefine.Login_PasswordInvalid);
            }
        }
        // 构建用户信息
        userDetailsService.build(user);
        // 检查账号状态
        checker.check( user );

        UsernamePasswordAuthenticationToken result = UsernamePasswordAuthenticationToken
                .authenticated(
                        user,
                        _auth.getCredentials(),
                        user.getAuthorities() );
        result.setDetails(_auth.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> _auth) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(_auth);
    }
}
