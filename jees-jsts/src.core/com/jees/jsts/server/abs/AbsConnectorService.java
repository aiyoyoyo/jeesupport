package com.jees.jsts.server.abs;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.core.socket.support.ISocketBase;
import com.jees.core.socket.support.ISupportHandler;
import com.jees.jsts.netty.support.AbsNettyDecoder;
import com.jees.jsts.server.interf.IConnectorService;
import com.jees.jsts.server.interf.IConnectroHandler;
import com.jees.jsts.server.message.Message;
import com.jees.jsts.server.support.Connector;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 提供连接器的服务，用于服务器之间的通讯
 */
@Log4j2
public abstract class AbsConnectorService implements IConnectorService{
    @Getter
    Set< Connector >          servers;

    @Override
    public void onload() {
        servers = new HashSet<>();
        //连接中心服务器
        StringTokenizer st = CommonConfig.getStringTokenizer( "jees.jsts.connector.hosts" );

        while ( st.hasMoreTokens() ) {
            String connect_info = st.nextToken().trim();

            int idx0 = connect_info.indexOf( ":" );
            String host = connect_info.substring( 0 , idx0 );
            String port = connect_info.substring( idx0 + 1 );

            Connector sc = CommonContextHolder.getBean( Connector.class );
            sc.initialize( host , port );
            servers.add( sc );
            sc.onload();
        }
    }

    @Override
    public void unload() {
    }

    @Override
    public void reload() {
        servers.forEach( s ->{
            s.reload();
        } );
    }

    @Override
    public void reloadDisconnect(){
        servers.stream().filter( s-> s.getServer().getStatus() == IConnectroHandler.STATUS_DISCONNECT )
                .forEach( s ->{
            s.reload();
        } );
    }
}
