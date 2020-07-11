package com.jees.server.socket;

import com.jees.server.interf.IChannelHandler;
import com.jees.server.interf.ISocketServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
@Component
@Scope( value = ISocketServer.SCOPE_CREATOR )
@Log4j2
public class SocketHandler extends ChannelInboundHandlerAdapter{
	@Autowired
	private IChannelHandler< ChannelHandlerContext > 		handler = null;
	// == 连接方法序 == //
	@Override
	public void handlerAdded( ChannelHandlerContext _net ){
		handler.enter( _net, false );
	}

	// == 接收方法序 == //
	@Override
	public void channelRead( ChannelHandlerContext _net, Object _obj ){
		handler.request( _net, _obj );
	}

	// == 断开方法序 == //
	@Override
	public void handlerRemoved( ChannelHandlerContext _net ){
		handler.leave( _net );
	}

	// == 状态方法序 == //
	@Override
	public void exceptionCaught( ChannelHandlerContext _net, Throwable _thr ){
		handler.error( _net, _thr );
	}

	@Override
	public void userEventTriggered( ChannelHandlerContext _net, Object _obj ){
		handler.trigger( _net, _obj );
	}
}