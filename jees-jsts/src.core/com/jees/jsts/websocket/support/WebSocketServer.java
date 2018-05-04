package com.jees.jsts.websocket.support ;

import com.jees.common.CommonConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.stereotype.Component ;

import com.jees.core.socket.support.AbsSupportWebSocket;
import com.jees.core.socket.support.ISupportWebSocket;

import io.netty.bootstrap.ServerBootstrap ;
import io.netty.channel.EventLoopGroup ;
import io.netty.channel.nio.NioEventLoopGroup ;
import io.netty.channel.socket.nio.NioServerSocketChannel ;

/**
 * NettyWebSocket服务对象，通过SupportSocket加载。
 * 
 * @author aiyoyoyo
 *
 */
@Component
@Log4j2
public class WebSocketServer extends AbsSupportWebSocket implements ISupportWebSocket, Runnable{
	private EventLoopGroup		boss ;
	private EventLoopGroup		work ;
	private int					port ;
	@Autowired
	private WebSocketInitializer	websocketInitializer ;
	
	public void run(){
		this.start();
	}
	
	@Override
	public void onload() {
		new Thread( this ).start();
	}

	@Override
	public void unload() {
		log.debug( "--WebSocket Server[" + port + "] 停止中..." ) ;
		if ( boss != null ) boss.shutdownGracefully() ;
		if ( work != null ) work.shutdownGracefully() ;
		log.debug( "WebSocket Server[" + port + "] 已停止。"  ) ;
	}

	@Override
	public void reload(){
		log.debug( "--WebSocket Server[" + port + "] 重启中..." ) ;
		super.reload();
		log.debug( "--WebSocket Server[" + port + "] 已重启." ) ;
	}
	
	public void start(){
		log.debug( "--WebSocket Server准备中..." ) ;
		boss = new NioEventLoopGroup() ;
		work = new NioEventLoopGroup() ;
		port = CommonConfig.getInteger(Netty_WebSocket_Port);
		try {

			log.info( "WebSocket Server[" + port + "] 已启动." ) ;
			ServerBootstrap b = new ServerBootstrap() ;

			b.group( boss , work ) ;
			b.channel( NioServerSocketChannel.class ) ;
			b.childHandler( websocketInitializer ) ;
			b.bind( port ).sync().channel().closeFuture().sync() ;
		} catch ( Exception e ) {
			String err_string = e.toString();
			if( err_string.indexOf( "childHandler" ) != -1 ){
				log.error( "WebSocket Server[" + port + "] WebSocketInitializer实例没有找到。" ) ;
			}else{
				log.error( "WebSocket Server[" + port + "] 启动时发生错误:" + e.toString() , e  ) ;
			}
		} finally {
			log.error( "WebSocket Server[" + port + "] 停止中..."  ) ;
			unload();
			log.error( "WebSocket Server[" + port + "] 已停止。"  ) ;
		}
	}
}
