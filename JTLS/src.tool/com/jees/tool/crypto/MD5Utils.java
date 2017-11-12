package com.jees.tool.crypto;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

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
		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		return md5.encodePassword( _txt , _txt );
	}

	/**
	 * 带私钥MD5加密
	 * 
	 * @param _txt
	 *            待加密内容
	 * @param _key
	 *            加密私钥
	 * @return
	 */
	public static String s_encode( String _txt , String _key ) {
		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		return md5.encodePassword( _txt , _key );
	}
}
