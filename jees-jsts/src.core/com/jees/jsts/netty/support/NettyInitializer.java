package com.jees.jsts.netty.support ;

import java.util.concurrent.TimeUnit ;

import com.jees.common.CommonContextHolder;
import org.apache.logging.log4j.LogManager ;
import org.apache.logging.log4j.Logger ;
import org.springframework.stereotype.Component ;

import io.netty.channel.ChannelInitializer ;
import io.netty.channel.ChannelPipeline ;
import io.netty.channel.socket.SocketChannel ;
import io.netty.handler.timeout.IdleStateHandler ;

/**
 * Netty初始化实现类，这里暂无更多接入方式。
 * 
 * @author aiyoyoyo
 *
 */
@Component
public class NettyInitializer extends ChannelInitializer< SocketChannel > {
	private static Logger logger = LogManager.getLogger( NettyInitializer.class ) ;
	@Override
	protected void initChannel( SocketChannel _channel ) throws Exception {
		logger.info( "Netty Server init channel: " + _channel.isActive() ) ;

		ChannelPipeline pipeline = _channel.pipeline() ;

		pipeline.addLast( CommonContextHolder.getBean( AbsNettyDecoder.class ) ) ;
		pipeline.addLast( new IdleStateHandler( 100 , 0 , 0 , TimeUnit.SECONDS ) ) ;
		pipeline.addLast( CommonContextHolder.getBean( NettyHandler.class ) ) ;
	}
}
