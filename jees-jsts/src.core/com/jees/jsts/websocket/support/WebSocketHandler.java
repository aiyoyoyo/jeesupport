package com.jees.jsts.websocket.support;

import com.jees.common.CommonConfig;
import com.jees.core.socket.support.ISocketBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jees.core.socket.support.ISupportHandler;
import com.jees.jsts.netty.support.INettyHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * NettySocket 事件接收器，这里不处理实际数据，由ISupprotService的实现类代为处理。
 * 
 * @author aiyoyoyo
 *
 */
@Component
@Scope( value = INettyHandler.SCOPE_CREATOR )
public class WebSocketHandler extends SimpleChannelInboundHandler< WebSocketFrame > {
	private static Logger										logger	= LogManager
					.getLogger( WebSocketHandler.class );

	private static Long											maxLost = null;
	private long												lastTime = 0L;
	private boolean												stand = false;
	@Autowired
	private ISupportHandler< ChannelHandlerContext , Object >	handler = null;

	private String _handler_info( ChannelHandlerContext _ctx , String _event ) {
		return "--[WebSocket客户端连接信息] " + _event + "---------------------------------"
				+ "\n IP       =" + _ctx.channel().remoteAddress()
				+ "\n Register =" + _ctx.channel().isRegistered()
				+ "\n Open     =" + _ctx.channel().isOpen()
				+ "\n Active   =" + _ctx.channel().isActive()
				+ "\n Write    =" + _ctx.channel().isWritable()
				+ "\n--[WebSocket客户端连接信息]---------------------------------";
	}

	// == 连接方法序 == //
	@Override
	protected void channelRead0( ChannelHandlerContext _ctx , WebSocketFrame _msg ){
		logger.debug( _handler_info( _ctx , "channelRead" ) );
		if ( _msg != null ) handler.receive( _ctx , _msg );
	}

	@Override
	public void handlerAdded( ChannelHandlerContext _ctx ){
		logger.debug( _handler_info( _ctx , "handlerAdded" ) );
		handler.enter( _ctx );
	}

	@Override
	public void handlerRemoved( ChannelHandlerContext _ctx ) {
		logger.debug( _handler_info( _ctx , "handlerRemoved" ) );
		handler.leave( _ctx );
	}

	@Override
	public void exceptionCaught( ChannelHandlerContext _ctx , Throwable _thr ) {
		logger.error( _handler_info( _ctx , "exceptionCaught" + _thr.toString() ) );
		handler.error( _ctx , _thr );
	}

	/**
	 * 利用事件变化来做心跳检测
	 */
	@Override
	public void userEventTriggered( ChannelHandlerContext _ctx , Object _obj ) {
		logger.debug( _handler_info( _ctx , "userEventTriggered" ) );
		if( maxLost == null )
			maxLost = CommonConfig.getLong( ISocketBase.Netty_WebSocket_LostTime );
		if ( _obj instanceof IdleStateEvent ) {
			IdleStateEvent event = ( IdleStateEvent ) _obj;
			if ( event.state() == IdleState.READER_IDLE ) {
				long now = System.currentTimeMillis();
				long lost = now - lastTime;
				logger.debug( _handler_info( _ctx , "待机时长:" + lost ) );
				if( lastTime == 0L ) {
					lastTime = now;
				}else if( !stand && lost >= maxLost ){
					handler.standby( _ctx );
					stand = true;
				}
			}else if ( stand && event.state() == IdleState.WRITER_IDLE ) {
				logger.debug( _handler_info( _ctx , "待机恢复." ) );
				handler.recovery( _ctx );
				stand = false;
				lastTime = 0L;
			}
		} else {
			logger.warn( _handler_info( _ctx , " was discard" ) );
		}
	}
}
