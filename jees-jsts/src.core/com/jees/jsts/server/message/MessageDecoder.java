package com.jees.jsts.server.message;

import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jees.jsts.netty.support.*;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * 消息解码器，这里会对字节做高低位转换，以便于和其他部分语言进行通讯。
 * @author aiyoyoyo
 */
@Log4j2
@Component
@Scope( value = INettyHandler.SCOPE_CREATOR )
public class MessageDecoder extends AbsNettyDecoder {
	// 网络字节序，默认为大端字节序
	public static final int		MAX_FRAME_LENGTH		= 1024 * 4;
	// 消息中长度字段占用的字节数
	public static final int		LENGTH_FIELD_LENGTH		= 4;
	// 消息中长度字段偏移的字节数
	private static final int	LENGTH_FIELD_OFFSET		= 0;
	// 该字段加长度字段等于数据帧的长度
	private static final int	LENGTH_ADJUSTMENT		= 0;
	// 从数据帧中跳过的字节数
	private static final int	INITIAL_BYTES_TO_STRIP	= 0;

	public MessageDecoder() {
		super( ByteOrder.LITTLE_ENDIAN , MAX_FRAME_LENGTH , LENGTH_FIELD_OFFSET , LENGTH_FIELD_LENGTH ,
				LENGTH_ADJUSTMENT , INITIAL_BYTES_TO_STRIP , true );
	}

	/**
	 * 解码参考，解决数据包超出缓冲区的问题。
	 * https://www.cnblogs.com/hupengcool/p/3931721.html
	 */
	@Override
	public Object decode( ChannelHandlerContext _ctx , ByteBuf _buf ) throws Exception {
		if ( _buf.readableBytes() < LENGTH_FIELD_LENGTH ) return null;

		_buf.markReaderIndex();
//		int dataLength = DataUtil.warpHL( _buf.readInt() );
		int dataLength = _buf.readInt();

		// 长度为负数作为特定消息通道
		if( dataLength < 0 ){
			Message m = new Message();
			m.setId( dataLength );
			m.setType( Message.TYPE_BYTES );

			return m;
		}

		if ( _buf.readableBytes() < dataLength ) {
			_buf.resetReaderIndex();
			return null;
		}

		byte[] body = new byte [ dataLength ];
		_buf.readBytes( body );

		Message msg = deserializer( body , Message.class );
		return msg;
	}

	public static void buff( ByteBuf _buf, Message _msg ) {
		byte[] byt_data = serializer( _msg );
		_buf.writeInt( byt_data.length );
		_buf.writeBytes( byt_data );
	}

	public static void buff( ByteBuf _buf, byte[] _bytes ) {
		_buf.writeInt( _bytes.length );
		_buf.writeBytes( _bytes );
	}

	// socket序列化部分 ============================================================
	private static Map< Class< ? > , Schema< ? > > cachedSchema = new ConcurrentHashMap< Class< ? > , Schema< ? > >();

	/**
	 * 获取某个对象的实例构建器 
	 * @param _cls 实例化对象的类
	 * @return 对应类的实例构建器
	 */
	private static < T > Schema< T > _get_schema( Class< T > _cls ) {
		@SuppressWarnings( "unchecked" )
		Schema< T > schema = ( Schema< T > ) cachedSchema.get( _cls );
		if ( schema == null ) {
			schema = RuntimeSchema.getSchema( _cls );
			if ( schema != null ) {
				cachedSchema.put( _cls , schema );
			}
		}
		return schema;
	}

	/**
	 * 讲对象序列化为byte[]
	 *
	 * @param _obj 序列化对象
	 * @return 序列化后的byte[]值
	 */
	public static < T > byte[] serializer( T _obj ) {
		@SuppressWarnings( "unchecked" )
		Class< T > cls = ( Class< T > ) _obj.getClass();
		LinkedBuffer buf = LinkedBuffer.allocate( LinkedBuffer.DEFAULT_BUFFER_SIZE );
		try {
			Schema< T > schema = _get_schema( cls );
			return ProtostuffIOUtil.toByteArray( _obj , schema , buf );
		} catch ( Exception e ) {
			throw new IllegalStateException( e.getMessage() , e );
		} finally {
			buf.clear();
		}
	}

	/**
	 * 反序列化byte[]为对象
	 *
	 * @param _byts 序列化后的byte[]值
	 * @param _cls 反序列化后的对象
	 * @return 返回的对象
	 */
	public static < T > T deserializer( byte[] _byts , Class< T > _cls ) {
		try {
			T obj = _cls.newInstance();
			ProtostuffIOUtil.mergeFrom( _byts , obj , _get_schema( _cls ) );
			return obj;
		} catch ( Exception e ) {
			throw new IllegalStateException( e.getMessage() , e );
		}
	}
	// websocket 序列化部分 ========================================================
	public static String serializerToJson( Message _msg ) {
		JSONObject json =new JSONObject();

		String json_data = JSON.toJSONString( _msg );
		return json_data;
	}

	public static Message deserializer( Object _obj ) {
		Message msg = null;
		if( _obj instanceof WebSocketFrame){
			msg = deserializer( ( WebSocketFrame ) _obj );
			msg.setType( Message.TYPE_WEBSOCKET );
		}else msg = ( Message ) _obj;

		return msg;
	}
	/**
	 * 序列化Message对象，根据自身情况确定使用二进制还是纯文本格式
	 * 所有字节先尝试转int，失败后转为string
	 * @param _frame
	 * @return
	 */
	public static Message deserializer( WebSocketFrame _frame ) {
		// 当前只支持文本消息，不支持二进制消息
		if ( ! ( _frame instanceof TextWebSocketFrame ) ) { throw new UnsupportedOperationException(
				"当前只支持文本消息，不支持二进制消息" ); }
		// 处理来自客户端的WebSocket请求
		String request_text = ( ( TextWebSocketFrame ) _frame ).text();

		Message msg = null;
		try{
			msg = JSON.parseObject( request_text, Message.class);
			return msg;
		}catch( Exception e ){
			log.debug( "-- JSON转换Message失败，消息将存入无ID属性的Message对象。" );
			msg = new Message();
			msg.addString( request_text );
		}
		return msg;
	}
}
