package com.jees.jsts.server.support;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @param <ID>
 * @author aiyoyoyo
 */
@Service
@Log4j2
public class SessionService< ID >{
    Map< ID, SuperUser< ID > >            Ids2Usr = new ConcurrentHashMap<>();
    Map< ChannelHandlerContext, Boolean > NetIsWs = new ConcurrentHashMap<>();
    // 是否有效用户，以下2个数据为准

    Map< ID, ChannelHandlerContext > Ids2Net = new ConcurrentHashMap<>();
    Map< ChannelHandlerContext, ID > Net2Ids = new ConcurrentHashMap<>();

    public < T extends SuperUser< ID > > T find( ChannelHandlerContext _ctx ){
        ID id = Net2Ids.getOrDefault( _ctx, null );
        if( id == null ) return null;
        return ( T ) Ids2Usr.getOrDefault( id, null );
    }

    public < T extends SuperUser< ID > > T find( ID _user ){
        return ( T ) Ids2Usr.getOrDefault( _user, null );
    }

    public ID findID( ChannelHandlerContext _ctx ){
        return Net2Ids.getOrDefault( _ctx, null );
    }

    public boolean isOnline( ID _id ){
        return Ids2Net.containsKey( _id );
    }

    public boolean isOnline( ChannelHandlerContext _ctx ){
        return Net2Ids.containsKey( _ctx );
    }

    public < T extends SuperUser< ID > > boolean isOnline( T _user ){
        if( _user.getId() != null )
            return isOnline( _user.getId() );
        else if( _user.getNet() != null )
            return isOnline( _user.getNet() );

        return false;
    }

    public boolean isWebSocket( ChannelHandlerContext _ctx ){
        return NetIsWs.getOrDefault( _ctx, false );
    }

    public void connect( ChannelHandlerContext _ctx, boolean _ws ){
        NetIsWs.put( _ctx, _ws );
    }

    public < T extends SuperUser< ID > > void enter( ChannelHandlerContext _net, T _user ){
        if( !isOnline( _net ) ){
            Ids2Usr.put( _user.getId(), _user );
            Ids2Net.put( _user.getId(), _net );
            Net2Ids.put( _net, _user.getId() );

            _user.enter( _net );
        }
    }

    public < T extends SuperUser< ID > > void leave( ChannelHandlerContext _net ){
        if( isOnline( _net ) ){
            T user = find( _net );
            Ids2Usr.remove( user.getId() );
            ChannelHandlerContext net = Ids2Net.remove( user.getId() );
            Net2Ids.remove( net );
            NetIsWs.remove( net );
            user.leave();
        }
    }

    public < T extends SuperUser< ID > > void switchover( ChannelHandlerContext _net, ID _id ){
        T user = find( _id );

        if( user != null ){
            ChannelHandlerContext old = Ids2Net.remove( user.getId() );
            Net2Ids.remove( old );
            NetIsWs.remove( old );
            old.flush().close();

            user.switchover( _net );
        }

        Ids2Net.put( _id, _net );
        Net2Ids.put( _net, _id );
    }

    public int onlines(){
        return Ids2Usr.size();
    }

    public List< ID > onlineIds(){
        return Ids2Usr.keySet().stream().collect( Collectors.toList() );
    }

    public < T extends SuperUser< ID > > List< T > onlineUsers(){
        return ( List< T > ) Ids2Usr.values().stream().collect( Collectors.toList() );
    }

    public void foreach( Consumer< ? super SuperUser< ID > > _action ){
        Ids2Usr.values().forEach( _action );
    }

    public < T extends SuperUser< ID > > void standby( ChannelHandlerContext _net ){
        T user = find( _net );

        if( user != null ){
            user.standby();
        }
    }

    public < T extends SuperUser< ID > > void recovery( ChannelHandlerContext _net ){
        T user = find( _net );

        if( user != null ){
            user.recovery();
        }
    }
}
