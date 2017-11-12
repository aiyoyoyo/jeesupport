package com.jees.tool.license;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jees.tool.crypto.B64Utils;
import com.jees.tool.crypto.RSAUtils;

/**
 * 简易的License检查方案，提供4种模型：<br/>
 * 0、单机模式：仅验证license的有效性<br/>
 * 1、时限模式：验证license的有效性，并验证运行时长<br/>
 * 2、联网模式：通过验证服务器验证本地的License是否有效<br/>
 * @author aiyoyoyo
 * @version 1.0
 *       
 */
@Component
public class LicenseClient {
	/** 单机模式  **/
	public static final int		MODE_SINGLE		= 0;
	/** 时限模式  **/
	public static final int		MODE_INTIME		= 1;
	/** 联网模式  **/
	public static final int		MODE_ONLINE		= 2;
	
	/** License加载 **/
	public static final int		STATE_NONE			= 0;
	/** License无效或文件错误等 **/
	public static final int		STATE_FAILD		= 1;
	/** License超时 **/
	public static final int		STATE_TIMEOUT		= 2;
	/** License非本机运行 **/
	public static final int		STATE_CODEERR		= 3;
	/** License正常 **/
	public static final int		STATE_SUCCESS		= 9;
	
	/** 验证模式 **/
	public static final int		CONTENT_MODE		= 0;
	/** 机器码 **/
	public static final int		CONTENT_CODE		= 1;
	/** 总可用时长 **/
	public static final int		CONTENT_TIME		= 2;
	
	private static Logger			logger				= LogManager.getLogger( LicenseClient.class );
	
	/** 验证状态 **/
	private static int			license_state		= STATE_NONE;
	/** License内容 **/
	private static byte[]			license_content;
	/** 用户公钥 **/
	private static byte[]			license_keys;
	
	/**
	 * 启用服务10秒后，加载License，并检查有效性，无效时将停止服务。
	 */
	@Scheduled( fixedRate=1000 * 60 * 60 * 24 * 360, initialDelay = 1000 * 10 ) 
	public void scheduledOnStart() {
		logger.info( "检查License是否有效..." );
		File file = new File( "" , "application.license" );
		String[] txt = LicenseUtils.s_read_license( file );
		
		try {
			license_keys = B64Utils.s_decode( txt[ 0 ] );
		} catch ( UnsupportedEncodingException e ) {
			license_state = STATE_FAILD;
		}
		try {
			license_content = B64Utils.s_decode( txt[ 1 ] );
		} catch ( Exception e ) {
			license_state = STATE_FAILD;
		}
		
		_validateLicense();
		logger.info( "License有效，服务器可正常运行。" );
	}
	
	/**
	 * 服务启动后检查License有效性，每24小时检查一次
	 */
	@Scheduled( fixedRate = 1000 * 60 * 60 * 24 * 1, initialDelay = 1000 * 60 )// * 60 * 24 * 1 ) 
	public void scheduledOnRunnig() {
		logger.info( "检查License是否有效..." );
		
		_validateLicense();
		logger.info( "License有效，服务器可正常运行。" );
	}
	
	/**
	 * 检查License的内容
	 */
	private void _validateLicense(){
		String[] d_txt = null;
		try {
			d_txt = LicenseUtils.s_decode_string( RSAUtils.s_decrypt_public( license_keys , license_content ) );
		} catch ( Exception e ) {
			license_state = STATE_FAILD;
		}
		int mode = 0;
		switch( license_state ){
		case STATE_NONE:
			mode = Integer.parseInt( d_txt[CONTENT_MODE] );
			break;
		case STATE_FAILD:
		case STATE_TIMEOUT:
		case STATE_CODEERR:
			logger.error( "用户License信息无效，将停止服务器运行。错误代码：" + license_state );
			System.exit( 0 );
			break;
		case STATE_SUCCESS:
			break;
		}
		
		switch( mode ){
		case MODE_SINGLE:
			_validateSingle( d_txt[CONTENT_CODE] );
			break;
		case MODE_INTIME:
			break;
		case MODE_ONLINE:
			break;
		}
	}
	
	/**
	 * 单机验证
	 */
	private void _validateSingle( String _code ){
		if( _code == LicenseSequences.s_sequence() ){
			license_state = STATE_SUCCESS;
		}else{
			license_state = STATE_CODEERR;
			logger.fatal( "用户License信息无效，将再下次运行时停止服务器运行。" );
		}
	}
}
