package com.jees.tool.crypto;

import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;

/**
 * 通过RSA方式，对文本内容进行非对称加密和解密
 * 
 * @author aiyoyoyo
 */
public class RSAUtils {
	/**
	 * 非对称加密密钥算法
	 */
	public static final String	KEY_ALGORITHM_RSA		= "RSA";

	/**
	 * 公钥
	 */
	private static final String	RSA_PUBLIC_KEY		= "RSAPublicKey";

	/**
	 * 私钥
	 */
	private static final String	RSA_PRIVATE_KEY		= "RSAPrivateKey";

	/**
	 * RSA密钥长度
	 * 默认1024位，
	 * 密钥长度必须是64的倍数，
	 * 范围在512至65536位之间。
	 */
	private static final int		KEY_SIZE				= 1024;

	/**
	 * 私钥解密
	 * 
	 * @param _key
	 *            私钥
	 * @param _dat
	 *            待解密数据
	 * @return
	 * @throws Exception
	 */
	public static byte[] s_decrypt_private( byte[] _key, byte[] _dat ) throws Exception {
		// 取得私钥
		PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec( _key );

		KeyFactory key_factory = KeyFactory.getInstance( KEY_ALGORITHM_RSA );

		// 生成私钥
		PrivateKey private_key = key_factory.generatePrivate( pkcs8 );

		// 对数据解密
		Cipher cipher = Cipher.getInstance( key_factory.getAlgorithm() );

		cipher.init( Cipher.DECRYPT_MODE , private_key );

		int blockSize = cipher.getBlockSize();
		if ( blockSize > 0 ) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream( 64 );
			int j = 0;
			while ( _dat.length - j * blockSize > 0 ) {
				bout.write( cipher.doFinal( _dat , j * blockSize , blockSize ) );
				j++;
			}
			return bout.toByteArray();
		}
		return cipher.doFinal( _dat );
	}

	/**
	 * 公钥解密
	 * 
	 * @param _key
	 *            公钥
	 * @param _dat
	 *            待解密数据
	 * @return
	 * @throws Exception
	 */
	public static byte[] s_decrypt_public( byte[] _key , byte[] _data ) throws Exception {

		// 取得公钥
		X509EncodedKeySpec x509 = new X509EncodedKeySpec( _key );

		KeyFactory key_factory = KeyFactory.getInstance( KEY_ALGORITHM_RSA );

		// 生成公钥
		PublicKey public_key = key_factory.generatePublic( x509 );

		// 对数据解密
		Cipher cipher = Cipher.getInstance( key_factory.getAlgorithm() );

		cipher.init( Cipher.DECRYPT_MODE , public_key );

		return cipher.doFinal( _data );
	}

	/**
	 * 公钥加密
	 * 
	 * @param _key
	 *            公钥
	 * @param _data
	 *            待加密数据
	 * @return 
	 * @throws Exception
	 */
	public static byte[] s_encrypt_public( byte[] _key , byte[] _data ) throws Exception {

		// 取得公钥
		X509EncodedKeySpec x509 = new X509EncodedKeySpec( _key );

		KeyFactory key_factory = KeyFactory.getInstance( KEY_ALGORITHM_RSA );

		PublicKey public_key = key_factory.generatePublic( x509 );

		// 对数据加密
		Cipher cipher = Cipher.getInstance( key_factory.getAlgorithm() );

		cipher.init( Cipher.ENCRYPT_MODE , public_key );

		int blockSize = cipher.getBlockSize();
		if ( blockSize > 0 ) {
			int outputSize = cipher.getOutputSize( _data.length );
			int leavedSize = _data.length % blockSize;
			int blocksSize = leavedSize != 0 ? _data.length / blockSize + 1 : _data.length / blockSize;
			byte[] raw = new byte[ outputSize * blocksSize ];
			int i = 0 , remainSize = 0;
			while ( ( remainSize = _data.length - i * blockSize ) > 0 ) {
				int inputLen = remainSize > blockSize ? blockSize : remainSize;
				cipher.doFinal( _data , i * blockSize , inputLen , raw , i * outputSize );
				i++;
			}
			return raw;
		}
		return cipher.doFinal( _data );
	}

	/**
	 * 私钥加密
	 * 
	 * @param _key
	 *            私钥
	 * @param _data
	 *            待加密数据
	 * @return
	 * @throws Exception
	 */
	public static byte[] s_encrypt_private( byte[] _key , byte[] _data ) throws Exception {

		// 取得私钥
		PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec( _key );

		KeyFactory key_factory = KeyFactory.getInstance( KEY_ALGORITHM_RSA );

		// 生成私钥
		PrivateKey private_key = key_factory.generatePrivate( pkcs8 );

		// 对数据加密
		Cipher cipher = Cipher.getInstance( key_factory.getAlgorithm() );

		cipher.init( Cipher.ENCRYPT_MODE , private_key );

		int blockSize = cipher.getBlockSize();
		if ( blockSize > 0 ) {
			int outputSize = cipher.getOutputSize( _data.length );
			int leavedSize = _data.length % blockSize;
			int blocksSize = leavedSize != 0 ? _data.length / blockSize + 1 : _data.length / blockSize;
			byte[] raw = new byte[ outputSize * blocksSize ];
			int i = 0 , remainSize = 0;
			while ( ( remainSize = _data.length - i * blockSize ) > 0 ) {
				int inputLen = remainSize > blockSize ? blockSize : remainSize;
				cipher.doFinal( _data , i * blockSize , inputLen , raw , i * outputSize );
				i++;
			}
			return raw;
		}
		return cipher.doFinal( _data );
	}

	/**
	 * 取得私钥
	 * 
	 * @param _keymap
	 *            密钥Map
	 * @return 
	 * @throws Exception
	 */
	public static Key s_private_key( Map< String , Key > _keymap ){
		return _keymap.get( RSA_PRIVATE_KEY );
	}

	/**
	 * 取得私钥
	 * 
	 * @param _keymap
	 *            密钥Map
	 * @return
	 * @throws Exception
	 */
	public static byte[] s_private_key_byte( Map< String , Key > _keymap ){
		return _keymap.get( RSA_PRIVATE_KEY ).getEncoded();
	}

	/**
	 * 取得公钥
	 * 
	 * @param _keymap
	 *            密钥Map
	 * @return
	 */
	public static Key s_public_key( Map< String , Key > _keymap ) {
		return _keymap.get( RSA_PUBLIC_KEY );
	}

	/**
	 * 取得公钥
	 * 
	 * @param _keymap
	 *            密钥Map
	 * @return 
	 */
	public static byte[] s_public_key_byte( Map< String , Key > _keymap ) {
		return _keymap.get( RSA_PUBLIC_KEY ).getEncoded();
	}

	/**
	 * 初始化密钥
	 * 
	 * @param byte[]
	 *            _arg 种子
	 * @return
	 * @throws Exception
	 */
	public static Map< String , Key > s_genkeys_map( byte[] _arg ) throws Exception {
		// 实例化密钥对生成器
		KeyPairGenerator key_pair_generator = KeyPairGenerator.getInstance( KEY_ALGORITHM_RSA );

		// 初始化密钥对生成器
		key_pair_generator.initialize( KEY_SIZE , new SecureRandom( _arg ) );

		// 生成密钥对
		KeyPair key_pair = key_pair_generator.generateKeyPair();

		// 公钥
		RSAPublicKey public_key = (RSAPublicKey) key_pair.getPublic();

		// 私钥
		RSAPrivateKey private_key = (RSAPrivateKey) key_pair.getPrivate();

		// 封装密钥
		Map< String , Key > key_map = new HashMap< String , Key >( 2 );

		key_map.put( RSA_PUBLIC_KEY , public_key );
		key_map.put( RSA_PRIVATE_KEY , private_key );

		return key_map;
	}

	/**
	 * 初始化密钥
	 * 
	 * @param _arg
	 *            种子
	 * @return
	 * @throws Exception
	 */
	public static Map< String , Key > s_genkeys_map( String _arg ) throws Exception {
		return s_genkeys_map( _arg.getBytes() );
	}

	/**
	 * 初始化密钥
	 * 
	 * @return Map 密钥Map
	 * @throws Exception
	 */
	public static Map< String , Key > s_genkeys_map() throws Exception {
		return s_genkeys_map( UUID.randomUUID().toString().getBytes() );
	}
}
