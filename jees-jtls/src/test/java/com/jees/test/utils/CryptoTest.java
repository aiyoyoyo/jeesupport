package com.jees.test.utils;

import com.jees.tool.crypto.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import sun.security.provider.Sun;

import java.security.Key;
import java.util.Map;

@Log4j2
public class CryptoTest {
    @Test
    public void testAES() {
        log.debug("-- Test AES ---------------------");
        try {
            String key = AESUtils.s_genkeys("cm9vdA90");
            log.debug("  原文：[cm9vdA90] -> [" + key + "]");
            String txt = "1:aiyoyoyo:c05357f9b3e9f23eece71b0c06ed1d18:localhost:1506065442008:中文测试";
            log.debug("  内容: [" + txt + "]");
            String str_e = AESUtils.s_encrypt(key, txt);

            log.debug("  加密：[" + str_e + "]");
            String str_d = AESUtils.s_decrypt(key, str_e);
            log.debug("  解密：[" + str_d + "]");
            log.debug("  匹配结果:" + txt.equals(str_d));
        } catch (Exception e) {
            log.debug("私钥或者内容错误！");
        }
        log.debug("-- Test AES ---------------------");
    }

    @Test
    public void testMD5() {
        log.debug("-- MD5 ---------------------");

        String key = "abcdef";
        String txt = "你好，1234,aBcDer&*(&(*";
        log.debug("  内容：[" + txt + "]，密钥：[" + key + "]");
        log.debug("  原文加密：" + MD5Utils.s_encode(txt));
        log.debug("  密钥加密：" + MD5Utils.s_encode(txt, key));
        log.debug("-- MD5 ---------------------");
    }

    @Test
    public void testBase64() {
        log.debug("-- Base64 ---------------------");

        String txt = "你好，1234,aBcDer&*(&(*";
        String str_e = null;
        byte[] byt_d = null;
        log.debug("  内容：[" + txt + "]");
        str_e = B64Utils.s_encode(txt.getBytes());
        log.debug("  加密:" + str_e);
        byt_d = B64Utils.s_decode(str_e);
        log.debug("  解密:" + new String(byt_d));

        log.debug("-- Base64 ---------------------");
    }

    @Test
    public void testDES() {
        log.debug("-- DES ---------------------");
        try {
            byte[] key = DESUtils.s_genkeys();
            log.debug("  私钥：[ " + new String(key) + "]");
            String txt = "你好，1234,aBcDer&*(&(*";
            byte[] byt_e = DESUtils.s_encrypt(key, txt.getBytes());
            log.debug("  加密内容: [" + txt + "] -> [" + new String(byt_e) + "] 长度：" + byt_e.length);

            byte[] byt_d = DESUtils.s_decrypt(key, byt_e);
            String str_d = new String(byt_d);
            log.debug("  解密: [" + str_d + "]");
            log.debug("  匹配结果:" + txt.equals(str_d));
        } catch (Exception e) {
            log.debug("私钥或者内容错误！");
        }
        log.debug("-- DES ---------------------");
    }

    @Test
    public void testRSA() {
        log.debug("-- RSA ---------------------");
        try {
            Map<String, Key> key_map = RSAUtils.s_genkeys_map("123");

//            Sun RSA public key, 1024 bits
//            modulus: 101799295182428744795505144996528983770529190547129454356838916476888256502005289240107974912970304726464852922390387153088384082171304329920303782387065477279147518721453180655575017240905228425534574705215608241330444395513135794984599960437491467012980369291666943809031411045825273968268308735404951984501
//            public exponent: 65537
//            Sun RSA public key, 1024 bits
//            modulus: 101799295182428744795505144996528983770529190547129454356838916476888256502005289240107974912970304726464852922390387153088384082171304329920303782387065477279147518721453180655575017240905228425534574705215608241330444395513135794984599960437491467012980369291666943809031411045825273968268308735404951984501
//            public exponent: 65537
            byte[] pub_key = RSAUtils.s_public_key_byte(key_map);
            byte[] pri_key = RSAUtils.s_private_key_byte(key_map);

            String pub_key_str = B64Utils.s_encode(pub_key);
            log.debug("  公钥              : [" + new String(pub_key) + "]");
            log.debug("  公钥BASE64: [" + pub_key_str + "]");
            log.debug("  私钥: [" + new String(pri_key) + "]");
            log.debug("  私钥BASE64: [" + B64Utils.s_encode(pub_key) + "]");

            String txt = "DABA-C5D2-05C127B8-77D330BCB674F506";
            log.debug("  内容：[" + txt + "]");
            byte[] byt_e = RSAUtils.s_encrypt_private(pri_key, txt.getBytes());
            byte[] byt_d = RSAUtils.s_decrypt_public(B64Utils.s_decode(pub_key_str), byt_e);
            log.debug("私钥加密，公钥解密: [" + new String(byt_d) + "]");
//            byt_d = RSAUtils.s_decrypt_private(pri_key, byt_e);
//            log.debug("私钥加密，私钥解密: [" + new String(byt_d) + "]");// 失败

            byt_e = RSAUtils.s_encrypt_public(pub_key, txt.getBytes());
            byt_d = RSAUtils.s_decrypt_private(pri_key, byt_e);
            log.debug("公钥加密，私钥解密: [" + new String(byt_d) + "]");
            byt_d = RSAUtils.s_decrypt_public(pri_key, byt_e);
            log.debug("公钥加密，公钥解密: [" + new String(byt_d) + "]");
        } catch (Exception e) {
            log.debug("私钥或者内容错误！");
        }
        log.debug("-- RSA ---------------------");
    }
}
