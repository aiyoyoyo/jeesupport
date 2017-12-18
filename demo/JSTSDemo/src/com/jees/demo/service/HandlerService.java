package com.jees.demo.service;

import org.springframework.stereotype.Service;

import com.jees.core.socket.common.CommonContextHolder;
import com.jees.core.socket.common.ICommonConfig;
import com.jees.core.socket.support.ISupportHandler;
import com.jees.core.socket.support.ISupportService;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

@Service
public class HandlerService implements ISupportHandler< ChannelHandlerContext , Object > , ICommonConfig {

	@Override
	public void receive( ChannelHandlerContext _ctx , Object _obj ) {
		@SuppressWarnings( "unchecked" )
		ISupportService< ChannelHandlerContext , ByteBuf > cmd = CommonContextHolder.getBean( ISupportService.class );
		cmd.request( _ctx , ( ByteBuf ) _obj );
	}

	@Override
	public void enter( ChannelHandlerContext _ctx ) {
		// TODO 建立连接触发
	}

	@Override
	public void leave( ChannelHandlerContext _ctx ) {
		// TODO 连接断开触发
	}

	@Override
	public void stand( ChannelHandlerContext _ctx ) {
		// TODO 默认的心跳检测机制，超过1分钟未发生任何请求的客户端会触发
	}

	@Override
	public void error( ChannelHandlerContext _ctx , Throwable _thr ) {
		// TODO 当事件变化，或者客户端请求触发异常时，触发
	}
}
