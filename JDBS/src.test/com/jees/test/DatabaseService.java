package com.jees.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jees.core.database.support.ISupportDao;
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
	
	static final String	DB_A = "a";
	static final String	DB_B = "b";
	static final String	DB_C = "c";
	
	@Autowired
	private ISupportDao	dao;
	
	/**
	 * 单次插入数据A, 方法需要显式声明事务标记
	 */
	@Transactional
	public void testInsertA0(){
		TabA a = new TabA();
		dao.insert( DB_A , a );
	}
	/**
	 * 插入数据B
	 */
	@Transactional
	public void testInsertB0(){
		TabB b = new TabB();
		dao.insert( DB_B , b );
	}
	/**
	 * 插入数据失败
	 */
	@Transactional( readOnly = true )
	public void testInsertB1(){
		TabB b = new TabB();
		dao.insert( DB_B , b );
	}
	
	public void testInsertB2(){
		TabB b = new TabB();
		dao.insert( DB_B , b );
	}
	/**
	 * 未注入的数据库
	 */
	public void testInsertC0(){
		TabA a = new TabA();
		dao.insert( DB_C , a );
	}
	/**
	 * 多次的数据处理
	 */
	@Transactional
	public void testInsertAA0() {
		TabA a0 = new TabA();
		dao.insert( DB_A , a0 );
		
		TabA a1 = new TabA();
		dao.insert( DB_A , a1 );
	}
	
	/**
	 * 多次嵌套其他业务方式的数据处理
	 */
	@Transactional
	public void testInsertAA1() {
		testInsertA0();
		testInsertAA0();
	}
	
	/**
	 * 演示发生错误时的情况
	 * 多次嵌套其他业务方式的数据处理
	 */
	@Transactional
	public void testInsertAA2() {
		testInsertAA1();
		
		testInsertC0();
	}
	
	/**
	 * 演示跨数据库
	 * 多次嵌套其他业务方式的数据处理
	 */
	@Transactional
	public void testInsertAB0() {
		testInsertA0();
		testInsertB0();
	}
	/**
	 * 演示跨数据库，发生错误
	 * 多次嵌套其他业务方式的数据处理
	 */
	@Transactional( readOnly = true )
	public void testInsertAB1() {
		testInsertA0();
		testInsertB0();
		
		testInsertC0();
	}
	/**
	 * 演示跨数据库，发生错误
	 * 多次嵌套其他业务方式的数据处理
	 */
	public void testInsertAB2() {
		testInsertA0();
		testInsertB0();
		
		testInsertC0();
	}
	/**
	 * 更新数据
	 */
	@Transactional
	public void testUpdateA0(){
		TabA a = new TabA();
		dao.insert( DB_A , a );
		dao.update( DB_A , a );
	}
	/**
	 * 更新数据发生错误。
	 */
	@Transactional
	public void testUpdateA1(){
		TabA a = new TabA();
		dao.update( DB_A , a );
	}

	public void testUpdateA2(){
		TabA a = dao.selectById( DB_A , TabA.class , 1 );
		a.setStr( "" + a.getId() );
		dao.update( DB_A , a );
	}
	
	/**
	 * 更新失败，事务生效
	 */
	@Transactional
	public void testUpdateA3(){
		testUpdateA2();
		testUpdateA1();
	}
	/**
	 * 更新成功，没有事务
	 */
	public void testUpdateA4(){
		testUpdateA2();
		testUpdateA1();
	}
	/**
	 * 多层嵌套，以最上层为准
	 */
	@Transactional
	public void testUpdateA5(){
		testUpdateA4();
	}
	/**
	 * 删除数据
	 */
	@Transactional
	public void testDeleteA0(){
		TabA a = new TabA(); 
		dao.insert( DB_A , a );
		dao.delete( DB_A , a );
	}
	
	/**
	 * 删除数据发生错误
	 */
	@Transactional
	public void testDeleteA1(){
		TabA a = new TabA();
		dao.delete( DB_A , a );
	}
	
	/**
	 * 查询演示
	 */
	public void testSelectA0(){
		dao.select( DB_A , TabA.class );
	}
}
