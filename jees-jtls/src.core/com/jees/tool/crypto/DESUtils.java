package com.jees.tool.crypto;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 通过DES方式，对文本内容进行加密和解密
 * 
 * @author aiyoyoyo
 */
public class DESUtils {
	/**
	 * 密钥算法 <br>
	 * Java 6 只支持56bit密钥 <br>
	 * Bouncy Castle 支持64bit密钥
	 */
	public static final String	ALGORITHM			= "DES";

	/**
	 * 密钥长度
	 */
	private static final int	KEY_SIZE			= 56;

	/**
	 * 加密/解密算法 / 工作模式 / 填充方式
	 */
	public static final String	CIPHER_ALGORITHM	= "DES/ECB/PKCS5PADDING";

	/**
	 * 转换密钥
	 * 
	 * @param _key
	 *            二进制密钥
	 * @return
	 * @throws Exception
	 */
	private static Key _s_to_key( byte[] _key ) throws Exception {
		// 实例化DES密钥材料
		DESKeySpec dks = new DESKeySpec( _key );

		// 实例化秘密密钥工厂
		SecretKeyFactory factory = SecretKeyFactory.getInstance( ALGORITHM );

		// 生成秘密密钥
		SecretKey secretKey = factory.generateSecret( dks );

		return secretKey;
	}

	/**
	 * 解密
	 * 
	 * @param _key 密钥
	 * @param _dat 待解密数据
	 * @return 返回解密后字节
	 * @throws Exception 解密时发生错误
	 */
	public static byte[] s_decrypt( byte[] _key , byte[] _dat ) throws Exception {

		// 还原密钥
		Key k = _s_to_key( _key );

		// 实例化
		Cipher cipher = Cipher.getInstance( ALGORITHM );

		// 初始化，设置为解密模式
		cipher.init( Cipher.DECRYPT_MODE , k );

		// 执行操作
		return cipher.doFinal( _dat );
	}

	/**
	 * 加密
	 * 
	 * @param _key 密钥
	 * @param _dat 待加密数据
	 * @return 加密后的字节
	 * @throws Exception 加密时产生错误
	 */
	public static byte[] s_encrypt( byte[] _key , byte[] _dat ) throws Exception {

		// 还原密钥
		Key k = _s_to_key( _key );

		// 实例化
		Cipher cipher = Cipher.getInstance( ALGORITHM );

		// 初始化，设置为加密模式
		cipher.init( Cipher.ENCRYPT_MODE , k );

		// 执行操作
		return cipher.doFinal( _dat );
	}

	/**
	 * 生成二进制密钥 <br>
	 * Java 6 只支持56bit密钥 <br>
	 * Bouncy Castle 支持64bit密钥 <br>
	 * 
	 * @return 二进制密钥
	 * @throws Exception 生成密钥出错
	 */
	public static byte[] s_genkeys() throws Exception {
		/*
		 * 实例化密钥生成器
		 * 
		 * 若要使用64bit密钥注意替换 将下述代码中的KeyGenerator.getInstance(CIPHER_ALGORITHM);
		 * 替换为KeyGenerator.getInstance(CIPHER_ALGORITHM, "BC");
		 */
		KeyGenerator key_generator = KeyGenerator.getInstance( ALGORITHM );

		/*
		 * 初始化密钥生成器 若要使用64bit密钥注意替换 将下述代码kg.init(56); 替换为kg.init(64);
		 */
		key_generator.init( KEY_SIZE , new SecureRandom() );

		// 生成秘密密钥
		SecretKey secret_key = key_generator.generateKey();

		// 获得密钥的二进制编码形式
		return secret_key.getEncoded();
	}

	/**
	 * 通过种子生成二进制密钥
	 * 
	 * @param _arg 生成私钥的种子
	 * @return 二进制密钥字节
	 * @throws Exception 生成错误
	 */
	public static byte[] s_genkeys( String _arg ) throws Exception {
		KeyGenerator key_generator = KeyGenerator.getInstance( ALGORITHM );
		SecureRandom secure_random = new SecureRandom( B64Utils.s_decode( _arg ) );
		key_generator.init( secure_random );
		SecretKey secret_key = key_generator.generateKey();
		return secret_key.getEncoded();
	}
}
