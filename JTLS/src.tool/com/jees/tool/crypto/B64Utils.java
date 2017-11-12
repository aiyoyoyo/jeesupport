package com.jees.tool.crypto;

import java.io.UnsupportedEncodingException;

import org.springframework.security.crypto.codec.Base64;

/**
 * 通过Base64方式，对文本内容进行加密和解密
 * 
 * @author aiyoyoyo
 */
public class B64Utils {
	/**
	 * 字符编码
	 */
	public final static String ENCODING = "UTF-8";

	/**
	 * Base64编码
	 * 
	 * @param _data
	 *            待编码数据
	 * @return String 编码数据
	 * @throws UnsupportedEncodingException
	 */
	public static String s_encode( byte[] _data ) throws UnsupportedEncodingException {
		byte[] b = Base64.encode( _data );
		return new String( b );
	}

	/**
	 * Base64解码
	 * 
	 * @param _data
	 *            待解码数据
	 * @return String 解码数据
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] s_decode( String _data ) throws UnsupportedEncodingException {
		return Base64.decode( _data.getBytes() );
	}
}
