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
	 * 接收器的解码类，由Spring代为注入
	 */
	public String	NETTY_DECODER	= "nettyDecoder";
	/**
	 * 接收器的实例名，由Spring代为注入
	 */
	public String	NETTY_HANDLER	= "nettyHandler";

	/**
	 * 接收器的对象必须声明为每一次都是新的
	 */
	public String	SCOPE_CREATOR	= "prototype";

	/**
	 * 接收器的数据处理服务实例名，该接口继承ISupportService，通过Spring注解自动载入
	 */
	public String	CENTER_SERVICE	= "centerService";
}
