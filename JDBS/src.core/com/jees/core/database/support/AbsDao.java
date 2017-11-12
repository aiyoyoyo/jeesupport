package com.jees.core.database.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.springframework.stereotype.Repository;

/**
 * 
 * 提供基本数据库单次操作函数，以及各个数据库连接org.hibernate.Session对象，业务对象类通过加入注解：org.
 * springframework.transaction.annotation.Transactional 来管理事务。
 * 
 * @author aiyoyoyo
 * 
 */
@Repository
public class AbsDao {
	public static final int						DEFAULT_LIMIT	= 1000;
	public static final int						DEFAULT_FIRST	= 0;
	/**
	 * 从配置文件中获取数据库链接对象。
	 */
	@Resource ( name = "sessionFactoryMap" )
	private HashMap< String , SessionFactory >	sessionFactoryMap;

	/**
	 * 获取某个数据库的Session <br/>
	 * 获取后需要调用{@code close()} <br/>
	 * 
	 * @param _dbName
	 * @return null | Session
	 */
	public Session getSession( String _dbName ) throws NullPointerException {
		if ( sessionFactoryMap.containsKey( _dbName ) ) { 
			SessionFactory sessFactory = sessionFactoryMap.get( _dbName );
			return sessFactory.openSession(); 
		}

		throw new NullPointerException( "SessionFactory {" + _dbName + "} was not found." );
	}

	/**
	 * 插入任意映射对象，适合单对象单次操作。
	 * 
	 * @param _dbName
	 * @param _entitye
	 */
	public void insert( String _dbName , Object _entity ) {
		Session sess = null;
		try {
			sess = getSession( _dbName );
			sess.save( _entity );
			sess.flush();
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( sess != null ) sess.close();
		}
	}

	/**
	 * 删除任意映射对象，适合单对象单次操作。
	 * 
	 * @param _dbName
	 * @param _entity
	 */
	public void delete( String _dbName , Object _entity ) {
		Session sess = null;
		try {
			sess = getSession( _dbName );
			sess.delete( _entity );
			sess.flush();
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( sess != null ) sess.close();
		}
	}

	/**
	 * 更新任意映射对象，适合单对象单次操作。
	 * 
	 * @param _dbName
	 * @param _entity
	 */
	public void update( String _dbName , Object _entity ) {
		Session sess = null;
		try {
			sess = getSession( _dbName );
			sess.update( _entity );
			sess.flush();
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( sess != null ) sess.close();
		}
	}

	/**
	 * 查找任意映射对象，适合单对象单次操作。 <br/>
	 * 
	 * 默认最大返回数： 1000
	 * 
	 * @param _dbName
	 * @param _cls
	 * @return java.util.List
	 */
	public < T > List< T > select( String _dbName , Class< T > _cls ) {
		return select( _dbName , _cls , DEFAULT_LIMIT );
	}

	/**
	 * 
	 * 查找任意映射对象，设置最大行数，适合单对象单次操作。
	 * 
	 * @param _dbName
	 * @param _cls
	 * @param _limit
	 * @return java.util.List
	 */
	public < T > List< T > select( String _dbName , Class< T > _cls , Integer _limit ) {
		return select( _dbName , _cls ,DEFAULT_FIRST, DEFAULT_LIMIT) ;
	}
	
	/**
	 * 
	 * 查找任意映射对象，设置起始位置和最大行数，适合单对象单次操作。
	 * 
	 * @param _dbName
	 * @param _cls
	 * @param _first
	 * @param _limit
	 * @return java.util.List
	 */
	@SuppressWarnings ( "unchecked" )
	public < T > List< T > select( String _dbName , Class< T > _cls ,Integer _first, Integer _limit ) {
		Session sess = null;
		try {
			sess = getSession( _dbName );
			return sess.createCriteria( _cls ).setFirstResult( _first ).setMaxResults( _limit ).list();
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( sess != null ) sess.close();
		}
	}
	/**
	 * 根据查找任意映射对象，适合单对象单次操作。
	 * 
	 * @param _dbName
	 * @param _cls
	 * @return null | Object
	 */
	@SuppressWarnings ( "unchecked" )
	public < T > T selectById( String _dbName , Class< T > _cls , Serializable _id ) {
		Session sess = null;
		try {
			sess = getSession( _dbName );
			return ( T ) sess.get( _cls , _id );
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( sess != null ) sess.close();
		}
	}

	/**
	 * 根据映射类的对象查找结果, 当映射类中设置以下内容时，查询无效：<br/>
	 * 主键 或 关联类 或 为空
	 * 
	 * @param _dbName
	 * @param _obj
	 * @return java.util.List
	 */
	@SuppressWarnings ( "unchecked" )
	public < T > List< T > selectByExample( String _dbName , Object _obj ) {
		Session sess = null;
		try {
			sess = getSession( _dbName );
			return sess.createCriteria( _obj.getClass() ).add( Example.create( _obj ) ).list();
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( sess != null ) sess.close();
		}
	}

	/**
	 * 执行HQL查询语句, 默认最大返回数： 1000
	 * 
	 * @param _dbName
	 * @param _hql
	 * @param _cls
	 * @param _param
	 * @return java.util.List
	 */
	public < T > List< T > selectByHQL( String _dbName , String _hql, Object[] _param, Class< T > _cls ) {
		return selectByHQL( _dbName, _hql, DEFAULT_FIRST, DEFAULT_LIMIT, _param, _cls );
	}
	
	/**
	 * 执行HQL查询语句, 默认最大返回数： 1000
	 * 
	 * @param _dbName
	 * @param _hql
	 * @param _first
	 * @param _limit
	 * @param _param
	 * @return java.util.List
	 */
	@SuppressWarnings ( "unchecked" )
	public < T > List< T > selectByHQL( String _dbName , String _hql, int _first, int _limit, Object[] _param, Class< T > _cls ) {
		Session sess = null;
		try {
			sess = getSession( _dbName );
			Query query = sess.createQuery( _hql );
			query.setFirstResult( _first );
			query.setMaxResults( _limit );
			int i = 0;
			for( Object o : _param )
				query.setParameter( i++ , o );
			return query.list();
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( sess != null ) sess.close();
		}
	}
	
	/**
	 * 执行SQL查询语句, 默认最大返回数： 1000
	 * 
	 * @param _dbName
	 * @param _sql
	 * @param _first
	 * @param _limit
	 * @param _param
	 * @return java.util.List
	 */
	@SuppressWarnings ( "unchecked" )
	public < T > List< T > selectBySQL( String _dbName , String _sql, Object[] _param, Class< T > _cls ) {
		Session sess = null;
		try {
			sess = getSession( _dbName );
			Query query = sess.createSQLQuery( _sql ).addEntity( _cls );
			int i = 0;
			for( Object o : _param )
				query.setParameter( i++ , o );
			return query.list();
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( sess != null ) sess.close();
		}
	}
	/**
	 * 执行HQL更新语句，并返回影响行数。
	 * 
	 * @param _dbName
	 * @param _hql
	 * @return int
	 */
	public int executeByHQL( String _dbName , String _hql ) {
		Session sess = null;
		try {
			sess = getSession( _dbName );
			return sess.createQuery( _hql ).executeUpdate();
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( sess != null ) sess.close();
		}
	}
	/**
	 * 查询某个表的记录数
	 * 
	 * @param _dbName
	 * @param _hql
	 * @return int
	 */
	public int selectCount( String _dbName, @SuppressWarnings ( "rawtypes" ) Class _cls ){
		Session sess = null;
		String hql = "SELECT COUNT(*) FROM ";
		try {
			sess = getSession( _dbName );
			Query query = sess.createQuery( hql + _cls.getName() );
			
			return ((Long) query.iterate().next()).intValue();
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( sess != null ) sess.close();
		}
	}
	/**
	 * 查询某个表的记录数
	 * 
	 * @param _dbName
	 * @param _hql
	 * @param _param
	 * @return int
	 */
	public int selectCount( String _dbName, String _hql, Object[] _param ){
		Session sess = null;
		try {
			sess = getSession( _dbName );
			Query query = sess.createQuery( _hql );
			int i = 0;
			for( Object o : _param )
				query.setParameter( i++ , o );
			return ((Long) query.iterate().next()).intValue();
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( sess != null ) sess.close();
		}
	}
}
