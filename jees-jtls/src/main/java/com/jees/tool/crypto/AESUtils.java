package com.jees.tool.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 通过AES方式，对文本内容进行加密和解密
 *
 * @author aiyoyoyo
 */
public class AESUtils {
    /**
     * 密钥算法
     */
    public static final String ALGORITHM = "AES";
    public static final String SHA1PRNG = "SHA1PRNG";
    /**
     * 密钥长度
     */
    private static final int KEY_SIZE = 128;

    /**
     * 解密
     *
     * @param _key 加密私钥
     * @param _txt 待解密内容
     * @return 解密结果
     * @throws Exception 私钥错误，无法解密时，会抛出对应的异常
     */
    public static String s_decrypt(String _key, String _txt) throws Exception {
        SecretKey secret_key = new SecretKeySpec(B64Utils.s_decode(_key), ALGORITHM);
        byte[] raw = secret_key.getEncoded();
        SecretKeySpec secret_key_spec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secret_key_spec);
        return new String(cipher.doFinal(B64Utils.s_decode(_txt)));
    }

    /**
     * 加密
     *
     * @param _key 加密私钥
     * @param _txt 待加密内容
     * @return 加密结果
     * @throws Exception 加密异常
     */
    public static String s_encrypt(String _key, String _txt) throws Exception {
        SecretKey secret_key = new SecretKeySpec(B64Utils.s_decode(_key), ALGORITHM);
        byte[] raw = secret_key.getEncoded();
        SecretKeySpec secret_key_spec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secret_key_spec);
        return B64Utils.s_encode(cipher.doFinal(_txt.getBytes()));
    }

    /**
     * 创建私钥
     *
     * @param _arg 生成私钥的种子
     * @return 返回私钥
     * @throws NoSuchAlgorithmException 生成异常
     */
    public static String s_genkeys(String _arg) throws NoSuchAlgorithmException {
        KeyGenerator key_generator = KeyGenerator.getInstance(ALGORITHM);
        SecureRandom secure_random = SecureRandom.getInstance(SHA1PRNG);
        if (_arg != null && !"".equals(_arg)) {
            secure_random.setSeed(_arg.getBytes());
        }

        key_generator.init(KEY_SIZE, secure_random);
        SecretKey secret_key = key_generator.generateKey();
        return B64Utils.s_encode(secret_key.getEncoded());
    }
}
