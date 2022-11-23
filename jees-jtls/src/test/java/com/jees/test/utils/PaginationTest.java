package com.jees.test.utils;

import com.jees.tool.pagination.PaginationUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaginationTest {
	static Logger logger =  LoggerFactory.getLogger( PaginationTest.class );
	@Test
	public void test() {
		int len = 10;
		for( int i = 1; i <= len; i ++){
			PaginationUtils pg = new PaginationUtils(i, len, 5);
			logger.debug( "请求页:" + i + ", 返回页码:" + pg.getIndex() + ", 总页数：" + pg.getPages() );
		}
	}
}
