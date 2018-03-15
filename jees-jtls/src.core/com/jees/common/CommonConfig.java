package com.jees.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 调用参数配置，大多数内容由.properties内容获取。
 * 
 * @author aiyoyoyo
 * @version 1.0
 */
@Component
@Configuration
public class CommonConfig {
	private static Environment environment;

	@Autowired
	protected  void setEnvironment( Environment environment ){
		CommonConfig.environment = environment;
	}

	public static String get( String _key ) {
		String val = environment.getProperty( _key );
		CommonLogger.debug( CommonConfig.class,"获取上下文：KEY=[" + _key + "], VAL=[" + val + "]");
		return val;
	}

	public static String getString( String _key ) {
		String val = get( _key );
		return val == null ? "" : val;
	}

	public static boolean getBoolean( String _key ) {
		String val = getString( _key );
		return val.equalsIgnoreCase( "true" ) ? true : false;
	}

	public static int getInteger( String _key ) {
		try {
			return Integer.parseInt( getString( _key ) );
		} catch ( Exception e ) {
			return 0;
		}
	}

	public static float getFloat( String _key ) {
		try {
			return Float.parseFloat( getString( _key ) );
		} catch ( Exception e ) {
			return 0.f;
		}
	}

	public static long getLong( String _key ) {
		try {
			return Long.parseLong( getString( _key ) );
		} catch ( Exception e ) {
			return 0L;
		}
	}

	public static boolean getEquals( String _key , String _word ) {
		String val = getString( _key );
		return val.equalsIgnoreCase( _word );
	}
}
