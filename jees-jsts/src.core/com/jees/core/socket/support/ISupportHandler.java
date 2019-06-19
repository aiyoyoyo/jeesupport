package com.jees.core.socket.support;

import io.netty.channel.ChannelHandlerContext;

/**
 * Socket服务的内容接口
 * @author aiyoyoyo
 *
 * @param <T> 客户端对象模版
 */
public interface ISupportHandler<T extends ChannelHandlerContext > {
	
	/**
	 * 接收数据
	 * @param _ctx 客户端对象
	 * @param _obj 数据内容
	 */
	void receive( T _ctx , Object _obj ) ;

	/**
	 * 发送数据
	 * @param _ctx 客户端对象
	 * @param _obj 数据内容
	 */
	void send( Object _obj, T _ctx  );
	/**
	 * 连接建立
	 * @param _ctx 客户端对象
	 * @param _ws 是否WebSocket
	 */
	void enter( T _ctx, boolean _ws ) ;
	
	/**
	 * 连接断开
	 * @param _ctx 客户端对象
	 */
	void leave( T _ctx ) ;
	
	/**
	 * 连接待机
	 * @param _ctx 客户端对象
	 */
	void standby( T _ctx ) ;

	/**
	 * 待机恢复
	 * @param _ctx 客户端对象
	 */
	void recovery( T _ctx );

	/**
	 * 连接过程中出现错误
	 * @param _ctx 客户端对象
	 * @param _thr 错误类型
	 */
	void error( T _ctx, Throwable _thr ) ;

}
