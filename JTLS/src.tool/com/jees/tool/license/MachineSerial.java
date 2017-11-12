package com.jees.tool.license;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 获取计算机的相关信息
 * 
 * @author aiyoyoyo
 */
public class MachineSerial {

	private static Logger logger = LogManager.getLogger( MachineSerial.class );

	/**
	 * 获取CPU序列号
	 * @return
	 */
	public static String s_serial_CPU() {
		try {
			Process process = Runtime.getRuntime().exec( new String[] { "wmic" , "cpu" , "get" , "ProcessorId" } );
			process.getOutputStream().close();
			@SuppressWarnings ( "resource" )
			Scanner sc = new Scanner( process.getInputStream() );
			sc.next();
			return sc.next();
		} catch ( IOException e ) {
			logger.error( "生成CPUSerial失败" , e );
		}
		return null;
	}

	/**
	 * 获取磁盘卷标
	 * @param drive
	 *            硬盘驱动器分区 如C,D
	 * @return
	 */
	public static String s_serial_HD( String _drive ) {
		StringBuilder result = new StringBuilder();
		try {
			File file = File.createTempFile( "tmp" , ".vbs" );
			file.deleteOnExit();
			try ( FileWriter fw = new java.io.FileWriter( file ) ) {
				String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n" + "Set colDrives = objFSO.Drives\n"
				        + "Set objDrive = colDrives.item(\"" + _drive + "\")\n" + "Wscript.Echo objDrive.SerialNumber";
				fw.write( vbs );
			}
			Process p = Runtime.getRuntime().exec( "cscript //NoLogo " + file.getPath() );
			try ( BufferedReader input = new BufferedReader( new InputStreamReader( p.getInputStream() ) ) ) {
				String line;
				while ( ( line = input.readLine() ) != null ) {
					result.append( line );
				}
			}
			file.delete();
		} catch ( Throwable e ) {
			logger.error( "生成HDSerial失败" , e );
		}
		if ( result.length() < 1 ) {
			logger.info( "无磁盘ID被读取" );
		}

		return result.toString();
	}

	/**
	 * 获取MAC地址
	 * @param _ia
	 * @throws SocketException
	 */
	public static void _s_serial_MAC( InetAddress _ia ) throws SocketException {
		byte[] mac = NetworkInterface.getByInetAddress( _ia ).getHardwareAddress();

		StringBuffer sb = new StringBuffer( "" );
		for ( int i = 0; i < mac.length; i++ ) {
			if ( i != 0 ) {
				sb.append( "-" );
			}
			// 字节转换为整数
			int temp = mac[ i ] & 0xff;
			String str = Integer.toHexString( temp );

			if ( str.length() == 1 ) {
				sb.append( "0" + str );
			} else {
				sb.append( str );
			}
		}

		logger.info( "本机MAC地址:" + sb.toString().toUpperCase() );
	}
	
	/**
	 * Window系统机器码规则
	 * @return
	 */
	private static String _s_os_windows_code() {
		String cpu = s_serial_CPU();
		String hd = s_serial_HD( "C" );

		if ( cpu == null || hd == null ) { return null; }
		return cpu + hd;
	}
	
	/**
	 * 获取当前计算机的机器码
	 * @return
	 */
	public static String s_code() {
		return _s_os_windows_code();
	}
}
