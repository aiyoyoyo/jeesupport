package com.jees.test;

import com.jees.common.CommonContextHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.*;

/**
 * 这里暂时不可以加入@SpringBootApplication注解，原因带分析。
 * @author aiyoyoyo
 */
//@SpringBootApplication
//@SpringBootConfiguration
@ImportResource("classpath:jees-core-database.xml")
public class ApplicationTest {
	public static void main( String[] args ) {
		SpringApplication.run( ApplicationTest.class, args);

		TestController ctr = CommonContextHolder.getBean( TestController.class );

		ctr.simpleTest();

//		ExTest( ctr );

//		ExMoreDBTest( ctr );
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
