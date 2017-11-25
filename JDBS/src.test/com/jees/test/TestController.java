package com.jees.test;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.jees.test.entity.TabA;
import com.jees.test.entity.TabB;

@Controller
public class TestController {

	static final String	DB_A	= "a";
	static final String	DB_B	= "b";
	static final String	DB_C	= "c";

	@Autowired
	ExtendDao			ex;

	public void insertA() {
		TabA a = new TabA();
		a.setStr( System.currentTimeMillis() + "" );

		ex.beging( DB_A );

		ex.insert( DB_A , a );

		ex.finish( ExtendDao.FINISH_CLOSE );
	}

	@Transactional
	public void insertATransSucc() {
		insertA();
		@SuppressWarnings( "unused" )
		int n = 1 / 0;
	}

	public void insertATransFail() {
		insertA();
		@SuppressWarnings( "unused" )
		int n = 1 / 0;
	}

	public void updateA() {
		ex.beging( DB_A );

		TabA a = ex.select( DB_A , TabA.class ).get( 0 );

		a.setStr( a.getStr() + " change" );

		ex.update( DB_A , a );

		ex.finish( ExtendDao.FINISH_CLOSE );
	}

	@Transactional
	public void updateATransSucc() {
		updateA();
		@SuppressWarnings( "unused" )
		int n = 1 / 0;
	}

	public void updateATransFail() {
		updateA();
		@SuppressWarnings( "unused" )
		int n = 1 / 0;
	}

	public void deleteA() {
		ex.beging( DB_A );

		TabA a = ex.select( DB_A , TabA.class ).get( 0 );

		ex.delete( DB_A , a );

		ex.finish( ExtendDao.FINISH_CLOSE );
	}

	@Transactional
	public void deleteATransSucc() {
		deleteA();
		@SuppressWarnings( "unused" )
		int n = 1 / 0;
	}

	public void deleteATransFail() {
		deleteA();
		@SuppressWarnings( "unused" )
		int n = 1 / 0;
	}

	public void insertAB() {
		ex.beging( DB_A , DB_B );

		TabA a = new TabA();
		a.setStr( System.currentTimeMillis() + "" );

		ex.insert( DB_A , a );

		TabB b = new TabB();
		b.setNum( new Random().nextInt() );

		ex.insert( DB_B , b );

		ex.finish( ExtendDao.FINISH_CLOSE );
	}

	@Transactional
	public void insertABTransSucc() {
		insertAB();
		@SuppressWarnings( "unused" )
		int n = 1 / 0;
	}

	public void insertABTransFail() {
		insertAB();
		@SuppressWarnings( "unused" )
		int n = 1 / 0;
	}

	@Transactional
	public void otherTest() {
		ex.beging( DB_A , DB_B );

		System.out.println( "--SELECT ID:" + ex.selectById( DB_A , TabA.class , 1 ) );
		System.out.println( "--EXECUTE HQL:"
				+ ex.executeByHQL( DB_A , "UPDATE TabA SET str = ? WHERE id = ?" , new Object [] { "change" , 1 } ) );
		System.out.println( "--EXECUTE SQL:"
				+ ex.executeBySQL( DB_A , "UPDATE a SET str = concat( str,  ? ) WHERE id = ?" , new Object [] { " change2" , 1 } ) );
		System.out.println( "--SELECT COUNT:" + ex.selectCount( DB_A , TabA.class ) );
		System.out.println(
				"--SELECT COUNT SQL:" + ex.selectCount( DB_A , "SELECT count( * ) FROM TabA WHERE str like 'change%'" , null ) );
		
		ex.finish( ExtendDao.FINISH_CLOSE );
	}
}
