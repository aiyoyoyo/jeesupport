package com.jees.webs.remote;

import com.jees.webs.abs.AbsSuperService;
import com.jees.webs.entity.SuperMenu;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;

import java.util.List;
import java.util.stream.Collectors;

@RemoteProxy
public class MgrMenuRemote{

//    @Autowired
    AbsSuperService     absSS;

    @RemoteMethod
    public List< SuperMenu > load(){
        List< SuperMenu > datas = ( List< SuperMenu > ) absSS.getMenus().values().stream().collect( Collectors.toList() );
        return datas;
    }

    @RemoteMethod
    public void save( SuperMenu _menu ){
        absSS.save( _menu );
    }

    @RemoteMethod
    public void remove( SuperMenu _menu ) throws Exception{
        absSS.remove( _menu );
    }
}
