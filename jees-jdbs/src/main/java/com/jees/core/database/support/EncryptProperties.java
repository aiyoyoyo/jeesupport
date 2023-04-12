package com.jees.core.database.support;

import com.jees.tool.crypto.AESUtils;
import lombok.extern.log4j.Log4j2;

import java.util.Enumeration;
import java.util.Properties;

/**
 * 提供数据库明文加密的简易支持。
 *
 * @author aiyoyoyo
 */
@Log4j2
public class EncryptProperties extends Properties {

    private static final long serialVersionUID = -8248130519122355306L;

    /**
     * 解密用密钥
     */
    private String keys;

    public void setKeys(String _keys) {
        this.keys = _keys;
    }

    public void setProps(Properties _props) {
        Enumeration<Object> e = _props.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String val = _props.getProperty(key);
            setProperty(key, val);
        }
    }

    @Override
    public Object setProperty(String _key, String _val) {
        try {
            if (_key.equalsIgnoreCase("user") || _key.equalsIgnoreCase("password")
                    || _key.equalsIgnoreCase("url")) {
                return super.setProperty(_key, AESUtils.s_decrypt(this.keys, _val));
            } else {
                return super.setProperty(_key, _val);
            }
        } catch (Exception e) {
            log.error("密钥错误，请确认密钥和密文是否配套。");
        }
        return super.setProperty(_key, "");
    }
}
