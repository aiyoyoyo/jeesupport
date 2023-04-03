package com.jees.webs.security.interf;

import com.jees.webs.entity.SuperUser;

/**
 * @Description: TODO
 * @Package: com.jees.webs.security.interf
 * @ClassName: IVerifyUser
 * @Author: 刘甜
 * @Date: 2023/3/29 15:31
 * @Version: 1.0
 */
public interface IVerifyUser {
    /**
     * 自定义加载用户信息
     * @param _user
     */
    public void loadUser(SuperUser _user);
}
