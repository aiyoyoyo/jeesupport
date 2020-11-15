package com.jees.tool.crypto;

import org.springframework.util.DigestUtils;
/**
 * 通过MD5方式，对文本内容进行加密
 * 
 * @author aiyoyoyo
 */
public class MD5Utils {

	/**
	 * 无私钥MD5加密
	 * 
	 * @param _txt
	 *            待加密内容
	 * @return 加密结果
	 */
	public static String s_encode( String _txt ) {
		return DigestUtils.md5DigestAsHex( _txt.getBytes() );
	}

	/**
	 * 带私钥MD5加密
	 * 
	 * @param _txt 待加密内容
	 * @param _key 加密私钥
	 * @return 加密后字符串
	 */
	public static String s_encode( String _txt , String _key ) {
		return DigestUtils.md5DigestAsHex( _key.concat( _txt ).getBytes() );
	}
}
