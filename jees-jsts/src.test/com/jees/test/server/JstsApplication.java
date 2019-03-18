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
	public ISupportHandler<ChannelHandlerContext > supportHandler(){
		return new AbsHandlerService<ChannelHandlerContext>() {
			@Override
			public void error ( ChannelHandlerContext _ctx, Throwable _thr ) {
				System.out.println( "ERROR->" + _thr.getMessage() );
			}
		};
	}

	/**
	 * 客户端请求执行器
	 * @return
	 */
	@Bean
	public IRequestHandler<ChannelHandlerContext> requestHandler(){
		return new AbsRequestHandler<ChannelHandlerContext>() {
			@Override
			public boolean before ( ChannelHandlerContext _ctx, int _cmd ) {
				return true;
			}

			@Override
			public void after(ChannelHandlerContext _ctx) {
			}

			@Override
			public void unregist ( ChannelHandlerContext _ctx, Object _msg ) {
			}

			@Override
			public void error(ChannelHandlerContext _ctx, MessageException _msg) {
				System.out.println( "ERROR->" + _msg.getMessage() );
			}
		};
	}

	@Bean
	@Scope( value = INettyHandler.SCOPE_CREATOR )
	public AbsConnectorHandler connectroHandler() {
		return new AbsConnectorHandler() {};
	}
}
