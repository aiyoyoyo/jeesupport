package com.jees.core.socket.support;

import com.jees.jsts.server.message.MessageException;

/**
 * 命令的对接内容
 * @author aiyoyoyo
 *
 * @param <C, M>
 */
public interface ISupportService<C, M> {
	/**
	 * 收到请求
	 * @param _ctx
	 * @param _obj
	 */
	void request( C _ctx , Object _obj ) ;
	/**
	 * 收到回复
	 * @param _ctx
	 * @param _msg
	 */
	void response( C _ctx , Object _msg );

	void handler( C _ctx , Object _obj );

	boolean before( C _ctx, M _msg );

	void after( C _ctx );

	void error( C _ctx, MessageException _ex );

	void exit( C _ctx );
}
