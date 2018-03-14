package com.jees.jsts.netty.support;

import io.netty.channel.ChannelInboundHandler;

/**
 * NettySocket连接对象的信息接收器
 * 
 * @author aiyoyoyo
 *
 */
public interface INettyHandler extends ChannelInboundHandler {
	/**
	 * 接收器的对象必须声明为每一次都是新的
	 */
	public String	SCOPE_CREATOR		= "prototype";
}
