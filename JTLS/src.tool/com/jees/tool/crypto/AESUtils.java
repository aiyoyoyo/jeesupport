package com.jees.tool.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.codec.Base64;

/**
 * 通过AES方式，对文本内容进行加密和解密
 * 
 * @author aiyoyoyo
 */
public class AESUtils {
	/**
	 * 密钥算法
	 */
	private static final String	ALGORITHM	= "AES";
	/**
	 * 密钥长度
	 */
	private static final int	KEY_SIZE	= 128;

	/**
	 * 解密
	 * 
	 * @param _key
	 *            加密私钥
	 * @param _arg
	 *            待解密内容
	 * @return 解密结果
	 * @throws Exception
	 *             私钥错误，无法解密时，会抛出对应的异常
	 */
	public static String s_decrypt( String _key , String _txt ) throws Exception {
		SecretKey secret_key = new SecretKeySpec( Base64.decode( _key.getBytes() ) , ALGORITHM );
		byte[] raw = secret_key.getEncoded();
		SecretKeySpec secret_key_spec = new SecretKeySpec( raw , ALGORITHM );
		Cipher cipher = Cipher.getInstance( ALGORITHM );
		cipher.init( Cipher.DECRYPT_MODE , secret_key_spec );
		return new String( cipher.doFinal( Base64.decode( _txt.getBytes() ) ) );
	}

	/**
	 * 加密
	 * 
	 * @param _key
	 *            加密私钥
	 * @param _arg
	 *            待加密内容
	 * @return 加密结果
	 * @throws Exception
	 */
	public static String s_encrypt( String _key , String _txt ) throws Exception {
		SecretKey secret_key = new SecretKeySpec( Base64.decode( _key.getBytes() ) , ALGORITHM );
		byte[] raw = secret_key.getEncoded();
		SecretKeySpec secret_key_spec = new SecretKeySpec( raw , ALGORITHM );
		Cipher cipher = Cipher.getInstance( ALGORITHM );
		cipher.init( Cipher.ENCRYPT_MODE , secret_key_spec );
		return new String( Base64.encode( cipher.doFinal( _txt.getBytes() ) ) );
	}

	/**
	 * 创建私钥
	 * 
	 * @param _arg
	 *            生成私钥的种子
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String s_genkeys( String _arg ) throws NoSuchAlgorithmException {
		KeyGenerator key_generator = KeyGenerator.getInstance( ALGORITHM );
		SecureRandom secure_random;
		if ( _arg != null && !"".equals( _arg ) ) {
			secure_random = new SecureRandom( _arg.getBytes() );
		} else {
			secure_random = new SecureRandom();
		}
		key_generator.init( KEY_SIZE , secure_random );
		SecretKey secret_key = key_generator.generateKey();
		return new String( Base64.encode( secret_key.getEncoded() ) );
	}
}
