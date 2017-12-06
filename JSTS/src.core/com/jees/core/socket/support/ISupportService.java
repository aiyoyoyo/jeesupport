package com.jees.core.socket.support;

/**
 * 命令的对接内容
 * @author aiyoyoyo
 *
 * @param <C, M>
 */
public interface ISupportService<C, M> {
	/**
	 * 消息接收
	 * @param _ctx
	 * @param _msg
	 */
	public void request( C _ctx , M _msg ) ;
	/**
	 * 消息回复
	 * @param _ctx
	 * @param _msg
	 */
	public void response( C _ctx , M _msg );
}
