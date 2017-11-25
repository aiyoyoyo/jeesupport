package com.jees.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainTest {

	public static void main( String[] args ) {
		@SuppressWarnings( "resource" )
		ApplicationContext ctx = new ClassPathXmlApplicationContext( "classpath*:jees-core-database.xml" );

		TestController ctr = ( TestController ) ctx.getBean( "testController" );

//		ExTest( ctr );

		ExMoreDBTest( ctr );
	}

	public static void ExTest( TestController ctr ) {
		try {
			ctr.insertA();
		} catch ( Exception e ) {}
		try {
			ctr.insertATransSucc();
		} catch ( Exception e ) {}
		try {
			ctr.insertATransFail();
		} catch ( Exception e ) {}
		try {
			ctr.updateA();
		} catch ( Exception e ) {}
		try {
			ctr.updateATransSucc();
		} catch ( Exception e ) {}
		try {
			ctr.updateATransFail();
		} catch ( Exception e ) {}
		try {
			ctr.deleteA();
		} catch ( Exception e ) {}
		try {
			ctr.deleteATransSucc();
		} catch ( Exception e ) {}
		try {
			ctr.deleteATransFail();
		} catch ( Exception e ) {}
	}

	public static void ExMoreDBTest( TestController ctr ) {
		try {
			ctr.insertAB();
		} catch ( Exception e ) {}
		try {
			ctr.insertABTransSucc();
		} catch ( Exception e ) {}
		try {
			ctr.insertABTransFail();
		} catch ( Exception e ) {}
		try {
			ctr.otherTest();
		} catch ( Exception e ) { e.printStackTrace(); }
	}
}
