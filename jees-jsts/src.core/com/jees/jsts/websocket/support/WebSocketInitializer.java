package com.jees.jsts.websocket.support;

import java.util.concurrent.TimeUnit;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.core.socket.support.ISocketBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Netty WebSocket初始化实现类
 * 参考：https://www.cnblogs.com/miller-zou/p/7002070.html
 * @author aiyoyoyo
 *
 */
@Component
public class WebSocketInitializer extends ChannelInitializer< SocketChannel > {
	private static Logger logger = LogManager.getLogger( WebSocketInitializer.class );

	@Override
	protected void initChannel( SocketChannel _channel ) throws Exception {
		logger.info( "Netty WebSocket Server init channel: " + _channel.isActive() );

		ChannelPipeline pipeline = _channel.pipeline();

		pipeline.addLast( new IdleStateHandler( 100 , 0 , 0 , TimeUnit.SECONDS ) );
		// HttpServerCodec: 针对http协议进行编解码
		pipeline.addLast( new HttpServerCodec() );
		// ChunkedWriteHandler分块写处理，文件过大会将内存撑爆
		pipeline.addLast( new ChunkedWriteHandler() );
		/**
		 * 作用是将一个Http的消息组装成一个完成的HttpRequest或者HttpResponse，那么具体的是什么
		 * 取决于是请求还是响应, 该Handler必须放在HttpServerCodec后的后面
		 */
		pipeline.addLast( new HttpObjectAggregator( 8192 ) );
		// 用于处理websocket, /ws为访问websocket时的uri
		pipeline.addLast( new WebSocketServerProtocolHandler( CommonConfig.get( ISocketBase.Netty_WebSocket_Url ) ) );
		pipeline.addLast( CommonContextHolder.getBean( WebSocketHandler.class ) );

	}
}
