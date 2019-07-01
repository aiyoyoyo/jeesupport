package com.jees.test;

import com.jees.common.CommonContextHolder;
import com.jees.core.database.support.IRedisDao;
import com.jees.test.entity.RedisUser;
import org.joda.time.DateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 启动时需要剔除DataSourceAutoConfiguration，HibernateJpaAutoConfiguration的自动配置器。
 * @author aiyoyoyo
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@ComponentScan( "com.jees" )
@DependsOn({"commonContextHolder", "commonConfig" })
public class JdbsApplication{
	public static void main( String[] args ) throws Exception{
		SpringApplication.run( JdbsApplication.class, args);

//		TestController ctr = CommonContextHolder.getBean( TestController.class );
//		ctr.insertATransSucc();
//		ctr.simpleTest();

//		ctr.absTest();

//		ExTest( ctr );

//		ExMoreDBTest( ctr );

		IRedisDao dao = CommonContextHolder.getBean( IRedisDao.class );
		dao.initialize();
		Map<Integer, RedisUser> maps = new HashMap<>();
		for( int i = 0; i < 1; i ++ ){
			RedisUser a = new RedisUser();
			a.setId( i );
			a.setDate( new Date( DateTime.now().getMillis() ) );
//			dao.insert( a );
			maps.put( a.getId(), a );
		}

		dao.insertMap( maps, RedisUser.class );

		Map map = dao.findHashAll( RedisUser.class );
		System.out.println(map.size());
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
