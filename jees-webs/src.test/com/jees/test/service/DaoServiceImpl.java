package com.jees.test.service;

import com.jees.core.database.dao.BaseDao;
import com.jees.test.entity.User;
import com.jees.webs.entity.SuperUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DaoServiceImpl extends BaseDao {
    public static String    DB_Default = "default";

    public SuperUser selectUserByName( String username ) {
        String hql = "FROM User WHERE username = :UN" ;
        List<User> list = this.selectByHQL( DB_Default, hql, new String[]{"UN"}, new Object[]{ username }, User.class );
        return list.size() == 1 ? list.get(0) : null;
    }
}
