package com.jees.jsts.netty.support ;

import org.apache.logging.log4j.LogManager ;
import org.apache.logging.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.context.annotation.Scope ;
import org.springframework.stereotype.Component ;

import com.jees.core.socket.support.ISupportHandler;

import io.netty.channel.ChannelHandlerContext ;
import io.netty.handler.timeout.IdleState ;
import io.netty.handler.timeout.IdleStateEvent ;

/**
 * NettySocket 事件接收器，这里不处理实际数据，由ISupprotService的实现类代为处理。
 * 
 * @author aiyoyoyo
 *
 */
@Component
@Scope( value = INettyHandler.NETTY_CREATOR )
public class NettyHandler implements INettyHandler {
	private static Logger	logger	= LogManager.getLogger( NettyHandler.class ) ;

	private int				lost	= 0 ;

	@Autowired
	private ISupportHandler< ChannelHandlerContext, Object >	service ;

	private String _handler_info( ChannelHandlerContext _ctx, String _event ) {
		String str_channel = ""
						+ "--[Client Info] " + _event + "---------------------------------"
						+ "\n IP     =" + _ctx.channel().remoteAddress()
						+ "\n Regist =" + _ctx.channel().isRegistered()
						+ "\n Open   =" + _ctx.channel().isOpen()
						+ "\n Active =" + _ctx.channel().isActive()
						+ "\n Write  =" + _ctx.channel().isWritable()
						+ "\n--[Client Info]---------------------------------";
		
		return str_channel ;
	}

	// == 连接方法序 == //
	@Override
	public void handlerAdded( ChannelHandlerContext _ctx ) throws Exception {
		logger.debug( _handler_info( _ctx, "handlerAdded" ) ) ;
		service.enter( _ctx ) ;
	}

	@Override
	public void channelRegistered( ChannelHandlerContext _ctx ) throws Exception {
		logger.debug( _handler_info( _ctx, "channelRegistered" ) ) ;
	}

	@Override
	public void channelActive( ChannelHandlerContext _ctx ) throws Exception {
		logger.debug( _handler_info( _ctx, "channelActive" ) ) ;
	}

	// == 接收方法序 == //
	@Override
	public void channelRead( ChannelHandlerContext _ctx , Object _obj ) throws Exception {
		logger.debug( _handler_info( _ctx, "channelRead" ) ) ;
		
		if( _obj != null )
			service.receive( _ctx , _obj ) ;
	}

	@Override
	public void channelReadComplete( ChannelHandlerContext _ctx ) throws Exception {
		logger.debug( _handler_info( _ctx, "channelReadComplete" ) ) ;
	}

	// == 断开方法序 == //
	@Override
	public void channelInactive( ChannelHandlerContext _ctx ) throws Exception {
		logger.debug( _handler_info( _ctx, "channelInactive" ) ) ;
	}

	@Override
	public void channelUnregistered( ChannelHandlerContext _ctx ) throws Exception {
		logger.debug( _handler_info( _ctx, "channelUnregistered" ) ) ;
	}

	@Override
	public void handlerRemoved( ChannelHandlerContext _ctx ) throws Exception {
		logger.debug( _handler_info( _ctx, "handlerRemoved" ) ) ;
		service.leave( _ctx ) ;
	}

	// == 异常方法序 == //
	@Override
	public void exceptionCaught( ChannelHandlerContext _ctx , Throwable _thr ) throws Exception {
		logger.error( _handler_info( _ctx, "exceptionCaught" + _thr.toString() ) ) ;
		service.error( _ctx , _thr ) ;
	}

	@Override
	public void channelWritabilityChanged( ChannelHandlerContext _ctx ) throws Exception {
		logger.debug( _handler_info( _ctx, "channelWritabilityChanged" ) ) ;
	}

	/**
	 * 利用事件变化来做心跳检测
	 */
	@Override
	public void userEventTriggered( ChannelHandlerContext _ctx , Object _obj ) throws Exception {
		logger.debug( _handler_info( _ctx, "userEventTriggered" ) ) ;

		if ( _obj instanceof IdleStateEvent ) {
			IdleStateEvent event = ( IdleStateEvent ) _obj ;
			if ( event.state() == IdleState.READER_IDLE ) {
				lost++ ;
				logger.debug( _handler_info( _ctx, " inactive with=" + lost ) ) ;
				if ( lost > 2 ) {
					service.stand( _ctx ) ;
					logger.debug( _handler_info( _ctx, " was stand with=" + lost ) ) ;
				}
			}
		} else {
			logger.warn( _handler_info( _ctx, " was discard" ) ) ;
		}
	}
}
