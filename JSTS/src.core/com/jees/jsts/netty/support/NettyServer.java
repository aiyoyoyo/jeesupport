package com.jees.jsts.netty.support ;

import org.apache.logging.log4j.LogManager ;
import org.apache.logging.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.stereotype.Component ;

import com.jees.core.socket.common.CommonConfig;
import com.jees.core.socket.support.AbsSupportSocket;
import com.jees.core.socket.support.ISupportSocket;

import io.netty.bootstrap.ServerBootstrap ;
import io.netty.channel.EventLoopGroup ;
import io.netty.channel.nio.NioEventLoopGroup ;
import io.netty.channel.socket.nio.NioServerSocketChannel ;

/**
 * NettySocket服务对象，通过SupportSocket加载。
 * 
 * @author aiyoyoyo
 *
 */
@Component( value = ISupportSocket.SOCKET_SUPER )
public class NettyServer extends AbsSupportSocket implements Runnable{
	private static Logger		logger	= LogManager.getLogger( NettyServer.class ) ;
	private EventLoopGroup		boss ;
	private EventLoopGroup		work ;
	@Autowired
	private NettyInitializer	nettyInitializer ;
	
	public void run(){
		this.start();
	}
	
	@Override
	public void onload() {
		new Thread( this ).start();
	}

	@Override
	public void unload() {
		logger.debug( "--Socket Server will stop------------" ) ;
		if ( boss != null ) boss.shutdownGracefully() ;
		if ( work != null ) work.shutdownGracefully() ;
	}
	
	@Override
	public void reload(){
		logger.debug( "--Socket Server will restart------------" ) ;
		super.reload();
	}
	
	public void start(){
		logger.debug( "--Socket Server will start------------" ) ;
		boss = new NioEventLoopGroup() ;
		work = new NioEventLoopGroup() ;
		int port = CommonConfig.getInteger( SOCKET_PORT1 );
		try {
			
			logger.info( "Netty Server[" + port + "] started..." ) ;
			ServerBootstrap b = new ServerBootstrap() ;

			b.group( boss , work ) ;
			b.channel( NioServerSocketChannel.class ) ;
			b.childHandler( nettyInitializer ) ;
			b.bind( port ).sync().channel().closeFuture().sync() ;
		} catch ( Exception e ) {
			String err_string = e.toString();
			if( err_string.indexOf( "childHandler" ) != -1 ){
				logger.error( "Netty Server[" + port + "] NettyInitializer can't find." ) ;
			}else{
				logger.error( "Netty Server[" + port + "] onload err:" + e.toString() , e  ) ;
			}
		} finally {
			logger.error( "Netty Server[" + port + "] will be unload..."  ) ;
			unload();
		}
	}
}
