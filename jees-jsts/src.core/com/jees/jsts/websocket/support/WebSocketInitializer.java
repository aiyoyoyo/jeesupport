package com.jees.jsts.websocket.support;

import java.util.concurrent.TimeUnit;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.core.socket.support.ISocketBase;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * Netty WebSocket初始化实现类
 * 参考：https://www.cnblogs.com/miller-zou/p/7002070.html
 * @author aiyoyoyo
 *
 */
@Log4j2
@Component
public class WebSocketInitializer extends ChannelInitializer< SocketChannel > {
	@Autowired
	SSLContext sslContext1;

	@Override
	protected void initChannel( SocketChannel _channel ) throws Exception {
		ChannelPipeline pipeline = _channel.pipeline();

		// 是否使用客户端模式
		if( CommonConfig.getBoolean( "jees.jsts.websocket.ssl.enable", false ) ){
			SSLEngine engine = sslContext1.createSSLEngine();
//			 是否需要验证客户端
			engine.setUseClientMode(false);
//			engine.setNeedClientAuth(false);
			pipeline.addFirst("ssl", new SslHandler( engine ));
		}

		pipeline.addLast( new IdleStateHandler( 100 , 0 , 0 , TimeUnit.SECONDS ) );
		pipeline.addLast( new HttpServerCodec() );
		pipeline.addLast( new ChunkedWriteHandler() );
		pipeline.addLast( new HttpObjectAggregator( 8192 ) );
		pipeline.addLast( new WebSocketServerProtocolHandler( CommonConfig.getString( ISocketBase.Netty_WebSocket_Url, "/" ) ) );
		pipeline.addLast( CommonContextHolder.getBean( WebSocketHandler.class ) );
	}


}
