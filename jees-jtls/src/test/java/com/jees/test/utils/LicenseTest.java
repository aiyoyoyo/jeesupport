package com.jees.test.utils;

import com.jees.tool.license.LicenseSequences;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenseTest {
	static Logger logger =  LoggerFactory.getLogger( LicenseTest.class );
	@Test
	public void testLocalCode() {
		try {
			String sequence = LicenseSequences.s_sequence();
			logger.debug( "本地机器码:" + sequence );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void testLicense(){
		logger.debug( "测试应用运行 开始-----" );
		boolean running = true;
		while( running ){
			
		}
		logger.debug( "测试应用运行 结束-----" );
	}
}
