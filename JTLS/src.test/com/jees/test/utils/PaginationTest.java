package com.jees.test.utils;

import org.junit.Test;

import com.jees.tool.pagination.PaginationUtils;

public class PaginationTest {
	
	@Test
	public void test() {
		int len = 10;
		for( int i = 1; i <= len; i ++){
			new PaginationUtils( i, len, 1 );
		}
	}
}
