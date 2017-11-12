package com.jees.test.utils;

import org.junit.Test;

import com.jees.tool.license.LicenseSequences;

public class LicenseTest {
	@Test
	public void testLocalCode() {
		try {
			String sequence = LicenseSequences.s_sequence();
			
			System.out.println( "本地机器码:" + sequence );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void testLicense(){
		System.out.println( "测试应用运行 开始-----" );
		boolean running = true;
		while( running ){
			
		}
		System.out.println( "测试应用运行 结束-----" );
	}
}
