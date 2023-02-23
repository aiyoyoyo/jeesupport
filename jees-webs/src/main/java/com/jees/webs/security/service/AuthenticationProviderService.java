package com.jees.webs.security.service;

import com.jees.webs.core.service.ExUserDetailsService;
import com.jees.webs.core.service.SecurityService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @Override
    public Authentication authenticate(Authentication _auth) throws AuthenticationException {
        String username = _auth.getName();
        String password = _auth.getCredentials().toString();
        UserDetails user = null;
        try{
            user = userDetailsService.loadUserByUsername(username);
            if( !securityService.matches( password, user.getPassword() ) ){
                user = null;
            }
        }catch (UsernameNotFoundException e){
            log.error(e.getMessage());
        }
        if( user == null ){
            user = securityService.thirdMatches( username, password );
            if( user == null ){
                throw new BadCredentialsException("用户名密码不匹配！");
            }
        }
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
