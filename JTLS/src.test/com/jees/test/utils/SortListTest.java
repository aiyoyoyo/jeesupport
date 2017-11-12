package com.jees.test.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.jees.tool.utils.SortListUtil;

public class SortListTest {
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
		list.forEach( o -> {System.out.println( "--" + o.i + "|" + o.s ); } );
		System.out.println( "------------" );
		SortListUtil.sort( list , "i" );
		list.forEach( o -> {System.out.println( "i=" + o.i ); } );
		System.out.println( "------------" );
		SortListUtil.sort( list , "s" );
		list.forEach( o -> {System.out.println( "s=" + o.s ); } );
	}
}
