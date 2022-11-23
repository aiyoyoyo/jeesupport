package com.jees.test.utils;

import com.jees.tool.utils.SortListUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SortListTest {
	static Logger logger =  LoggerFactory.getLogger( PaginationTest.class );
	private class A {
		public int		i	= 0;
		public String	s;
	}

	@Test
	public void test() {
		List< A > list = new ArrayList<>();
		Random r = new Random();
		for ( int i = 0; i < 10; i++ ) {
			A a = new A();

			a.i = r.nextInt(20);
			a.s = "" + r.nextInt(20);
			
			list.add( a );
		}
		list.forEach( o -> {logger.debug( "--" + o.i + "|" + o.s ); } );
		logger.debug( "---------------------------------" );
		SortListUtil.sort( list , "i" );
		list.forEach( o -> {logger.debug( "i=" + o.i ); } );
		logger.debug( "---------------------------------" );
		SortListUtil.sort( list , "s" );
		list.forEach( o -> {logger.debug( "s=" + o.s ); } );
	}
}
