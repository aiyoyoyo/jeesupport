package com.jees.test;

import com.jees.test.entity.TabA;
import com.jees.test.entity.TabB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class TestController {

	static final String	DB_A	= "testa";
	static final String	DB_B	= "testb";
	static final String	DB_C	= "c";

	@Autowired
	ExtendDao			ex;

	public void insertA() {
		TabA a = new TabA();
		a.setStr( System.currentTimeMillis() + "" );

		ex.insert( DB_A , a );
		ex.commit();
	}

	@Transactional
	public void insertATransSucc() {
		insertA();
//		@SuppressWarnings( "unused" )
//		int n = 1 / 0;
	}

	public void insertATransFail() {
		insertA();
		@SuppressWarnings( "unused" )
		int n = 1 / 0;
	}

	public void updateA() {
//		TabA a = ex.select( DB_A , TabA.class ).get( 0 );
//
//		a.setStr( a.getStr() + " change" );
//
//		ex.update( DB_A , a );
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
//		TabA a = ex.select( DB_A , TabA.class ).get( 0 );
//
//		ex.delete( DB_A , a );
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

	@Transactional
	public void insertAB() {
		TabA a = new TabA();
		a.setStr( System.currentTimeMillis() + "" );

//		ex.insert( DB_A , a );

		TabB b = new TabB();
		b.setNum( new Random().nextInt() );

//		ex.insert( DB_B , b );
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
	public void insertAList(){
		List<TabA> list =new ArrayList<>();
		
		for( int i = 0; i < 5; i++ ){
			list.add( new TabA() );
		}
		
		ex.insertAll( DB_A , list );
		
		@SuppressWarnings( "unused" )
		int n = 1/ 0;
	}
	
//	@Transactional
	public void selectA(){
		System.out.println( "--SELECT ID:" + ex.selectById( DB_A , TabA.class , 1 ) );
	}
	
	@Transactional
	public void otherTest() {
		ex.insert( DB_A , new TabA() );
		System.out.println( "--SELECT ID:" + ex.selectById( DB_A , TabA.class , 1 ) );
//		System.out.println( "--EXECUTE HQL:"
//				+ ex.executeByHQL( DB_A , "UPDATE TabA SET str = ? WHERE id = ?" , new Object [] { "change" , 1 } ) );
//		System.out.println( "--EXECUTE SQL:"
//				+ ex.executeBySQL( DB_A , "UPDATE a SET str = concat( str,  ? ) WHERE id = ?" , new Object [] { " change2" , 1 } ) );
		System.out.println( "--SELECT COUNT:" + ex.selectCount( DB_A , TabA.class ) );
//		System.out.println(
//				"--SELECT COUNT SQL:" + ex.selectCount( DB_A , "SELECT count( * ) FROM TabA WHERE str like 'change%'" , null ) );
		@SuppressWarnings( "unused" )
		int n = 1 / 0;
	}
	@Transactional
	public void moreThreadTestA(){
		insertA();
	}
	@Transactional
	public void moreThreadTestB(){
		updateA();
		TabB b = new TabB();
		b.setNum( new Random().nextInt() );
//		ex.insert( DB_B , b );
	}
	@Transactional
	public void moreThreadTestC(){
		deleteA();
	}

	@Transactional
	public void simpleTest(){
		TabA a = new TabA();
		ex.select( "testa", TabA.class );
//		TabB b = new TabB();
//
//		ex.insert( DB_B, new TabB() );
//
//		a.setId( 1 );
//		ex.insert( DB_A, a );
//		a.setStr("aaa1");
//		ex.update( DB_A, a );
//
////		ex.delete( DB_A, a );
//		ex.commit();

//		int n = 1 / 0;
	}

	@Transactional
	public void absTest(){

		TabA a = new TabA();
		ex.insert( DB_A, a );
		System.out.println( "插入1:---------------" );
		List<TabA> list = new ArrayList<>();
		list.add( new TabA() );
		list.add( new TabA() );
		ex.insertAll( DB_A, list );
		System.out.println( "插入2:---------------" );
		ex.commit();

		list.add( new TabA() );
		list.add( new TabA() );
		list.add( new TabA() );
		list.add( new TabA() );
		list.add( new TabA() );
		list.add( new TabA() );
		list.add( new TabA() );
		list.add( new TabA() );
		ex.insertAll( DB_A, list , 10 );
		System.out.println( "插入2:---------------" );
		String hql = "FROM TabA WHERE id = :ID";
		String sql = "SELECT * FROM a WHERE id = :ID";
		list =
//				ex.select( DB_A, TabA.class );
//				ex.select( DB_A, TabA.class , 5 );
//				ex.select( DB_A, TabA.class , 13, 2 );
//				ex.selectByHQL( DB_A, hql , new String[]{ "ID" } , new Object[]{ 1 }, TabA.class );
				ex.selectBySQL( DB_A, sql , new String[]{ "ID" } , new Object[]{ 1 }, TabA.class );
		System.out.println( "查询1:" + list.size() );

		TabA a0 =
				ex.selectById( DB_A, TabA.class ,  1 );
		System.out.println( "查询2:" + ( a0 == null ? null : a0.getId() ) );

		long c =
				ex.selectCount( DB_A, TabA.class );
//				ex. selectCountByHQL( DB_A, hql , null, null );
//				ex. selectCountBySQL( DB_A, sql , null, null );
		System.out.println( "查询3:" + c );

		if( a0 != null )
			a0.setStr( "000" );
		ex.update( DB_A, a0 );
		System.out.println( "更新1:" + a0.getStr() );

		ex.update( DB_A, list );
		System.out.println( "更新2:" + list.size() );
//		ex.updateAll( DB_A, list , 5 );

		System.out.println( "--更新3:"
				+ ex.executeByHQL( DB_A , "UPDATE TabA SET str = :STR WHERE id = :ID" , new String[]{ "STR", "ID"}, new Object [] { "change" , 1 } ) );
		System.out.println( "--更新4:"
				+ ex.executeBySQL( DB_A , "UPDATE a SET str = concat( str,  :STR ) WHERE id = :ID" ,new String[]{ "STR", "ID"}, new Object [] { " change2" , 1 } ) );

//		ex.delete( DB_A, a0 );
//		System.out.println( "删除1:" + a0 );

//		ex.delete( DB_A, list );
//		System.out.println( "不能和删除1一起执行，删除2:" + list.size() );
//		ex.commit();

		ex.deleteAll( DB_A, list , 5 );
		System.out.println( "不能和删除2一起执行，删除3:" + list.size() );
	}
}
