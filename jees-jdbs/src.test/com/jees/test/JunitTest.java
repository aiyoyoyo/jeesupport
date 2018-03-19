package com.jees.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;

/**
 * 测试方法,执行日志默认在工程目录/logs下
 * 
 * @author aiyoyoyo
 * 
 */
@RunWith( SpringJUnit4ClassRunner.class )
@SpringBootTest
@ComponentScan("com.jees")
public class JunitTest implements Runnable {
	static Logger			logger	= LogManager.getLogger( JunitTest.class );

	public long				id;
	public int				right;
	public int				faild_a;
	public int				faild_i;
	public int				faild_r;

	@Autowired
	public TestController	ctr;

	@Test
	public void SimpleTest(){
		ctr.simpleTest();
	}
	// @Test
	public void ExTest() {
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
				logger.error( "--执行的实体对象不符合操作规则，例如ID发生了变化后执行了更新操作。" );
			else if ( str.indexOf( "The given object has a null identifier" ) != - 1 )
				logger.error( "--执行的实体对象不符合操作规则，例如更新或删除为Null的对象。" );
			else if ( str.indexOf( "Could not obtain transaction-synchronized Session for current thread" ) != - 1 )
				logger.error( "--业务方法没有显式声明事务注解@Transactional。" );
			else if ( str.indexOf( "没有找到数据库" ) != - 1 ) logger.error( "--没有配置的数据库连接池。" );
			else logger.error( "--其他错误，待分类说明：" , e );
		}
		logger.info( "----------------run test1 end---------------" );
	}

	/**
	 * 并发测试。1000个线程各100次访问。
	 */
//	 @Test
	public void test2() {
		logger.debug( "----------------run test2 start---------------" );
		for ( int i = 0; i < 30; i++ ) {
			JunitTest t = new JunitTest();
			t.id = i;
			t.ctr = ctr;
			new Thread( t ).start();
		}

		// 这里是主应用程序,多长时间后一定结束。
		try {
			Thread.sleep( 1000 * 60 * 1 );
		} catch ( InterruptedException e ) {}
		logger.debug( "----------------run test2 end---------------" );
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

		logger.info( "Thread[" + id + "] 统计的总数 错误: 线程-" + faild_i + "/事件-" + faild_r + "/逻辑-" + faild_a + "/成功-"
						+ right );
	}
}
