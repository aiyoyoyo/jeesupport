package com.jees.test.utils;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Map;

import org.junit.Test;

import com.jees.tool.crypto.AESUtils;
import com.jees.tool.crypto.B64Utils;
import com.jees.tool.crypto.DESUtils;
import com.jees.tool.crypto.MD5Utils;
import com.jees.tool.crypto.RSAUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoTest {
	static Logger logger =  LoggerFactory.getLogger( CryptoTest.class );
	@Test
	public void testAES() {
		logger.debug( "-- Test AES ---------------------" );
		try {
			String key = AESUtils.s_genkeys( "cm9vdA90" );
			logger.debug( "  原文：[cm9vdA90] -> [" + key + "]" );
			String txt = "1:aiyoyoyo:c05357f9b3e9f23eece71b0c06ed1d18:localhost:1506065442008:中文测试";
			logger.debug( "  内容: [" + txt + "]" );
			String str_e = AESUtils.s_encrypt( key , txt );

			logger.debug( "  加密：[" + str_e + "]" );
			String str_d = AESUtils.s_decrypt( key , str_e );
			logger.debug( "  解密：[" + str_d + "]" );
			logger.debug( "  匹配结果:" + txt.equals( str_d ) );
		} catch ( Exception e ) {
			logger.debug( "私钥或者内容错误！" );
		}
		logger.debug( "-- Test AES ---------------------" );
	}

	@Test
	public void testMD5() {
		logger.debug( "-- MD5 ---------------------" );

		String key = "abcdef";
		String txt = "你好，1234,aBcDer&*(&(*";
		logger.debug( "  内容：[" + txt + "]，密钥：[" + key + "]" );
		logger.debug( "  原文加密：" + MD5Utils.s_encode( txt ) );
		logger.debug( "  密钥加密：" + MD5Utils.s_encode( txt , key ) );
		logger.debug( "-- MD5 ---------------------" );
	}

	@Test
	public void testBase64() {
		logger.debug( "-- Base64 ---------------------" );

		String txt = "你好，1234,aBcDer&*(&(*";
		String str_e = null;
		byte[] byt_d = null;
		logger.debug( "  内容：[" + txt + "]" );
		str_e = B64Utils.s_encode( txt.getBytes() );
		logger.debug( "  加密:" + str_e );
		byt_d = B64Utils.s_decode( str_e );
		logger.debug( "  解密:" + new String( byt_d ) );

		logger.debug( "-- Base64 ---------------------" );
	}

	@Test
	public void testDES() {
		logger.debug( "-- DES ---------------------" );
		try {
			byte[] key = DESUtils.s_genkeys();
			logger.debug( "  私钥：[ " + new String( key ) + "]" );
			String txt = "你好，1234,aBcDer&*(&(*";
			byte[] byt_e = DESUtils.s_encrypt( key , txt.getBytes() );
			logger.debug( "  加密内容: [" + txt + "] -> [" + new String( byt_e )  + "] 长度：" + byt_e.length );

			byte[] byt_d = DESUtils.s_decrypt( key , byt_e );
			String str_d = new String( byt_d );
			logger.debug( "  解密: [" + str_d + "]" );
			logger.debug( "  匹配结果:" + txt.equals( str_d ) );
		} catch ( Exception e ) {
			logger.debug( "私钥或者内容错误！" );
		}
		logger.debug( "-- DES ---------------------" );
	}

	@Test
	public void testRSA() {
		logger.debug( "-- RSA ---------------------" );
		try {
			Map< String , Key > key_map = RSAUtils.s_genkeys_map();

			byte[] pub_key = RSAUtils.s_public_key_byte( key_map );
			byte[] pri_key = RSAUtils.s_private_key_byte( key_map );

			String pub_key_str = B64Utils.s_encode( pub_key );
			logger.debug( "  公钥              : [" + new String( pub_key ) + "]" );
			logger.debug( "  公钥BASE64: [" + pub_key_str + "]" );
			logger.debug( "  私钥: [" + new String( pri_key ) + "]"  );
			logger.debug( "  私钥BASE64: [" + B64Utils.s_encode( pub_key ) + "]" );
			
			String txt = "DABA-C5D2-05C127B8-77D330BCB674F506";
			logger.debug( "  内容：[" + txt + "]" );
			byte[] byt_e = RSAUtils.s_encrypt_private( pri_key , txt.getBytes() );
			byte[] byt_d = RSAUtils.s_decrypt_public( B64Utils.s_decode( pub_key_str ) , byt_e );
			logger.debug( "私钥加密，公钥解密: [" + new String( byt_d ) + "]");

			byt_e = RSAUtils.s_encrypt_public( pub_key , txt.getBytes() );
			byt_d = RSAUtils.s_decrypt_private( pri_key , byt_e );
			logger.debug( "公钥加密，私钥解密: [" + new String( byt_d ) + "]" );
		} catch ( Exception e ) {
			logger.debug( "私钥或者内容错误！" );
		}
		logger.debug( "-- RSA ---------------------" );
	}
}
