package com.jees.jsts.server.message;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * 用于错误消息的封装类，该错误会返回客户端指定的ID和参数,多数报错原因是参数不正确或者非法信息
 * 
 * @author aiyoyoyo
 *
 */
public class MessageException extends RuntimeException {
	private static final long	serialVersionUID	= - 7777663065117915542L;

	@Getter
	private Message				msg;

	public MessageException( String _tip ) {
		super( _tip );
	}

	private static Message _s_set_message( long _uid, int _id, Object... _params ) {
		Message msg = new Message();
		msg.setId( _id );
		msg.setUserId( _uid );

		for ( Object o : _params ) {
			if ( o instanceof Integer ) msg.add( ( Integer ) o );
			else if ( o instanceof String ) msg.add( ( String ) o );
			else if ( o instanceof Boolean ) msg.add( ( Boolean ) o );
			else if ( o instanceof Float ) msg.add( ( Float ) o );
			else if ( o instanceof Double ) msg.add( ( Double ) o );
			else if ( o instanceof Long ) msg.add( ( Long ) o );
			else if ( o instanceof Enum ) {
				Enum e = (Enum<?>) o ;
				msg.add( e.name() );
				msg.add( e.ordinal() );
			}
			else msg.add( o.toString() );
		}

		return msg;
	}
	
	/**
	 * 中断后续操作，通知客户端
	 * @param _uid
	 * @param _id
	 * @param _params
	 * @throws MessageException
	 */
	public static void thrs( long _uid, int _id, Object... _params ) throws MessageException {
		String tip = "写回异常消息（中断模式）: UID[" + _uid + "]";

		MessageException me = new MessageException( tip );

		me.msg = _s_set_message( _uid, _id, _params );
		throw me;
	}

	/**
	 * 不中断后续操作，通知客户端
	 * @param _uid
	 * @param _id
	 * @param _params
	 * @return
	 */
	public static Message mesg( long _uid, int _id, Object... _params ) {
		String tip = "写回异常消息: UID[" + _uid + "]";

		MessageException me = new MessageException( tip );
		me.msg = _s_set_message( _uid, _id, _params );

		return me.msg;
	}

	/**
	 * 中断后续操作，不通知客户端
	 * @param _uid
	 * @param _id
	 * @param _err
	 * @param _params
	 */
	public static void fail( long _uid, int _id, int _err , Object... _params ) throws MessageException {
		String tip = "发生异常:UID[" + _uid + "]";

		MessageException me = new MessageException( tip );
		me.msg = _s_set_message( _uid, _id, _err , _params );
		throw me;
	}
}
