package com.jees.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 测试方法,执行日志默认在工程目录/logs下
 * 
 * @author aiyoyoyo
 * 
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "classpath:jees-core-database.xml" } )
public class TestDatabase implements Runnable {
	static Logger			logger	= LogManager.getLogger( TestDatabase.class );

	public long				id;
	public int				right;
	public int				faild_a;
	public int				faild_i;
	public int				faild_r;

	@Autowired
	public DatabaseService	service;

	/**
	 * 单项测试
	 */
//	@Test
	public void test1() {
		logger.info( "----------------run test1 start---------------" );
		try {
			// service.testInsertA0();
			// service.testInsertB0();
			// service.testInsertB1();
			// service.testInsertB2();
			// service.testInsertC0();
			// service.testInsertAA0();
			// service.testInsertAA1();
			// service.testInsertAA2();
			// service.testInsertAB0();
			// service.testInsertAB1();
			// service.testInsertAB2();
			// service.testUpdateA0();
			// service.testUpdateA1();
			// service.testUpdateA2();
			// service.testUpdateA3();
			// service.testUpdateA4();
			// service.testUpdateA5();
			// service.testDeleteA0();
			// service.testDeleteA1();
			// service.testSelectA0();
		} catch ( RuntimeException e ) {
			String str = e.toString();

			if ( str.indexOf( "identifier of an instance of" ) != -1 )
				logger.error( "--执行的实体对象不符合操作规则，例如ID发生了变化后执行了更新操作。" );
			else if ( str.indexOf( "The given object has a null identifier" ) != -1 )
				logger.error( "--执行的实体对象不符合操作规则，例如更新或删除为Null的对象。" );
			else if ( str.indexOf( "Could not obtain transaction-synchronized Session for current thread" ) != -1 )
				logger.error( "--业务方法没有显式声明事务注解@Transactional。" );
			else logger.error( "--其他运行错误，待分类说明：" , e );
		} catch ( Exception e ) {
			String str = e.toString();
			if ( str.indexOf( "没有找到数据库" ) != -1 ) logger.error( "--没有配置的数据库连接池。" );
			else logger.error( "--其他错误，待分类说明：" , e );
		}
		logger.info( "----------------run test1 end---------------" );
	}

	/**
	 * 并发测试。1000个线程各100次访问。
	 */
	 @Test
	public void test2() {
		logger.debug( "----------------run test2 start---------------" );
		for ( int i = 0; i < 1000; i++ ) {
			TestDatabase t = new TestDatabase();
			t.id = i;
			t.service = service;
			new Thread( t ).start();
		}

		// 这里是主应用程序,多长时间后一定结束。
		try {
			Thread.sleep( 1000 * 60 * 1 );
		} catch ( InterruptedException e ) {}
		logger.debug( "----------------run test2 end---------------" );
	}

	public void testRun() {
		int run = new java.util.Random().nextInt( 15 );

		switch ( run ) {
			case 0:
				service.testInsertA0();
				break;
			case 1:
				service.testInsertB0();
				break;
			case 2:
				service.testInsertB1();
				break;
			case 3:
				service.testInsertC0();
				break;
			case 4:
				service.testInsertAA0();
				break;
			case 5:
				service.testInsertAA1();
				break;
			case 6:
				service.testInsertAA2();
				break;
			case 7:
				service.testInsertAB0();
				break;
			case 8:
				service.testInsertAB1();
				break;
			case 9:
				service.testInsertAB2();
				break;
			case 10:
				service.testUpdateA0();
				break;
			case 11:
				service.testUpdateA1();
				break;
			case 12:
				service.testUpdateA2();
				break;
			case 13:
				service.testDeleteA0();
				break;
			case 14:
				service.testDeleteA1();
				break;
			case 15:
				service.testSelectA0();
				break;
		}
	}

	@Override
	public void run() {
		int c = 0;
		while ( c < 100 ) {
			try {
				Thread.sleep( 30 );

				testRun();

				right++;
			} catch ( ArithmeticException e ) {
				// 这里可以认为是正确，程序代码逻辑错误导致运算异常，比如 变量除以零
				faild_a++;
			} catch ( InterruptedException e ) {
				faild_i++;
			} catch ( RuntimeException e ) {
				faild_r++;
			} catch ( Exception e ) {
			} finally {
				c++;
			}
		}

		logger.info( "Thread[" + id + "] 统计的总数 错误(线程/事件/逻辑) / 成功 : " + faild_i + ":" + faild_r + "/" + faild_a + ":" + right );
	}
}
