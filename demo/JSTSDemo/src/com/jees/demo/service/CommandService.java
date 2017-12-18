package com.jees.demo.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jees.core.socket.support.ISupportService;
import com.jees.demo.models.DemoModel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

@Service
public class CommandService implements ISupportService< ChannelHandlerContext , ByteBuf > {
	private static Logger logger		= LogManager.getLogger( CommandService.class );
	
	@Autowired
	DemoModel	demoModel;
	
	@Override
	public void request( ChannelHandlerContext _ctx , ByteBuf _request ) {
		final int		UserLogin = 1;
		final int		UserRegist = 2;
		int cmd = _request.readInt();
		logger.info( "CommandService do command: CMD=[" + cmd + "]" );
		ByteBuf response = _ctx.alloc().buffer();
		
		//根据cmd来识别使用哪个模块来处理，
		switch( cmd ){
			case UserLogin:
				demoModel.requestLogin( _request, response );
				break;
			case UserRegist:
				demoModel.requestRegist( _request, response );
				break;
		}
		
		response( _ctx, response );
	}
	
	@Override
	public void response( ChannelHandlerContext _ctx , ByteBuf _msg ) {
		_ctx.writeAndFlush( _msg );
	}
}
