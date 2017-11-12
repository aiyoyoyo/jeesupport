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

public class CryptoTest {
	@Test
	public void testAES() {
		System.out.println( "-- Test AES ---------------------" );
		try {
			String key = AESUtils.s_genkeys( "cm9vdA90" );
			System.out.println( "  原文：[cm9vdA90] -> [" + key + "]" );
			String txt = "1:aiyoyoyo:c05357f9b3e9f23eece71b0c06ed1d18:localhost:1506065442008:中文测试";
			System.out.println( "  内容: [" + txt + "]" );
			String str_e = AESUtils.s_encrypt( key , txt );

			System.out.println( "  加密：[" + str_e + "]" );
			String str_d = AESUtils.s_decrypt( key , str_e );
			System.out.println( "  解密：[" + str_d + "]" );
			System.out.println( "  匹配结果:" + txt.equals( str_d ) );
		} catch ( Exception e ) {
			System.out.println( "私钥或者内容错误！" );
		}
		System.out.println( "-- Test AES ---------------------" );
	}

//	@Test
	public void testMD5() {
		System.out.println( "-- MD5 ---------------------" );

		String key = "abcdef";
		String txt = "你好，1234,aBcDer&*(&(*";
		System.out.println( "  内容：[" + txt + "]，密钥：[" + key + "]" );
		System.out.println( "  原文加密：" + MD5Utils.s_encode( txt ) );
		System.out.println( "  密钥加密：" + MD5Utils.s_encode( txt , key ) );
		System.out.println( "-- MD5 ---------------------" );
	}

//	@Test
	public void testBase64() {
		System.out.println( "-- Base64 ---------------------" );

		String txt = "你好，1234,aBcDer&*(&(*";
		String str_e = null;
		byte[] byt_d = null;
		try {
			System.out.println( "  内容：[" + txt + "]" );
			str_e = B64Utils.s_encode( txt.getBytes() );
			System.out.println( "  加密:" + str_e );
			byt_d = B64Utils.s_decode( str_e );
			System.out.println( "  解密:" + new String( byt_d ) );
		} catch ( UnsupportedEncodingException e ) {
			e.printStackTrace();
		}

		System.out.println( "-- Base64 ---------------------" );
	}

//	@Test
	public void testDES() {
		System.out.println( "-- DES ---------------------" );
		try {
			byte[] key = DESUtils.s_genkeys();
			System.out.println( "  私钥：[ " + new String( key ) + "]" );
			String txt = "你好，1234,aBcDer&*(&(*";
			byte[] byt_e = DESUtils.s_encrypt( key , txt.getBytes() );
			System.out.println( "  加密内容: [" + txt + "] -> [" + new String( byt_e )  + "] 长度：" + byt_e.length );

			byte[] byt_d = DESUtils.s_decrypt( key , byt_e );
			String str_d = new String( byt_d );
			System.out.println( "  解密: [" + str_d + "]" );
			System.out.println( "  匹配结果:" + txt.equals( str_d ) );
		} catch ( Exception e ) {
			System.out.println( "私钥或者内容错误！" );
		}
		System.out.println( "-- DES ---------------------" );
	}

//	@Test
	public void testRSA() {
		System.out.println( "-- RSA ---------------------" );
		try {
			Map< String , Key > key_map = RSAUtils.s_genkeys_map();

			byte[] pub_key = RSAUtils.s_public_key_byte( key_map );
			byte[] pri_key = RSAUtils.s_private_key_byte( key_map );

			String pub_key_str = B64Utils.s_encode( pub_key );
			System.out.println( "  公钥              : [" + new String( pub_key ) + "]" );
			System.out.println( "  公钥BASE64: [" + pub_key_str + "]" );
			System.out.println( "  私钥: [" + new String( pri_key ) + "]"  );
			System.out.println( "  私钥BASE64: [" + B64Utils.s_encode( pub_key ) + "]" );
			
			String txt = "DABA-C5D2-05C127B8-77D330BCB674F506";
			System.out.println( "  内容：[" + txt + "]" );
			byte[] byt_e = RSAUtils.s_encrypt_private( pri_key , txt.getBytes() );
			byte[] byt_d = RSAUtils.s_decrypt_public( B64Utils.s_decode( pub_key_str ) , byt_e );
			System.out.println( "私钥加密，公钥解密: [" + new String( byt_d ) + "]");

			byt_e = RSAUtils.s_encrypt_public( pub_key , txt.getBytes() );
			byt_d = RSAUtils.s_decrypt_private( pri_key , byt_e );
			System.out.println( "公钥加密，私钥解密: [" + new String( byt_d ) + "]" );
		} catch ( Exception e ) {
			System.out.println( "私钥或者内容错误！" );
		}
		System.out.println( "-- RSA ---------------------" );
	}
}
