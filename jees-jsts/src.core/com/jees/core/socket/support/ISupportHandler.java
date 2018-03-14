package com.jees.core.socket.support;

/**
 * Socket服务的内容接口
 * @author aiyoyoyo
 *
 * @param <T> 客户端对象模版
 */
public interface ISupportHandler<T, M> {
	
	/**
	 * 接收数据
	 * @param _ctx 客户端对象
	 * @param _obj 数据内容
	 */
	public void receive( T _ctx , M _obj ) ;
	
	/**
	 * 连接建立
	 * @param _ctx 客户端对象
	 */
	public void enter( T _ctx ) ;
	
	/**
	 * 连接断开
	 * @param _ctx 客户端对象
	 */
	public void leave( T _ctx ) ;
	
	/**
	 * 连接待机
	 * @param _ctx 客户端对象
	 */
	public void standby( T _ctx ) ;

	/**
	 * 待机恢复
	 * @param _ctx 客户端对象
	 */
	public void recovery( T _ctx );
	/**
	 * 连接过程中出现错误
	 * @param _ctx 客户端对象
	 * @param _thr 错误类型
	 */
	public void error( T _ctx, Throwable _thr ) ;

}
