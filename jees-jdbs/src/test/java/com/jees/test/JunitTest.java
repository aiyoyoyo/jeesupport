package com.jees.test;

import com.jees.common.CommonContextHolder;
import com.jees.core.database.support.AbsRedisDao;
import com.jees.core.database.support.AbsSupportDao;
import com.jees.core.database.support.IRedisDao;
import com.jees.test.entity.RedisUser;
import com.jees.test.entity.TabA;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 测试方法,执行日志默认在工程目录/logs下
 * 
 * @author aiyoyoyo
 * 
 */
@RunWith( SpringJUnit4ClassRunner.class )
@SpringBootTest
@ComponentScan("com.jees")
@Log4j2
@PropertySource(value={"classpath: config/com.jees.test.cfg"})
public class JunitTest implements Runnable {
	public long				id;
	public int				right;
	public int				faild_a;
	public int				faild_i;
	public int				faild_r;

	@Autowired
	public TestController	ctr;

	@Autowired
	AbsRedisDao dao;

	@Autowired
	AbsSupportDao dao2;

	@Test
	public void RedisTest() throws Exception{
		CommonContextHolder.getBean( IRedisDao.class ).initialize();

		for( int i = 0; i < 3000; i ++ ){
			RedisUser a = new RedisUser();
			a.setId( i );
			a.setDate( new Date( DateTime.now().getMillis() ) );
			dao.insert( a );
		}

//		RedisUser u = dao.findById( 1, 1L, RedisUser.class );
//		System.out.println( new DateTime( u.getDate().getTime() ).getMillisOfSecond() );
	}
	@Test
//	@Transactional
	public void SimpleTest(){
		ctr.simpleTest();
	}
//	 @Test
	public void ExTest() {
		try {
//			ctr.insertA();
		} catch ( Exception e ) {}
		try {
			ctr.insertATransSucc();
		} catch ( Exception e ) {}
		try {
//			ctr.insertATransFail();
		} catch ( Exception e ) {}
		try {
//			ctr.updateA();
		} catch ( Exception e ) {}
		try {
//			ctr.updateATransSucc();
		} catch ( Exception e ) {}
		try {
//			ctr.updateATransFail();
		} catch ( Exception e ) {}
		try {
//			ctr.deleteA();
		} catch ( Exception e ) {}
		try {
//			ctr.deleteATransSucc();
		} catch ( Exception e ) {}
		try {
//			ctr.deleteATransFail();
		} catch ( Exception e ) {}
	}

	// @Test
	public void ExMoreDBTest() {
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
		} catch ( Exception e ) {}
	}

//	@Test
	public void test1() {
		try {
			ctr.insertABTransFail();
		} catch ( Exception e ) {
			String str = e.toString();
			if ( str.indexOf( "identifier of an instance of" ) != - 1 )
				log.error( "--执行的实体对象不符合操作规则，例如ID发生了变化后执行了更新操作。" );
			else if ( str.indexOf( "The given object has a null identifier" ) != - 1 )
				log.error( "--执行的实体对象不符合操作规则，例如更新或删除为Null的对象。" );
			else if ( str.indexOf( "Could not obtain transaction-synchronized Session for current thread" ) != - 1 )
				log.error( "--业务方法没有显式声明事务注解@Transactional。" );
			else if ( str.indexOf( "没有找到数据库" ) != - 1 ) log.error( "--没有配置的数据库连接池。" );
			else log.error( "--其他错误，待分类说明：" , e );
		}
		log.info( "----------------run test1 end---------------" );
	}

	/**
	 * 并发测试。1000个线程各100次访问。
	 */
//	 @Test
	public void test2() {
		log.debug( "----------------run test2 start---------------" );
		for ( int i = 0; i < 1000; i++ ) {
			JunitTest t = new JunitTest();
			t.id = i;
			t.ctr = ctr;
			new Thread( t ).start();
		}

		// 这里是主应用程序,多长时间后一定结束。
		try {
			Thread.sleep( 1000 * 60 * 1 );
		} catch ( InterruptedException e ) {}
		log.debug( "----------------run test2 end---------------" );
	}

	@Override
	public void run() {
		int c = 0;
		while ( c < 100 ) {
			try {
				Thread.sleep( 30 );
				if( id % 3 == 0 ){
					ctr.moreThreadTestA();
				}else if( id % 3 == 1 ){
					ctr.moreThreadTestB();
				}else{
					ctr.moreThreadTestC();
				}
				right++;
			} catch ( ArithmeticException e ) {
				// 这里可以认为是正确，程序代码逻辑错误导致运算异常，比如 变量除以零
				faild_a++;
			} catch ( InterruptedException e ) {
				faild_i++;
			} catch ( RuntimeException e ) {
				faild_r++;
			} catch ( Exception e ) {} finally {
				c++;
			}
		}

		log.info( "Thread[" + id + "] 统计的总数 错误: 线程-" + faild_i + "/事件-" + faild_r + "/逻辑-" + faild_a + "/成功-"
						+ right );
	}
}
