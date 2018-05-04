package com.jees.jsts.netty.support;

import com.jees.common.CommonConfig;
import com.jees.core.socket.support.ISocketBase;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jees.core.socket.support.ISupportHandler;

import io.netty.channel.ChannelHandlerContext;
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
@Log4j2
public class NettyHandler extends ChannelInboundHandlerAdapter implements INettyHandler {
	private static Long											maxLost = null;
	private long												lastTime = 0L;
	private boolean												stand = false;
	@Autowired
	private ISupportHandler< ChannelHandlerContext , Object >	handler = null;

	private String _handler_info( ChannelHandlerContext _ctx , String _event ) {
		return "--[Socket客户端连接信息] 事件：" + _event + "---------------------------------"
						+ "\n IP       =" + _ctx.channel().remoteAddress()
						+ "\n Register =" + _ctx.channel().isRegistered()
						+ "\n Open     =" + _ctx.channel().isOpen()
						+ "\n Active   =" + _ctx.channel().isActive()
						+ "\n Write    =" + _ctx.channel().isWritable()
						+ "\n--[Socket客户端连接信息]---------------------------------";
	}

	// == 连接方法序 == //
	@Override
	public void handlerAdded( ChannelHandlerContext _ctx ){
		log.debug( _handler_info( _ctx , "handlerAdded" ) );
		handler.enter( _ctx );
	}
	// == 接收方法序 == //
	@Override
	public void channelRead( ChannelHandlerContext _ctx , Object _obj ) {
		log.debug( _handler_info( _ctx , "channelRead" ) );
		handler.receive( _ctx , _obj );
	}

	// == 断开方法序 == //
	@Override
	public void handlerRemoved( ChannelHandlerContext _ctx ){
		log.debug( _handler_info( _ctx , "handlerRemoved" ) );
		handler.leave( _ctx );
	}

	// == 状态方法序 == //
	@Override
	public void exceptionCaught( ChannelHandlerContext _ctx , Throwable _thr ) {
		log.error( _handler_info( _ctx , "exceptionCaught" + _thr.toString() ) );
		handler.error( _ctx , _thr );
	}

	/**
	 * 利用事件变化来做心跳检测
	 */
	@Override
	public void userEventTriggered( ChannelHandlerContext _ctx , Object _obj ){
		log.debug( _handler_info( _ctx , "userEventTriggered" ) );
		if( maxLost == null )
			maxLost = CommonConfig.getLong( ISocketBase.Netty_Socket_LostTime );
		if ( _obj instanceof IdleStateEvent ) {
			IdleStateEvent event = ( IdleStateEvent ) _obj;
			if ( event.state() == IdleState.READER_IDLE ) {
				long now = System.currentTimeMillis();
				long lost = now - lastTime;
				log.debug( _handler_info( _ctx , "待机时长:" + lost ) );
				if( lastTime == 0L ) {
					lastTime = now;
				}else if( !stand && lost >= maxLost ){
					handler.standby( _ctx );
					stand = true;
				}
			}else if ( stand && event.state() == IdleState.WRITER_IDLE ) {
				log.debug( _handler_info( _ctx , "待机恢复." ) );
				handler.recovery( _ctx );
				stand = false;
				lastTime = 0L;
			}
		} else {
			log.warn( _handler_info( _ctx , " was discard" ) );
		}
	}
}
