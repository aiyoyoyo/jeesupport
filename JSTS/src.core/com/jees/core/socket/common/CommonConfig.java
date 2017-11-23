package com.jees.core.socket.common;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 调用参数配置，大多数内容由.properties内容获取。
 * 
 * @author aiyoyoyo
 * @version 1.0
 */
@Component
public class CommonConfig {
	
	private static Properties	defineConfig;
	
	@Autowired
	protected void setDefineConfig( Properties defineConfig ) {
		CommonConfig.defineConfig = defineConfig;
	}
	
	public static String get( String _key ) {
		return defineConfig.getProperty( _key );
	}
	
	public static String getString( String _key ){
		String val = defineConfig.getProperty( _key );
		return val == null ? "" : val;
	}
	public static boolean getBoolean( String _key ){
		String val = getString( _key );
		return val.equalsIgnoreCase( "true" ) ? true : false;
	}
	public static int getInteger( String _key ){
		try{
			return Integer.parseInt( getString( _key ) );
		}catch (Exception e) {
			return 0;
		}
	}
	public static float getFloat( String _key ){
		try{
			return Float.parseFloat( getString( _key ) );
		}catch (Exception e) {
			return 0.f;
		}
	}
	public static boolean getEquals( String _key, String _word ){
		String val = getString( _key );
		return val.equalsIgnoreCase( _word );
	}
}
