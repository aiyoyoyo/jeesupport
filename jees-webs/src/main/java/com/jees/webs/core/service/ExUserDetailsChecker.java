package com.jees.webs.core.service;

import com.jees.webs.core.interf.ICodeDefine;
import com.jees.webs.entity.SuperUser;
import com.jees.webs.security.exception.RequestException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

/**
 * @Description: TODO
 * @Package: com.jees.datascale.admin.impls
 * @ClassName: UserDetailsCheckerImpl
 * @Author: 刘甜
 * @Date: 2023/3/30 9:24
 * @Version: 1.0
 */
@Component
@Log4j2
public class ExUserDetailsChecker implements UserDetailsChecker {
    @Override
    public void check(UserDetails _user) {
        SuperUser user = (SuperUser) _user;
        if (!user.isAccountNonLocked()) {
            if (log.isDebugEnabled()) {
                log.debug("用户[" + user.getUsername() + "]账号被锁定！");
            }
            throw new RequestException(ICodeDefine.User_IsLocked);
        }

        if (!user.isEnabled()) {
            if (log.isDebugEnabled()) {
                log.debug("用户[" + user.getUsername() + "]账号被禁用！");
            }
            throw new RequestException(ICodeDefine.User_IsBlack);
        }

        if (!user.isAccountNonExpired()) {
            if (log.isDebugEnabled()) {
                log.debug("用户[" + user.getUsername() + "]账号已过有效期！");
            }
            throw new RequestException(ICodeDefine.User_IsExpired);
        }

        if (!user.isCredentialsNonExpired()) {
            if (log.isDebugEnabled()) {
                log.debug("用户[" + user.getUsername() + "]账号密码已过有效期！");
            }
            throw new RequestException(ICodeDefine.Password_IsExpired);
        }
    }
}
