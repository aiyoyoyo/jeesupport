package com.jees.webs.remote;

import com.jees.common.CommonConfig;
import com.jees.tool.crypto.MD5Utils;
import com.jees.tool.utils.RandomUtil;
import com.jees.webs.abs.AbsSuperService;
import com.jees.webs.entity.SuperUser;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;

import java.util.List;
import java.util.stream.Collectors;

@RemoteProxy
public class MgrUserRemote{

//    @Autowired
    AbsSuperService     absSS;

    @RemoteMethod
    public List< SuperUser > load(){
        List< SuperUser > datas = ( List< SuperUser > ) absSS.getUsers().values().stream().collect( Collectors.toList() );
        return datas;
    }

    @RemoteMethod
    public void save( SuperUser _user ){
        absSS.save( _user );
    }

    @RemoteMethod
    public void remove( SuperUser _user ) throws Exception{
        absSS.remove( _user );
    }

    @RemoteMethod
    public SuperUser add() throws Exception{
        SuperUser u = ( SuperUser ) absSS.build( absSS.getExUserClass() );

        u.setUsername( RandomUtil.s_random_string( 6 ) );
        u.setPassword( u.getUsername() );
        if( CommonConfig.getBoolean( "jees.webs.security.encodePwd", true ) ){
            u.setPassword( MD5Utils.s_encode( u.getPassword() ) );
        }

        u.setEnabled( false );
        u.setLocked( true );

        absSS.add( u );
        return u;
    }
}
