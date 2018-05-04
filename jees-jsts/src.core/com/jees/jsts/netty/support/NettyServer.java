package com.jees.jsts.netty.support ;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component ;

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
@Component
@Log4j2
public class NettyServer extends AbsSupportSocket implements ISupportSocket, Runnable{
	private EventLoopGroup		boss ;
	private EventLoopGroup		work ;
	private int 				port ;

	public void run(){
		this.start();
	}
	
	@Override
	public void onload() {
		new Thread( this ).start();
	}

	@Override
	public void unload() {
		log.debug( "--Socket Server[" + port + "] 停止中..." ) ;
		if ( boss != null ) boss.shutdownGracefully() ;
		if ( work != null ) work.shutdownGracefully() ;
		log.debug( "Socket Server[" + port + "] 已停止。"  ) ;
	}
	
	@Override
	public void reload(){
		log.debug( "--Socket Server[" + port + "] 重启中..." ) ;
		super.reload();
		log.debug( "--Socket Server[" + port + "] 已重启." ) ;
	}
	
	public void start(){
		log.debug( "--Socket Server准备中..." ) ;
		boss = new NioEventLoopGroup() ;
		work = new NioEventLoopGroup() ;
		port = CommonConfig.getInteger(Netty_Socket_Port);
		try {
			
			log.info( "Socket Server[" + port + "] 已启动." ) ;
			ServerBootstrap b = new ServerBootstrap() ;

			b.group( boss , work ) ;
			b.channel( NioServerSocketChannel.class ) ;
			b.childHandler( CommonContextHolder.getBean( NettyInitializer.class ) ) ;
			b.bind( port ).sync().channel().closeFuture().sync() ;
		} catch ( Exception e ) {
			String err_string = e.toString();
			if( err_string.indexOf( "childHandler" ) != -1 ){
				log.error( "Socket Server[" + port + "] NettyInitializer实例没有找到。" ) ;
			}else{
				log.error( "Socket Server[" + port + "] 启动时发生错误:" + e.toString() , e  ) ;
			}
		} finally {
			unload();
		}
	}
}
