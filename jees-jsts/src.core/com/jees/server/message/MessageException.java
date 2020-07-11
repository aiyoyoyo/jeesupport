package com.jees.server.message;

import lombok.Getter;

/**
 * 用于错误消息的封装类，该错误会返回客户端指定的ID和参数,多数报错原因是参数不正确或者非法信息
 * 
 * @author aiyoyoyo
 *
 */
public class MessageException extends RuntimeException{
	private static final long serialVersionUID = -7777663065117915542L;

	@Getter
	private Message msg;

	public MessageException( String _tip ){
		super( _tip );
	}

	/**
	 * 中断后续操作，通知客户端
	 *
	 * @param _id
	 * @param _params
	 * @throws MessageException
	 */
	public static void thrs( int _id, Object... _params ) throws MessageException{
		String tip = "写回异常消息（中断模式）:";

		MessageException me = new MessageException( tip );
		me.msg = new Message( _id, _params );

		throw me;
	}

	/**
	 * 不中断后续操作，通知客户端
	 *
	 * @param _id
	 * @param _params
	 * @return
	 */
	public static Message mesg( int _id, Object... _params ){
		String tip = "写回异常消息:";

		MessageException me = new MessageException( tip );
		me.msg = new Message( _id, _params );

		return me.msg;
	}

	/**
	 * 中断后续操作，不通知客户端
	 *
	 * @param _id
	 * @param _err
	 * @param _params
	 */
	public static void fail( int _id, int _err, Object... _params ) throws MessageException{
		String tip = "发生异常:";

		MessageException me = new MessageException( tip );
		me.msg = new Message( _id, _err, _params );

		throw me;
	}
}
