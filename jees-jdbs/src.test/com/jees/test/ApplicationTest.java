package com.jees.test;

import com.jees.common.CommonContextHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.*;

/**
 * 启动时需要剔除DataSourceAutoConfiguration，HibernateJpaAutoConfiguration的自动配置器。
 * @author aiyoyoyo
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@ComponentScan("com.jees")
public class ApplicationTest {
	public static void main( String[] args ) {
		SpringApplication.run( ApplicationTest.class, args);

		TestController ctr = CommonContextHolder.getBean( TestController.class );
		ctr.insertATransSucc();
//		ctr.simpleTest();

//		ctr.absTest();

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
