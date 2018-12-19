package com.jees.jsts.server.abs;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.core.socket.support.ISocketBase;
import com.jees.core.socket.support.ISupportHandler;
import com.jees.jsts.netty.support.AbsNettyDecoder;
import com.jees.jsts.server.interf.IConnectorService;
import com.jees.jsts.server.interf.IConnectroHandler;
import com.jees.jsts.server.message.Message;
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
    protected Set< Connector >          servers;

    @Autowired
    ISupportHandler                     handler;
    @Getter
    @Setter
    public class Connector implements Runnable, ISocketBase {
        private EventLoopGroup      work;
        private String			    host;
        private int				    port;
        private Channel			    channel;
        private int 			    count;
        private AbsConnectorHandler server;

        public Connector( String _host , String _port ) {
            this.host = _host;
            this.port = Integer.parseInt( _port );
            this.count = 0;
        }

        public void run() {
            this.start();
        }
        @Override
        public void onload() {
            new Thread( this ).start();
        }
        @Override
        public void unload() {
            if ( work != null ) work.shutdownGracefully();
            if( channel != null ) this.channel.closeFuture();
            count = 0;
        }

        public void start() {
            log.info( "连接指定服务器中，尝试第[" + this.count + "]次..." );
            work = new NioEventLoopGroup();
            server = CommonContextHolder.getBean( AbsConnectorHandler.class );
            try {
                Bootstrap b = new Bootstrap();
                server.initialize( host, port );
                b.group( work ).channel( NioSocketChannel.class )
                        .handler( clientInitializer( server ) )
                        .option( ChannelOption.SO_KEEPALIVE , true );

                ChannelFuture future = b.connect( host , port );
                this.channel = future.channel();

                future.sync().channel().closeFuture().sync();
                unload();
            } catch ( Exception e ) {
                log.error( "指定服务器[" + host + ":" + port + "] 连接时发生错误:" + e.toString() , e );
                server.setStatus( IConnectroHandler.STATUS_CONNECTING );
                reload();
            }
        }
        @SuppressWarnings( "unchecked" )
        public void request( Message _msg ) {
            handler.send( this.channel, _msg );
        }
        @Override
        public void reload() {
            log.info( "重新连接指定服务器中，尝试第[" + this.count + "]次..." );
            this.unload();

            int retry = CommonConfig.getInteger( "jees.jsts.connector.retry.max" , 3 );
            if( retry == 0 || this.count ++ < retry ){
                try {
                    Thread.sleep( CommonConfig.getInteger( "jees.jsts.connector.retry.rate" , 10000 ) );
                    this.onload();
                } catch ( InterruptedException e ) {
                }
            }else{
                try {
                    Thread.sleep( CommonConfig.getInteger( "jees.jsts.connector.retry.delay" , 300000 ) );
                    this.onload();
                } catch ( InterruptedException e ) {
                }
            }
        }
    }

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

            Connector sc = new Connector( host , port );
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

    public ChannelInitializer< SocketChannel > clientInitializer ( AbsConnectorHandler _handler ) {
        return new ChannelInitializer< SocketChannel >() {
            @Override
            protected void initChannel( SocketChannel _ch ) throws Exception {
                ChannelPipeline pipeline = _ch.pipeline();
                // 自己的逻辑Handler
                pipeline.addLast( CommonContextHolder.getBean( AbsNettyDecoder.class ) );
                pipeline.addLast( _handler );
            }
        };
    }
}
