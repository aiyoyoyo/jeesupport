package com.jees.test.service;

import com.jees.core.database.dao.BaseDao;
import com.jees.test.entity.Menu;
import com.jees.test.entity.User;
import com.jees.webs.entity.SuperMenu;
import com.jees.webs.entity.SuperUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DaoServiceImpl extends BaseDao {
    public static String    DB_Default = "default";

    public SuperUser selectUserByName( String username ) {
        String hql = "FROM User WHERE username = :UN" ;
        List<User> list = this.selectByHQL( DB_Default, hql, new String[]{"UN"}, new Object[]{ username }, User.class );

        SuperUser user = list.size() == 1 ? list.get(0) : null;

        if( user != null ){
            user.getMenus();
        }

        return user;
    }

    @SuppressWarnings( "unchecked" )
    public List<SuperMenu> selectTemplateMenus( String _tpl ) {
        String hql = "FROM Menu WHERE tpl = :TPL" ;
        List<? extends SuperMenu> list = this.selectByHQL( DB_Default, hql, new String[]{"TPL"}, new Object[]{ _tpl }, Menu.class );

        list.forEach( m -> m.getRoles().size() );

        return (List<SuperMenu>) list;
    }

    public <T> List<T> select( Class<T> _cls ) {
        return select( DB_Default, _cls );
    }
}
