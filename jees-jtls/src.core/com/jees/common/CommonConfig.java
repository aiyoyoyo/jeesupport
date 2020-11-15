package com.jees.common;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.StringTokenizer;

/**
 * 调用参数配置,大多数内容由application.properties内容获取.
 * @author aiyoyoyo
 * @version 1.0
 */
@Log4j2
@Component
@Configuration
public class CommonConfig {
	private static Environment environment;

	@Autowired
	protected  void setEnvironment( Environment environment ){
		CommonConfig.environment = environment;
	}

	public static String get( String _key ) {
		if( environment == null ) {
			log.warn( "environment is null.");
			return "";
		}
		String val = environment.getProperty( _key );
		log.debug( "K=[" + _key + "], V=[" + val + "]");
		return val;
	}

	public static String getString( String _key ) {
		return getString( _key, "" );
	}

	public static boolean getBoolean( String _key ) {
		return getBoolean( _key, false );
	}

	public static int getInteger( String _key ) {
		return getInteger( _key , 0 );
	}

	public static float getFloat( String _key ) {
		return getFloat( _key, 0.F );
	}

	public static long getLong( String _key ) {
		return getLong( _key, 0L );
	}

	public static String getString( String _key, String _def ) {
		String val = get( _key );
		return val == null ? _def : val;
	}

	public static boolean getBoolean( String _key, boolean _def ) {
		String val = getString( _key, _def ? "true" : "false" );
		return val.equalsIgnoreCase( "true" );
	}

	public static int getInteger( String _key, int _def ) {
		try {
			return Integer.parseInt( getString( _key ) );
		} catch ( Exception e ) {
			return _def;
		}
	}

	public static float getFloat( String _key, float _def ) {
		try {
			return Float.parseFloat( getString( _key ) );
		} catch ( Exception e ) {
			return _def;
		}
	}

	public static long getLong( String _key, long _def ) {
		try {
			return Long.parseLong( getString( _key ) );
		} catch ( Exception e ) {
			return _def;
		}
	}

	public static boolean getEquals( String _key , String _word ) {
		String val = getString( _key );
		return val.equalsIgnoreCase( _word );
	}

	public static StringTokenizer getStringTokenizer( String _key ){
		String val = getString( _key );
		return new StringTokenizer( val, "," );
	}

	public static <T> T get( String _key, T _t ){
		return _cast_value( _key, _t );
	}

	private static <T> T _cast_value(Object _obj, T _def ){
		String val;
		if( _obj != null ){
			val = _obj.toString();
		}else return _def;
		try{
			Object tpl;
			if( _def instanceof Integer ) tpl = getInteger( val );
			else if( _def instanceof Float ) tpl = getFloat( val );
			else if( _def instanceof Boolean ) tpl = getBoolean( val );
			else if( _def instanceof Long ) tpl = getLong( val );
			else tpl = getString( val );
			return (T) tpl;
		}catch ( Exception e ){
			return _def;
		}
	}
}
