package com.jees.core.socket.support;

import com.jees.jsts.server.message.MessageException;

/**
 * 命令的对接内容
 * @author aiyoyoyo
 *
 * @param <C>
 */
public interface ISupportService<C> {
	/**
	 * 收到请求
	 * @param _ctx
	 * @param _obj
	 * @param _ws
	 */
	void request( C _ctx , Object _obj, boolean _ws ) ;

	void handler( C _ctx , Object _obj );

	boolean before( C _ctx, int _cmd );

	void after( C _ctx );

	void unregist( C _ctx, Object _msg );

	void error( C _ctx, MessageException _ex );
}
