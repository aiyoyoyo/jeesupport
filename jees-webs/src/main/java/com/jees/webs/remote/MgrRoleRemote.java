package com.jees.webs.remote;

import com.jees.webs.abs.AbsSuperService;
import com.jees.webs.entity.SuperRole;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;

import java.util.List;
import java.util.stream.Collectors;

@RemoteProxy
public class MgrRoleRemote{

//    @Autowired
    AbsSuperService     absSS;

    @RemoteMethod
    public List< SuperRole > load(){
        List< SuperRole > datas = ( List< SuperRole > ) absSS.getRoles().values().stream().collect( Collectors.toList() );
        return datas;
    }

    @RemoteMethod
    public void save( SuperRole _role ){
        absSS.save( _role );
    }

    @RemoteMethod
    public void remove( SuperRole _role ) throws Exception{
        absSS.remove( _role );
    }

    @RemoteMethod
    public SuperRole add( String _name ) throws Exception{
        SuperRole r = ( SuperRole ) absSS.build( absSS.getExRoleClass() );
        r.setName( _name );

        absSS.add( r );

        return r;
    }
}
