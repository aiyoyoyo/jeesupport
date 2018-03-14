package com.jees.tool.license;

import com.jees.tool.crypto.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通过计算机信息生成可用许可
 * 
 * @author aiyoyoyo
 */
public class LicenseSequences {
	private static Logger logger			= LoggerFactory.getLogger( LicenseSequences.class );
	
	/** 
     * 将信息加密，并格式化为:<br/>
     * 字母单位: 4-4-8-16 <br/>
     * XXXX-XXXX-XXXXXXXX-XXXXXXXXXXXXXXXX
     * @param _str 待加密内容
     * @return 
     */  
    private static String _s_genmd5_code(String _str) {
    	String txt = MD5Utils.s_encode( _str );
    	try {
			return _s_format_code( txt );
		} catch ( Exception e ) {
			logger.error( "MD5摘要失败！", e );
		}
    	return null;
    }
    
    /** 
     * 将很长的字符串以固定的位数分割开，以便于人类阅读 
     * @param _str 待格式化内容
     * @return  
     * @throws Exception 
     */  
    private static String _s_format_code( String _str ) throws Exception{
    	if( _str.length() != 32 ) throw new Exception( "MD5信息长度不符合。" );
    	
        String c0 = _str.substring( 0, 4 );
        String c1 = _str.substring( 4, 8 );
        String c2 = _str.substring( 8, 16 );
        String c3 = _str.substring( 16, 32 );
    	
    	String new_str = c0 + "-" + c1 + "-" + c2 + "-" + c3;
    	
    	logger.debug( "格式化编码：" + new_str );
    	return new_str;
    }
    
    /**
     * 获取本机的机器码后，进行格式化及转大写
     * @return
     */
    public static String s_sequence() {          
        String code = MachineSerial.s_code();
                  
        return _s_genmd5_code( code ).toUpperCase();  
    }  
}
