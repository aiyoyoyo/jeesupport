package com.jees.tool.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

/**
 * 提供基础数据类型的相关操作
 * 
 * @author aiyoyoyo
 *
 */
public class DataUtil {

	public static final String Charset_UTF8 = "UTF-8";

	/**
	 * int转byte[]
	 *
	 * @param _val
	 * @return
	 */
	public static byte[] int2bytes(int _val) {
		return new byte[]{(byte) ((_val >> 24) & 0xFF), (byte) ((_val >> 16) & 0xFF),
				(byte) ((_val >> 8) & 0xFF), (byte) (_val & 0xFF)};
	}

	/**
	 * byte[]转int
	 *
	 * @param _val
	 * @return
	 */
	public static int bytes2int(byte[] _val) {
		return _val[3] & 0xFF | (_val[2] & 0xFF) << 8 | (_val[1] & 0xFF) << 16 | (_val[0] & 0xFF) << 24;
	}

	/**
	 * String转byte[]
	 *
	 * @param _val
	 * @param _chs 字符解码类型，建议UTF-8，参考：DataUtil.Charset_UTF8
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] str2bytes(String _val, String _chs) throws UnsupportedEncodingException {
		return _val.getBytes(_chs);
	}

	/**
	 * bytes转String
	 *
	 * @param _val
	 * @param _chs 字符解码类型，建议UTF-8，参考：DataUtil.Charset_UTF8
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String bytes2str(byte[] _val, String _chs) throws UnsupportedEncodingException {
		return new String(_val, _chs);
	}

	/**
	 * 将int类型的数字进行高低位互转
	 *
	 * @param _val
	 * @return
	 */
	public static int warpHL(int _val) {
		return ByteBuffer.wrap(int2bytes(_val)).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}

	/**
	 * 将long类型的数字进行高低位互转
	 *
	 * @param _val
	 * @return
	 */
	public static long warpHL(long _val) {
		return ByteBuffer.wrap(long2bytes(_val)).order(ByteOrder.LITTLE_ENDIAN).getLong();
	}

	/**
	 * 截取bytes
	 *
	 * @param _data
	 * @param _start
	 * @param _end
	 * @return
	 */
	public static byte[] subBytes(byte[] _data, int _start, int _end) {
		byte[] ret = new byte[_end - _start];
		for (int i = 0; (_start + i) < _end; i++) {
			ret[i] = _data[_start + i];
		}
		return ret;
	}

	/**
	 * long转bytes
	 *
	 * @param _val
	 * @return
	 */
	public static byte[] long2bytes(long _val) {
		byte[] ret = new byte[8];
		for (int i = 0; i < 8; ++i) {
			int offset = 64 - (i + 1) * 8;
			ret[i] = (byte) ((_val >> offset) & 0xff);
		}
		return ret;
	}

	/**
	 * bytes转long
	 *
	 * @param _val
	 * @return
	 */
	public static long bytes2long(byte[] _val) {
		long num = 0;
		for (int i = 0; i < 8; ++i) {
			num <<= 8;
			num |= (_val[i] & 0xff);
		}
		return num;
	}

	/**
	 * 16进制字符串转byte[]
	 *
	 * @param _val
	 * @return byte[]
	 */
	public static byte[] hex2bytes(String _val) {
		// 奇数位补0
		if (_val.length() % 2 != 0) {
			_val = "0" + _val;
		}

		int length = _val.length();
		ByteBuffer buffer = ByteBuffer.allocate(length / 2);
		for (int i = 0; i < length; i++) {
			String hex_str = _val.charAt(i) + "";
			i++;
			hex_str += _val.charAt(i);
			byte b = (byte) Integer.parseInt(hex_str, 16);
			buffer.put(b);
		}
		return buffer.array();
	}

	/**
	 * byte[]转16进制字符串
	 *
	 * @param _val
	 * @return
	 */
	public static String bytes2hex(byte[] _val) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < _val.length; i++) {
			buffer.append(byte2hex(_val[i]));
		}
		return buffer.toString();
	}

	/**
	 * byte转16进制字符
	 *
	 * @param _val
	 * @return
	 */
	public static String byte2hex(byte _val) {
		String hex = Integer.toHexString(_val & 0xFF);
		if (hex.length() == 1) hex = '0' + hex;
		return hex.toUpperCase(Locale.getDefault());
	}
}
