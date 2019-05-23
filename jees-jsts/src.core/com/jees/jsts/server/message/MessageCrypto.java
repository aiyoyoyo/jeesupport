package com.jees.jsts.server.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.jsts.netty.support.AbsNettyDecoder;
import com.jees.jsts.netty.support.INettyHandler;
import com.jees.jsts.server.annotation.MessageProxy;
import com.jees.tool.utils.DataUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.nio.ByteOrder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息解码器，这里会对字节做高低位转换，以便于和其他部分语言进行通讯。
 * @author aiyoyoyo
 */
@Log4j2
@Component
@Scope( value = INettyHandler.SCOPE_CREATOR )
public class MessageCrypto extends AbsNettyDecoder {
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

	public static final String MSG_TYPE_PROTO 			= "proto";
	public static final String MSG_TYPE_JSON 			= "json";

	public MessageCrypto () {
		super( ByteOrder.LITTLE_ENDIAN , MAX_FRAME_LENGTH , LENGTH_FIELD_OFFSET , LENGTH_FIELD_LENGTH ,
				LENGTH_ADJUSTMENT , INITIAL_BYTES_TO_STRIP , true );
	}

	/**
	 * 解码参考，解决数据包超出缓冲区的问题。
	 * https://www.cnblogs.com/hupengcool/p/3931721.html
	 */
	@Override
	public byte[] decode( ChannelHandlerContext _ctx , ByteBuf _buf ) throws Exception {
		if ( _buf.readableBytes() < LENGTH_FIELD_LENGTH ) return null;

		_buf.markReaderIndex();

		int dataLength = 0;
		if( CommonConfig.getBoolean( "jees.jsts.socket.bom", false ) ){
			dataLength = DataUtil.warpHL( _buf.readInt() );
		}else{
			dataLength = _buf.readInt();
		}

		if ( _buf.readableBytes() < dataLength ) {
			_buf.resetReaderIndex();
			return null;
		}

		byte[] body = new byte [ dataLength ];
		_buf.readBytes( body );

		return body;
	}
	// socket序列化部分 ============================================================
	private static Map< Class< ? > , Schema< ? > > cachedSchema = new ConcurrentHashMap< Class< ? > , Schema< ? > >();
	private static Map< Integer , Class< ? > > proxyClases  = new HashMap<>();
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

	public static void registProxy() {
		boolean trust = CommonConfig.getBoolean( "jees.jsts.message.proxy", true );
		if( trust ) return;

		log.debug( "@MessageProxy消息代理注册..." );

		Collection< Object > msg_coll = CommonContextHolder.getApplicationContext().getBeansWithAnnotation( MessageProxy.class ).values();
		if( msg_coll.size() == 0 )
			log.debug( "--未找到代理。" );
		msg_coll.forEach( b -> {
			Class cls = b.getClass();
			MessageProxy mr = AnnotationUtils.findAnnotation( cls, MessageProxy.class );
			if( mr != null ){
				int[] cmd = mr.value();
				for( int c : cmd ){
					if ( proxyClases.containsKey( c ) ) {
						try{
							String use_cls = proxyClases.get(c).getName();
							log.warn( "命令重复：CMD[" + c + "], 当前[" + cls.getName() + "], 已使用[" + use_cls + "]" );
						}catch( Exception e ){
							log.warn( "命令重复：CMD[" + c + "]。" );
						}
					} else {
						proxyClases.put( c , b.getClass() );
						_get_schema( b.getClass() );
						log.debug( "--配置服务器命令: CMD[" + c + "], PROXY=[" + cls.getName() + "]" );
					}
				}
			}
		} );

		log.debug( "@MessageProxy消息代理注册成功：SIZE[" + proxyClases.size() + "]" );
	}

	// message decode
	/**
	 * S2C
	 * 将对象序列化为byte[]
	 *
	 * @param _obj 序列化对象
	 * @return 序列化后的byte[]值
	 */
	public static < T > Object serializer( ByteBuf _buf, T _obj, boolean _ws ){
		boolean proxy = CommonConfig.getBoolean( "jees.jsts.message.proxy", true );
		String type = CommonConfig.getString( "jees.jsts.message.type", MSG_TYPE_PROTO );

		byte[] data = null;
		if( type.equalsIgnoreCase( MSG_TYPE_JSON ) ){
			if( _ws ) {
				return new TextWebSocketFrame( _obj.toString() );
			}else{
				if( proxy ) {
					data = serializer( _obj );
				}else{
					data = _obj.toString().getBytes();
				}
			}
		}else{
			if( _ws ) {
				return new TextWebSocketFrame( _obj.toString() );
			}else {
				data = serializer( _obj );
			}
		}

		if( data != null ){
			int dataLength = data.length;
			if( CommonConfig.getBoolean( "jees.jsts.socket.bom", false ) ){
				dataLength = DataUtil.warpHL( dataLength );
			}
			_buf.writeInt( dataLength );
			_buf.writeBytes( data );
		}

		return _buf;
	}

	/**
	 * S2C
	 * 将对象序列化为byte[]
	 *
	 * @param _obj 序列化对象
	 * @return 序列化后的byte[]值
	 */
	public static < T > byte[] serializer ( T _obj ) {
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

	// message encode
	/**
	 * C2S
	 * @param _obj
	 * @return
	 */
	public static Object deserializer( Object _obj, boolean _ws ){
		// 这里根据类型解析
		boolean proxy = CommonConfig.getBoolean( "jees.jsts.message.proxy", true );
		String type = CommonConfig.getString( "jees.jsts.message.type", MSG_TYPE_PROTO );

		String log_str = "--解析命令: TYPE=[" + type + "], WEBSOCEKT=[" + _ws + "], PROXY=[" + proxy +"], ";
		try{
			if( type.equalsIgnoreCase( MSG_TYPE_JSON ) ){
				String json = null;
				if( _ws ) {
					json = ( ( TextWebSocketFrame ) _obj ).text();
				}else{
					json = new String( ( byte[] ) _obj );
				}
				if( proxy ){
					log_str += "JSON[" + json + "]转Message对象";
					return JSON.parseObject( json, Message.class );
				}else{
					JSONObject obj = JSON.parseObject(  json );
					Class cls = proxyClases.getOrDefault( obj.getInteger( "id" ), null );

					if( cls != null ){
						log_str += "JSON[" + json + "]转Class[" + cls.getName() + "]代理对象";
						return JSON.parseObject( json, cls );
					}else{
						log_str += "JSON[" + json + "]原始对象";
						return obj;
					}
				}
			}else{
				Object proto = null;

				if( _ws ) {
					String json = ( ( TextWebSocketFrame ) _obj ).text();

					if( proxy ) {
						proto = JSON.parseObject( json, Message.class );
					}else{
						JSONObject obj = JSON.parseObject( json );
						Class cls = proxyClases.getOrDefault( obj.getInteger( "id" ), null );

						if( cls != null ){
							log_str += "JSON[" + json + "]转Class[" + cls.getName() + "]代理对象";
							proto = JSON.parseObject( json, cls );
						}else{
							log_str += "JSON[" + json + "]原始对象";
							return obj;
						}
					}
				}else{
					if( proxy ) {
						proto = deserializer( ( byte[] ) _obj, Message.class );
						log_str += "字节转Message对象";
					}else{
						JSONObject obj = JSON.parseObject( new String( ( byte[] ) _obj ) );
						Class cls = proxyClases.getOrDefault( obj.getInteger( "id" ), null );

						if( cls != null ){
							proto = deserializer( ( byte[] ) _obj, cls );
							log_str += "字节转Class[" + cls.getName() + "]代理对象";
						}else{
							proto = obj.toJavaObject( cls );
							log_str += "字节转Java[" + cls.getName() + "]代理对象";
						}
					}
				}

				return proto;
			}
		}catch( Exception e ){
			log.error( "接收的命令无法解析，返回默认命令号。" );
			return new Message();
		}finally{
			log.debug( log_str );
		}
	}

	/**
	 * C2S
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


}
