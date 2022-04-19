package com.jees.tool.license;

import com.jees.tool.crypto.B64Utils;
import com.jees.tool.crypto.RSAUtils;

import java.io.*;
import java.security.Key;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * 用于生成应用所需的license文件
 * 
 * @author aiyoyoyo
 *
 */
public class LicenseUtils {

	public static void LicenseGenrator() throws Exception {
		System.out.println( "开始生成License文件，当前支持以下模式：" );
		System.out.println( "0. 测试License文件，查看本地机器码" );
		System.out.println( "1. 单机模式-仅验证License文件的有效性" );
		System.out.println( "2. 时限模式-增加时限验证" );
		System.out.println( "3. 联网模式-需联网验证" );
		System.out.println( "请选择对应的模式，回车确认：" );
		int mode = LicenseClient.MODE_SINGLE;
		@SuppressWarnings ( "resource" )
		Scanner sc = new Scanner( System.in );

		int step = 0;
		String s0 = "";
		String s1 = "";
		String s2 = "";
		while ( sc.hasNext() ) {
			while ( step == 0 ) {
				int m = sc.nextInt();
				
				switch( m ){
				case 0:
					s_test();
					break;
				case 1:
					mode = LicenseClient.MODE_SINGLE;
					step = 1;
					break;
				case 2:
					mode = LicenseClient.MODE_INTIME;
					step = 1;
					break;
				case 3:
					mode = LicenseClient.MODE_ONLINE;
					step = 1;
					break;
				}

				if ( step == 0 ) {
					System.out.println( "模式选择错误，请重新选择：" );
				} else {
					System.out.println( "选择模式为：" + m );
					System.out.println( "你可以输入任意字符串作为自定义初始私钥种子，长度为8~16：" );
				}
			}

			while ( step == 1 ) {
				if ( s0.equalsIgnoreCase( "" ) ) {
					s0 = sc.next();
					System.out.println( "请确认你使用的种子内容：" );
				}
				if ( s1.equalsIgnoreCase( "" ) ) {
					s1 = sc.next();
					if ( s0.equalsIgnoreCase( s1 ) ) {
						step = 2;
					} else {
						s0 = "";
						s1 = "";

						System.out.println( "内容不一致，请重新输入：" );
					}
				}
			}

			while( step == 2 ) {
				if( s2.equalsIgnoreCase( "" ) ){
					if( mode == LicenseClient.MODE_INTIME ) {
						System.out.println("请输入您想要过期的时间：格式为（yyyy-mm-dd hh:mm:ss）");
						s2 = sc.next();
					}
					break;
				}
			}
		}

		String seed = s0;
		switch ( mode ) {
		case LicenseClient.MODE_SINGLE:
			s_generate_single( seed );
			break;
		case LicenseClient.MODE_INTIME:
			s_generate_intime( s0, s2 );
			break;
		}
	}

	/**
	 * 编码密文内容：模式:机器码(xxxx-xxxx-xxxxxxxx-xxxxxxxxxxxxxxxx):服务日期(yyyy-mm-dd
	 * hh:MM:ss)
	 * 
	 * @param _mode
	 *            模式
	 * @param _code
	 *            机器码
	 * @param _days
	 *            服务日期
	 * @return
	 */
	private static String s_encode_string( int _mode , String _code , String _days ) {
		return _mode + ":" + _code + ":" + _days;
	}
	
	/**
	 * 解码密文内容
	 * @param _data 字符串二进制数组
	 * @return 密文字符串数组
	 */
	public static String[] s_decode_string( byte[] _data ){
		StringTokenizer st = new StringTokenizer( new String( _data ) , ":" );
		
		String[] str = new String[3];
		int idx = 0;
		
		while( st.hasMoreTokens() )
			str[ idx++ ] = st.nextToken();
		
		return str;
	}
	/**
	 * 生成License，并写入文件
	 * @param _seed 种子
	 * @param _time 可用时长
	 * @throws Exception 错误
	 */
	public static void s_generate( String _seed, String _time, int _mode ) throws Exception{
		System.out.println( "基础种子:" + _seed );
		System.out.println( "可用时长:" + _time );
		System.out.println( "模式:" + _mode );
		
		String code = LicenseSequences.s_sequence();
		System.out.println( "机器码:" + code );
		
		Map< String , Key > key_map = RSAUtils.s_genkeys_map( _seed );

		byte[] pri_key = RSAUtils.s_private_key_byte( key_map );
		byte[] pub_key = RSAUtils.s_public_key_byte( key_map );
		String pub_key_str = B64Utils.s_encode( pub_key );
		System.out.println( "用户公钥:" + pub_key_str );

		String str = s_encode_string( _mode , code , _time );
		byte[] byt_e = RSAUtils.s_encrypt_private( pri_key , str.getBytes() );

		s_write_license( pub_key_str , B64Utils.s_encode( byt_e ) );
	}
	
	/**
	 * 测试License内容有效性
	 */
	public static void s_test() {
		File file = new File( "" , "application.license" );
		String code = LicenseSequences.s_sequence();
		String[] txt = s_read_license( file );
		String pub_key_str = txt[ 0 ];
		System.out.println( "用户公钥:" + pub_key_str );
		
		byte[] pub_key;
		try {
			pub_key = B64Utils.s_decode( pub_key_str );
			RSAUtils.s_decrypt_public( pub_key , B64Utils.s_decode( txt[ 1 ] ) );
			System.out.println( "License文件经验证结果：有效，机器码：" + code );
		} catch ( Exception e ) {
			System.out.println( "License文件经验证结果：失败 机器码：" + code );
		}
		System.exit( 0 );
	}
	
	/**
	 * 创建单机模式License
	 * 通过机器码生成MD5加密内容
	 * 私钥由供应商分配，由客户保管
	 * 
	 * @param _seed 私钥种子
	 * @throws Exception 错误
	 */
	public static void s_generate_single( String _seed ) throws Exception {
		System.out.println( "当前选择单机License方式。" );
		s_generate( _seed, "", LicenseClient.MODE_SINGLE );
	}
	
	/**
	 * 创建单机模式License
	 * 通过机器码生成MD5加密内容
	 * 私钥由供应商分配，由客户保管
	 * 
	 * @param _seed 私钥种子
	 * @param _time 时长 秒
	 * @throws Exception 错误
	 */
	public static void s_generate_intime( String _seed, String _time ) throws Exception {
		System.out.println( "当前选择时长License方式。" );
		s_generate( _seed, _time, LicenseClient.MODE_INTIME );
	}

	/**
	 * 
	 * 将信息写入文件
	 * 
	 * @param _keys 用户公钥
	 * @param _data 加密内容
	 */
	public static void s_write_license( String _keys , String _data ) {
		try {
			File f = new File( "" , "application.license" );

			if ( f.exists() ) f.delete();

			FileWriter fw = new FileWriter( f );
			BufferedWriter bw = new BufferedWriter( fw );
			bw.write( _keys );
			bw.newLine();
			bw.write( _data );

			bw.close();
			fw.close();

			System.out.println( "License已生成，保存至路径：" + f.getAbsolutePath()   );
		} catch ( Exception e ) {
			System.err.println( "License文件生成失败：" + e.toString()   );
		}
	}

	/**
	 * 读取License文件信息
	 * 
	 * @param _file License文件
	 * @return [2]-0:Key 1:Txt
	 */
	public static String[] s_read_license( File _file ) {
		try {
			String[] txt = new String[ 2 ];
			FileReader fr = new FileReader( _file );
			BufferedReader br = new BufferedReader( fr );

			txt[ 0 ] = br.readLine();
			txt[ 1 ] = br.readLine();

			br.close();
			fr.close();

			return txt;
		} catch ( Exception e ) {
			System.err.println( "License文件读取失败：" + e.toString() );
		}
		return null;
	}
}
