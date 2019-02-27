package com.jees.test.server;

import com.jees.common.CommonContextHolder;
import com.jees.core.socket.support.ISupportHandler;
import com.jees.jsts.netty.support.INettyHandler;
import com.jees.jsts.server.abs.*;
import com.jees.jsts.server.interf.*;
import com.jees.jsts.server.message.Message;
import com.jees.jsts.server.message.MessageException;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;

@ComponentScan( "com.jees" )
public class JstsApplication {

	@SuppressWarnings( "resource" )
	public static void main( String[] args ) {
		SpringApplication.run(JstsApplication.class, args);
		CommonContextHolder.getBean( IServerService.class ).onload();
	}
	// 以下为自定义实现 ======
	@Bean
	public IServerService serverService(){
		return new AbsServerService(){};
	}

	@Bean
	public IConnectorService connectorService(){
		return new AbsConnectorService(){};
	}
	/**
	 * 客户端事件处理器
	 * @return
	 */
	@Bean
	public ISupportHandler<ChannelHandlerContext, Object > supportHandler(){
		return new AbsHandlerService<ChannelHandlerContext, Object>() {
			@SuppressWarnings( "unchecked" )
			@Override
			/** 收到客户端消息 **/
			public void receive(ChannelHandlerContext _ctx, Object _obj) {
				CommonContextHolder.getBean( IRequestHandler.class ).request( _ctx, _obj );
			}

			@Override
			/** 客户端连接 **/
			public void enter(ChannelHandlerContext _ctx) {
			}

			@Override
			/** 客户端断开 **/
			public void leave(ChannelHandlerContext _ctx) {}

			@Override
			/** 客户端中断 **/
			public void standby(ChannelHandlerContext _ctx) {}

			@Override
			/** 客户端恢复连接 **/
			public void recovery(ChannelHandlerContext _ctx) {}

			@Override
			/** 客户端连接异常 **/
			public void error(ChannelHandlerContext _ctx, Throwable _thr) {}
		};
	}

	/**
	 * 客户端请求执行器
	 * @return
	 */
	@Bean
	public IRequestHandler<ChannelHandlerContext, Message > requestHandler(){
		return new AbsRequestHandler<ChannelHandlerContext, Message>() {
			@Override
			public void request(ChannelHandlerContext _ctx, Object _msg) {
				this.handler( _ctx, _msg );
			}

			@Override
			public void response(ChannelHandlerContext _ctx, Object _msg) {
			}

			@Override
			public boolean before( ChannelHandlerContext _ctx, Message _msg) {
				return true;
			}

			@Override
			public void after(ChannelHandlerContext _ctx) {
			}

			@Autowired
			ISupportHandler 		handler;
			@Override
			public void unregist ( ChannelHandlerContext _ctx, Message _msg ) {
				Message	msg = new Message();
				msg.setId(100);
				msg.setType( Message.TYPE_WEBSOCKET );
				msg.add( 123 );
				msg.add( 123L );
				msg.add( 123.00F );
				msg.add( 123D );
				msg.add( "123" );
				msg.add( false );
				handler.send( _ctx , msg );
			}

			@Override
			public void error(ChannelHandlerContext _ctx, MessageException _msg) {
			}

			@Override
			public void exit(ChannelHandlerContext _ctx) {
			}
		};
	}

	@Bean
	@Scope( value = INettyHandler.SCOPE_CREATOR )
	public AbsConnectorHandler connectroHandler() {
		return new AbsConnectorHandler() {
			@Autowired
			ISupportHandler 		handler;

			@SuppressWarnings( "unchecked" )
			@Override
			public void channelRead(ChannelHandlerContext _ctx , Object _obj ) {
				CommonContextHolder.getBean( IResponseHandler.class ).response( _ctx, _obj );
			}

			@SuppressWarnings( "unchecked" )
			@Override
			public void channelActive(ChannelHandlerContext _ctx) throws Exception {
				super.channelActive( _ctx );
			}
		};
	}
	/**
	 * 服务器返回执行器
	 */
	@Bean
	public IResponseHandler<ChannelHandlerContext, Message > responseHandler(){
		return new AbsResponseHandler<ChannelHandlerContext, Message>() {
			@Override
			public void request(ChannelHandlerContext _ctx, Object _msg) {
			}

			@Override
			public void response(ChannelHandlerContext _ctx, Object _msg) {
				this.handler(_ctx, _msg);
			}

			@Override
			public boolean before( ChannelHandlerContext _ctx, Message _msg) {
				return true;
			}

			@Override
			public void after(ChannelHandlerContext _ctx) {
			}

			@Override
			public void unregist ( ChannelHandlerContext _ctx, Message _msg ) {
			}

			@Override
			public void error(ChannelHandlerContext _ctx, MessageException _msg) {
			}

			@Override
			public void exit(ChannelHandlerContext _ctx) {
			}
		};
	}
}
