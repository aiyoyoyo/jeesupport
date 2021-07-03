package com.jees.server.abs;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;

/**
 * 用于自定义粘包解码类
 * 
 * @author aiyoyoyo
 *
 */
public abstract class AbsNettyDecoder extends LengthFieldBasedFrameDecoder {
	public AbsNettyDecoder( ByteOrder byteOrder , int maxFrameLength , int lengthFieldOffset , int lengthFieldLength ,
					int lengthAdjustment , int initialBytesToStrip , boolean failFast ) {
		super( byteOrder , maxFrameLength , lengthFieldOffset , lengthFieldLength , lengthAdjustment ,
						initialBytesToStrip , failFast ) ;
	}
}
