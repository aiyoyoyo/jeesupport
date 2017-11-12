package com.jees.test;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jees.core.database.support.AbsDao;
import com.jees.test.entity.TabA;
import com.jees.test.entity.TabB;

/**
 * 
 * 模拟业务逻辑测试数据库，所有函数不要使用try-catch，一定要抛给上层，否则事务不生效。
 * @Transactional 指定给类时，所有方法都含有事务特性，MySQL数据类型需要是InnoDB
 * 建议根据需要，指定给包含事务特性的方法。
 * @author aiyoyoyo
 * 
 */
@Service
public class DatabaseService {

	@Autowired
	private AbsDao	dao;

	@Transactional
	public void testInsertA() {
		TabA a = new TabA();

		dao.insert( "a" , a );
	}

	@Transactional
	public void testInsertB() {
		TabB b = new TabB();
		dao.insert( "b" , b );
	}

	public void testInsertAFaild() {
		Session sess = dao.getSession( "a" );

		TabA a = new TabA();

		sess.save( a );

		sess.flush();
		
		sess.close();
		
		@SuppressWarnings ( "unused" )
		int n = 1 / 0;
	}

	public void testInsertBFaild() {
		Session sess = dao.getSession( "b" );

		TabB a = new TabB();

		sess.save( a );

		sess.flush();
		
		sess.close();
		
		@SuppressWarnings ( "unused" )
		int n = 1 / 0;
	}

	public void testSelectA() {
		System.out.println( "-----testSelectA:" + dao.select( "a" , TabA.class ).get( 0 ) );
	}

	public void testSelectB() {
		dao.select( "b" , TabB.class ).get( 0 );
	}

	public void testSelectAB() {
		dao.select( "a" , TabA.class ).get( 0 );
		dao.select( "b" , TabB.class ).get( 0 );
	}

	public void testDeleteA() {
		Session sess = dao.getSession( "a" );

		Criteria cri = sess.createCriteria( TabA.class );

		cri.setMaxResults( 1 );

		@SuppressWarnings ( "rawtypes" )
		List list = cri.list();

		sess.delete( list.get( 0 ) );
		
		sess.close();
	}
	
	public void testDeleteA2() {
		TabA a = (TabA) dao.selectById( "a" , TabA.class , 638 );
		
		dao.delete( "a" , a );
		
		@SuppressWarnings ( "unused" )
		int b = 1 / 0;
	}

	public void testDeleteB() {
		Session sess = dao.getSession( "b" );

		Criteria cri = sess.createCriteria( TabB.class );

		cri.setMaxResults( 1 );

		@SuppressWarnings ( "rawtypes" )
		List list = cri.list();

		sess.delete( list.get( 0 ) );

		sess.flush();
		
		sess.close();
	}

	public void testDeleteAFaild() {
		Session sess = dao.getSession( "a" );

		Criteria cri = sess.createCriteria( TabA.class );

		cri.setMaxResults( 1 );

		@SuppressWarnings ( "rawtypes" )
		List list = cri.list();

		sess.delete( list.get( 0 ) );

		sess.flush();
		
		sess.close();
		
		@SuppressWarnings ( "unused" )
		int n = 1 / 0;
	}

	public void testDeleteBFaild() {
		Session sess = dao.getSession( "b" );

		Criteria cri = sess.createCriteria( TabB.class );

		cri.setMaxResults( 1 );

		@SuppressWarnings ( "rawtypes" )
		List list = cri.list();

		sess.delete( list.get( 0 ) );

		sess.flush();
		
		sess.close();
		
		@SuppressWarnings ( "unused" )
		int n = 1 / 0;
	}

	public void testUpdateA() {
		Session sess = dao.getSession( "a" );

		Criteria cri = sess.createCriteria( TabA.class );

		cri.setMaxResults( 1 );

		@SuppressWarnings ( "rawtypes" )
		List list = cri.list();

		TabA a = (TabA) list.get( 0 );

		sess.update( a );
		
		sess.flush();

		sess.close();
	}

	public void testUpdateB() {
		Session sess = dao.getSession( "b" );

		Criteria cri = sess.createCriteria( TabB.class );

		cri.setMaxResults( 1 );

		@SuppressWarnings ( "rawtypes" )
		List list = cri.list();

		TabB b = (TabB) list.get( 0 );

		sess.update( b );

		sess.flush();
		
		sess.close();
	}

	public void testUpdateAFaild() {
		Session sess = dao.getSession( "a" );

		Criteria cri = sess.createCriteria( TabA.class );

		cri.setMaxResults( 1 );

		@SuppressWarnings ( "rawtypes" )
		List list = cri.list();

		TabA a = (TabA) list.get( 0 );

		sess.update( a );

		sess.flush();

		sess.close();
		
		@SuppressWarnings ( "unused" )
		int n = 1 / 0;
	}

	public void testUpdateBFaild() {
		Session sess = dao.getSession( "b" );

		Criteria cri = sess.createCriteria( TabB.class );

		cri.setMaxResults( 1 );

		@SuppressWarnings ( "rawtypes" )
		List list = cri.list();

		TabB b = (TabB) list.get( 0 );
		
		sess.update( b );

		sess.flush();

		sess.close();
		
		@SuppressWarnings ( "unused" )
		int n = 1 / 0;
	}

	public void testInsertAB() {
		testInsertA();
		testInsertB();
	}

	public void testInsertABFaild() {
		testInsertA();
		testInsertBFaild();
	}

	public void testDeleteAB() {
		testDeleteA();
		testDeleteB();
	}

	public void testDeleteABFaild() {
		testDeleteA();
		testDeleteBFaild();
	}

	public void testUpdateAB() {
		testUpdateA();
		testUpdateB();
	}

	public void testUpdateABFaild() {
		testUpdateA();
		testUpdateBFaild();
	}
	
	@Transactional
	public void testInsertABFaildB(){
		Session sessB = dao.getSession( "b" );
		TabB b = new TabB();

		dao.insert( "b" , b );
		
		TabB b2 = new TabB();
		sessB.save( b2 );
		
		TabA a = new TabA();
		dao.insert( "a" , a );
		
		testInsertA();
		@SuppressWarnings ( "unused" )
		int n = 1 / 0;
	}
}
