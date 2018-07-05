package com.jees.test.remote;

import com.jees.test.entity.Menu;
import com.jees.test.entity.Role;
import com.jees.test.entity.User;
import com.jees.test.service.DaoServiceImpl;
import com.jees.webs.entity.SuperMenu;
import com.jees.webs.entity.SuperUser;
import com.jees.webs.support.AbsInstallService;
import com.jees.webs.support.ISuperService;
import com.jees.webs.support.ISupportEL;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.*;

@Service
@RemoteProxy
public class SuperRemote implements ISupportEL {

    @Autowired
    AbsInstallService installService;
    @Autowired
    ISuperService superService;
    @Autowired
    DaoServiceImpl  daoService;

    @Transactional
    @RemoteMethod
    public boolean install() {
        return installService.doInstall();
    }
    @Transactional
    @RemoteMethod
    public void installRefresh(){
        installService.doRefresh( DaoServiceImpl.DB_Default, new Menu() );
    }

    @RemoteMethod
    public boolean installState(){
        return installService.getInstallFile().exists();
    }

    @Transactional
    @RemoteMethod
    public <M extends Menu > List< M > loadMenuData( HttpServletRequest _request ){
        return superService.loadMenus();
    }

    @Transactional
    @RemoteMethod
    public <R extends Role > List< R > loadRoleData( HttpServletRequest _request ){
        return superService.loadRoles();
    }

    @Transactional
    @RemoteMethod
    public <U extends User > List< U > loadUserData( HttpServletRequest _request ){
        return superService.loadUsers();
    }

    @Transactional
    @RemoteMethod
    public void saveMenuData( Menu _menu ){
        // TIPS DWR注意事项
        // 1. 此处因为dwr没有加载关联表，所以在做更新时防止关联被删除，先加载一次给需要保存的对象。
        // 2. 不加载关联表，因为dwr对由于hibernate表关系使用的复杂泛型理解不了，看后续版本是否能修复。
        // 3. dwr:signatures和hibernate:cascade设置都无效。
        // TIPS 解决办法
        // 1. 不加入关联表相关字段的dwr映射，仅在需要的字段上加上@RemoteProperty
        // 2. 对于对象做更新操作时，关联表是为空的，所以在执行操作时hibernate会理解为删除对应关系。
        // 3. 于是你看到了下面这段代码。
        // 4. 对于更新或插入时会删除关联表的解决办法是重写equals和hashCode
        Menu old_menu = superService.loadMenu( _menu.getId() );
        _menu.setRoles( old_menu.getRoles() );

        superService.saveMenu( _menu );
    }

    @Transactional
    @RemoteMethod
    public void removeMenuData( Menu _menu ){
        daoService.delete( DaoServiceImpl.DB_Default, _menu );
        daoService.commit();
    }

    @RemoteMethod
    public <M extends Menu > void saveData( List<M> _obj ){
        System.out.println( "saveData--->" + _obj );
    }

    @Transactional
    @RemoteMethod
    public void saveRoleData( Role _role, Map<Serializable, Menu> _menus ) {
        // TIPS DWR注意事项2
        // 1. 这里在加载role到页面时，同时加载了相关栏目
        // 2. 由于DWR复杂泛型理解问题，我做了比较别扭的处理
        // TIPS 解决办法
        // 1.在js中，我先将role的menus提取到另外一个js对象中，并设为了Null，在做保存。
        //  var m = o.menus;
        //  o.menus = null;
        //  SuperRemote.saveRoleData( o, m );
        // 2. 这里需要配合dwr:signatures配置完成Serializable类型的声明
        //  SuperRemote.saveRoleData( Role _role, Map<Integer, Menu> _menu );
        _role.setMenus( _menus );

        // TIPS 关联的第三方表，因为没有处理，所以我没有加载对应数据和处理映射
        // 同理，其实也可以把user使用栏目的处理方式来做，这里就不需要在读取一次了。
        Role old_role = superService.loadRole( _role.getId() );
        _role.setUsers( old_role.getUsers() );

        superService.saveRole( _role );
    }

    @Transactional
    @RemoteMethod
    public void addRoleData( String _name ){
        // TIPS
        Role role = new Role();
        role.setName( _name );

        daoService.insert( DaoServiceImpl.DB_Default, role );
        daoService.commit();
    }

    @Transactional
    @RemoteMethod
    public void removeRoleData( Role _role ){
        daoService.delete( DaoServiceImpl.DB_Default, _role );
        daoService.commit();
    }
}
