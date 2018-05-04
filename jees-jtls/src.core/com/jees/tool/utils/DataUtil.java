package com.jees.tool.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 提供基础数据类型的相关操作
 * 
 * @author aiyoyoyo
 *
 */
public class DataUtil {

	public static final String Charset_UTF8 = "UTF-8";

	/**
	 * int转byte[]
	 * 
	 * @param _val
	 * @return
	 */
	public static byte[] int2bytes( int _val ) {
		return new byte [] { ( byte ) ( ( _val >> 24 ) & 0xFF ) , ( byte ) ( ( _val >> 16 ) & 0xFF ) ,
						( byte ) ( ( _val >> 8 ) & 0xFF ) , ( byte ) ( _val & 0xFF ) };
	}

	/**
	 * byte[]转int
	 * 
	 * @param _val
	 * @return
	 */
	public static int bytes2int( byte[] _val ) {
		return _val[ 3 ] & 0xFF | ( _val[ 2 ] & 0xFF ) << 8 | ( _val[ 1 ] & 0xFF ) << 16 | ( _val[ 0 ] & 0xFF ) << 24;
	}

	/**
	 * String转byte[]
	 * 
	 * @param _val
	 * @param _chs
	 *            字符解码类型，建议UTF-8，参考：DataUtil.Charset_UTF8
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] str2bytes( String _val , String _chs ) throws UnsupportedEncodingException {
		return _val.getBytes( _chs );
	}

	/**
	 * bytes转String
	 * 
	 * @param _val
	 * @param _chs
	 *            字符解码类型，建议UTF-8，参考：DataUtil.Charset_UTF8
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String bytes2str( byte[] _val , String _chs ) throws UnsupportedEncodingException {
		return new String( _val , _chs );
	}

	/**
	 * 将int类型的数字进行高低位互转
	 * 
	 * @param _val
	 * @return
	 */
	public static int warpHL( int _val ) {
		return ByteBuffer.wrap( int2bytes( _val ) ).order( ByteOrder.LITTLE_ENDIAN ).getInt();
	}

	/**
	 * 截取bytes
	 * 
	 * @param _data
	 * @param _start
	 * @param _end
	 * @return
	 */
	public static byte[] subBytes( byte[] _data , int _start , int _end ) {
		byte[] ret = new byte[_end - _start];
		for (int i = 0; (_start + i) < _end; i++) {
			ret[i] = _data[_start + i];
		}
		return ret;
	}
}
