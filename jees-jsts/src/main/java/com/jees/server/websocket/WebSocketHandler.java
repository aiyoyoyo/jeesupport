package com.jees.server.websocket;

import com.jees.server.interf.IChannelHandler;
import com.jees.server.interf.ISocketServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * NettySocket 事件接收器，这里不处理实际数据，由ISupprotService的实现类代为处理。
 * 
 * @author aiyoyoyo
 *
 */
@Log4j2
@Component
@Scope( value = ISocketServer.SCOPE_CREATOR )
public class WebSocketHandler extends SimpleChannelInboundHandler< WebSocketFrame >{
	@Autowired
	private        IChannelHandler< ChannelHandlerContext > handler  = null;

	// == 连接方法序 == //
	@Override
	protected void channelRead0( ChannelHandlerContext _net, WebSocketFrame _msg ){
		handler.request( _net, _msg );
	}

	@Override
	public void handlerAdded( ChannelHandlerContext _net ){
		handler.enter( _net, true );
	}

	@Override
	public void handlerRemoved( ChannelHandlerContext _net ){
		handler.leave( _net );
	}

	@Override
	public void exceptionCaught( ChannelHandlerContext _net, Throwable _thr ){
		handler.error( _net, _thr );
	}

	@Override
	public void userEventTriggered( ChannelHandlerContext _net, Object _obj ){
		handler.trigger( _net, _obj );
	}
}
