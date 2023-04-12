package com.jees.common;

import com.jees.tool.utils.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.StringTokenizer;

/**
 * 调用参数配置,大多数内容由application.properties内容获取.
 *
 * @author aiyoyoyo
 * @version 1.0
 */
@Log4j2
@Component
@Configuration
public class CommonConfig {
    private static Environment environment;
    public static final String SPLIT_DELIM = ",";

    @Autowired
    protected void setEnvironment(Environment environment) {
        CommonConfig.environment = environment;
    }

    /**
     * 获取所有配置内容中的某个Key对应的Value
     *
     * @param _key 标签名
     * @return String null
     */
    public static String get(String _key) {
        if (environment == null) {
            log.warn("Spring Environment没有正确加载，请检查配置文件。");
            return null;
        }
        String val = environment.getProperty(_key);
        log.debug("K=[" + _key + "], V=[" + val + "]");
        return val;
    }

    public static String getString(String _key) {
        return getString(_key, null);
    }

    public static boolean getBoolean(String _key) {
        return getBoolean(_key, false);
    }

    public static int getInteger(String _key) {
        return getInteger(_key, 0);
    }

    public static float getFloat(String _key) {
        return getFloat(_key, 0.F);
    }

    public static long getLong(String _key) {
        return getLong(_key, 0L);
    }

    public static byte getByte(String _key) {
        return getByte(_key, (byte) 0);
    }

    public static double getDouble(String _key) {
        return getDouble(_key, 0D);
    }

    public static String getString(String _key, String _def) {
        String val = get(_key);
        return val == null ? _def : val;
    }

    public static boolean getBoolean(String _key, boolean _def) {
        String val = getString(_key, _def ? "true" : "false");
        return "true".equalsIgnoreCase(val);
    }

    public static int getInteger(String _key, int _def) {
        try {
            return Integer.parseInt(getString(_key));
        } catch (Exception e) {
            return _def;
        }
    }

    public static float getFloat(String _key, float _def) {
        try {
            return Float.parseFloat(getString(_key));
        } catch (Exception e) {
            return _def;
        }
    }

    public static long getLong(String _key, long _def) {
        try {
            return Long.parseLong(getString(_key));
        } catch (Exception e) {
            return _def;
        }
    }

    public static byte getByte(String _key, byte _def) {
        try {
            return Byte.parseByte(getString(_key));
        } catch (Exception e) {
            return _def;
        }
    }

    public static double getDouble(String _key, double _def) {
        try {
            return Double.parseDouble(getString(_key));
        } catch (Exception e) {
            return _def;
        }
    }

    public static boolean equals(String _key, String _word) {
        return equals(_key, _word, false);
    }

    public static boolean equals(String _key, String _word, boolean _case) {
        String val = getString(_key);
        return _case ? val.equalsIgnoreCase(_word) : val.equals(_word);
    }

    public static StringTokenizer getStringTokenizer(String _key) {
        return getStringTokenizer(_key, SPLIT_DELIM);
    }

    public static StringTokenizer getStringTokenizer(String _key, String _delim) {
        String val = getString(_key);
        if (StringUtil.isEmpty(val)) {
            return new StringTokenizer("", _delim);
        }
        return new StringTokenizer(val, _delim);
    }

    public static <T> T[] getArray(String _key, Class<T> _t) {
        return getArray(_key, _t, SPLIT_DELIM);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] getArray(String _key, Class<T> _t, String _delim) {
        StringTokenizer st = getStringTokenizer(_key, _delim);
        T[] t = (T[]) Array.newInstance(_t, st.countTokens());

        for (int i = 0; i < t.length; i++) {
            String val = st.nextToken();
            t[i] = StringUtil.cover(val, _t);
        }

        return t;
    }

    public static <T> T get(String _key, T _t) {
        return _cast_value(_key, _t);
    }

    @SuppressWarnings("unchecked")
    private static <T> T _cast_value(Object _obj, T _def) {
        String val;
        if (_obj != null) {
            val = _obj.toString();
        } else {
            return _def;
        }
        try {
            Object tpl;
            if (_def instanceof Integer) {
                tpl = getInteger(val, (Integer) _def);
            } else if (_def instanceof Float) {
                tpl = getFloat(val, (Float) _def);
            } else if (_def instanceof Boolean) {
                tpl = getBoolean(val, (Boolean) _def);
            } else if (_def instanceof Long) {
                tpl = getLong(val, (Long) _def);
            } else if (_def instanceof Byte) {
                tpl = getByte(val, (Byte) _def);
            } else if (_def instanceof Double) {
                tpl = getDouble(val, (Double) _def);
            } else {
                tpl = getString(val, (String) _def);
            }
            return (T) tpl;
        } catch (Exception e) {
            return _def;
        }
    }
}
