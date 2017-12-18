package com.jees.demo.protos;

import java.nio.ByteOrder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jees.jsts.netty.support.*;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

@Component( value = INettyHandler.NETTY_DECODER )
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

	@Override
	protected ByteBuf decode( ChannelHandlerContext _ctx , ByteBuf _buf ) throws Exception {
		return _buf;
	}
}
