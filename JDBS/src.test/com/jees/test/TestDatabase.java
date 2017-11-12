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
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( locations = { "classpath:jees-core-database.xml" } )
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
	@Test
	public void test1() {
		logger.info( "----------------run test1 start---------------" );
		try {
			this.service.testInsertA();
//			this.service.testInsertAB();
			this.service.testInsertABFaildB();
//			this.service.testInsertAB();
//			this.service.testInsertABFaild();
//			this.service.testUpdateAB();
//			this.service.testUpdateABFaild();
//			this.service.testSelectAB();
//			this.service.testDeleteAB();
//			this.service.testDeleteA2();
//			this.service.testDeleteABFaild();
		} catch ( RuntimeException e ) {
			e.printStackTrace();
			logger.error( "----------------run test1 catch runtime error---------------" );
		} catch ( Exception e ) {
			e.printStackTrace();
			logger.error( "----------------run test1 catch error---------------" );
		}
		logger.info( "----------------run test1 end---------------" );
	}

	/**
	 * 并发测试。1000个线程各100次访问。
	 */
//	@Test
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
			Thread.sleep( 30000 );
		} catch ( InterruptedException e ) {
		}
		logger.debug( "----------------run test2 end---------------" );
	}

	public void testRun() {
		int run = new java.util.Random().nextInt( 20 );

		switch ( run ) {
		case 0:
			service.testInsertA();
			break;
		case 1:
			service.testInsertB();
			break;
		case 2:
			service.testInsertAB();
			break;
		case 3:
			service.testInsertAFaild();
			break;
		case 4:
			service.testInsertBFaild();
			break;
		case 5:
			service.testInsertABFaild();
			break;
		case 6:
			service.testDeleteA();
			break;
		case 7:
			service.testDeleteB();
			break;
		case 8:
			service.testDeleteAB();
			break;
		case 9:
			service.testUpdateA();
			break;
		case 10:
			service.testUpdateB();
			break;
		case 11:
			service.testUpdateAB();
			break;
		case 12:
			service.testSelectA();
			break;
		case 13:
			service.testSelectB();
			break;
		case 14:
			service.testSelectAB();
			break;
		case 15:
			service.testDeleteAFaild();
			break;
		case 16:
			service.testDeleteBFaild();
			break;
		case 17:
			service.testDeleteABFaild();
			break;
		case 18:
			service.testUpdateAFaild();
			break;
		case 19:
			service.testUpdateBFaild();
			break;
		case 20:
			service.testUpdateABFaild();
			break;
		}
	}

	@Override
	public void run() {
		int c = 0;
		while ( c < 100 ) {
			try {
				Thread.sleep( 500 );

				testRun();

				right++;
			} catch ( ArithmeticException e ) {
				// 这里可以认为是正确，程序代码逻辑错误导致运算异常，比如 变量除以零
				faild_a++;
			} catch ( InterruptedException e ) {
				faild_i++;
			} catch ( RuntimeException e ) {
				e.printStackTrace();
				faild_r++;
			} catch ( Exception e ) {
				e.printStackTrace();
			} finally {
				logger.debug( "Thread[" + id + "] run test F / S : " + faild_i + ":" + faild_r + "/" + faild_a + ":" + right );
				c++;
			}
		}

		logger.info( "Thread[" + id + "] run test F / S : " + faild_i + ":" + faild_r + "/" + faild_a + ":" + right );
	}
}
