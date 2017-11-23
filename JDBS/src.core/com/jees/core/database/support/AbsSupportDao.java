package com.jees.core.database.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

/**
 * 
 * 提供基本数据库单次操作函数，以及各个数据库连接org.hibernate.Session对象，业务对象类通过加入注解：org.
 * springframework.transaction.annotation.Transactional 来管理事务。
 * 
 * @author aiyoyoyo
 * 
 */
public abstract class AbsSupportDao implements ISupportDao {
	/**
	 * 从配置文件中获取数据库链接对象。
	 */
	@Resource( name = DEFAULT_SFMAP )
	private HashMap< String , SessionFactory > sessionFactoryMap;

	/**
	 * 获取某个数据库的Session <br/>
	 * session的开启和关闭由Spring事务管理器代理
	 * 
	 * @param _db
	 * @return null | Session
	 */
	@Override
	public Session getSession( String _db ) {
		if ( sessionFactoryMap.containsKey( _db ) ) {
			SessionFactory sessFactory = sessionFactoryMap.get( _db );
			if ( sessFactory.isClosed() ) return sessFactory.openSession();
			else return sessFactory.getCurrentSession();
		}

		throw new NullPointerException( "没有找到数据库[" + _db + "]的对应Session容器，请检查配置文件中的[ " + DEFAULT_SFMAP + " ]是否正确。" );
	}

	@Override
	public Session openSession( String _db ) {
		if ( sessionFactoryMap.containsKey( _db ) ) {
			SessionFactory sessFactory = sessionFactoryMap.get( _db );
			return sessFactory.openSession();
		}

		throw new NullPointerException( "没有找到数据库[" + _db + "]的对应Session容器，请检查配置文件中的[ " + DEFAULT_SFMAP + " ]是否正确。" );
	}

	/**
	 * 清理缓存，等于真正提交插入、更新、删除
	 */
	@Override
	public void flushSession( Session _sess ) {
		_sess.flush();
		_sess.clear();
	}

	/**
	 * 插入任意对象
	 */
	@Override
	public void insert( String _db , Object _entity ) {
		Session ses = getSession( _db );
		ses.save( _entity );
		ses.flush();
	}

	/**
	 * 插入多个映射对象
	 */
	@Override
	public < T > void insertAll( String _db , List< T > _list ) {
		insertAll( _db , _list , DEFAULT_FLUSH );
	}

	/**
	 * 插入到一定数量时，会立即提交并清理缓存
	 * 
	 * @param _db
	 * @param _list
	 * @param _flush
	 */
	@Override
	public < T > void insertAll( String _db , List< T > _list , int _flush ) {
		Session sess = getSession( _db );

		int exnum = 0;
		int monum = _flush - 1;

		for ( T e : _list ) {
			sess.save( e );
			if ( ++exnum % _flush == monum ) flushSession( sess );
		}

		flushSession( sess );
	}

	/**
	 * 删除任意映射对象
	 * 
	 * @param _db
	 * @param _entity
	 */
	@Override
	public void delete( String _db , Object _entity ) {
		Session ses = getSession( _db );
		ses.delete( _entity );
		ses.flush();
	}

	/**
	 * 删除多个映射对象
	 */
	@Override
	public < T > void deleteAll( String _db , List< T > _list ) {
		deleteAll( _db , _list , DEFAULT_FLUSH );
	}

	/**
	 * 删除到一定数量时，会立即提交并清理缓存
	 * 
	 * @param _db
	 * @param _list
	 * @param _flush
	 */
	@Override
	public < T > void deleteAll( String _db , List< T > _list , int _flush ) {
		Session sess = getSession( _db );

		int exnum = 0;
		int monum = _flush - 1;

		for ( T e : _list ) {
			sess.delete( e );
			if ( ++exnum % _flush == monum ) flushSession( sess );
		}

		flushSession( sess );
	}

	/**
	 * 更新任意映射对象
	 * 
	 * @param _db
	 * @param _entity
	 */
	@Override
	public void update( String _db , Object _entity ) {
		Session ses = getSession( _db );
		ses.update( _entity );
		ses.flush();
	}

	/**
	 * 更新到一定数量时，会立即提交并清理缓存
	 * 
	 * @param _db
	 * @param _list
	 * @param _flush
	 */
	@Override
	public < T > void updateAll( String _db , List< T > _list ) {
		updateAll( _db , _list , DEFAULT_FLUSH );
	}

	/**
	 * 更新到一定数量时，会立即提交并清理缓存
	 * 
	 * @param _db
	 * @param _list
	 * @param _flush
	 */
	@Override
	public < T > void updateAll( String _db , List< T > _list , int _flush ) {
		Session sess = getSession( _db );

		int exnum = 0;
		int monum = _flush - 1;

		for ( T e : _list ) {
			sess.update( e );
			if ( ++exnum % _flush == monum ) flushSession( sess );
		}

		flushSession( sess );
	}

	/**
	 * 查找任意映射对象，适合单对象单次操作。 <br/>
	 * 
	 * 默认最大返回数： 1000
	 * 
	 * @param _db
	 * @param _cls
	 * @return java.util.List
	 */
	@Override
	public < T > List< T > select( String _db , Class< T > _cls ) {
		return select( _db , _cls , DEFAULT_LIMIT );
	}

	/**
	 * 
	 * 查找任意映射对象，设置最大行数，适合单对象单次操作。
	 * 
	 * @param _db
	 * @param _cls
	 * @param _limit
	 * @return java.util.List
	 */
	@Override
	public < T > List< T > select( String _db , Class< T > _cls , int _limit ) {
		return select( _db , _cls , DEFAULT_FIRST , DEFAULT_LIMIT );
	}

	/**
	 * 
	 * 查找任意映射对象，设置起始位置和最大行数，适合单对象单次操作。
	 * 
	 * @param _db
	 * @param _cls
	 * @param _first
	 * @param _limit
	 * @return java.util.List
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public < T > List< T > select( String _db , Class< T > _cls , int _first , int _limit ) {
		Session sess = null;
		try {
			sess = getSession( _db );
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
	 * @param _db
	 * @param _cls
	 * @return null | Object
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public < T > T selectById( String _db , Class< T > _cls , Serializable _id ) {
		Session sess = null;
		try {
			sess = getSession( _db );
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
	 * @param _db
	 * @param _obj
	 * @return java.util.List
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public < T > List< T > selectByExample( String _db , Object _obj ) {
		Session sess = null;
		try {
			sess = getSession( _db );
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
	 * @param _db
	 * @param _hql
	 * @param _cls
	 * @param _param
	 * @return java.util.List
	 */
	@Override
	public < T > List< T > selectByHQL( String _db , String _hql , Object[] _param , Class< T > _cls ) {
		return selectByHQL( _db , _hql , DEFAULT_FIRST , DEFAULT_LIMIT , _param , _cls );
	}

	/**
	 * 执行HQL查询语句, 默认最大返回数： 1000
	 * 
	 * @param _db
	 * @param _hql
	 * @param _first
	 * @param _limit
	 * @param _param
	 * @return java.util.List
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public < T > List< T > selectByHQL( String _db , String _hql , int _first , int _limit , Object[] _param ,
					Class< T > _cls ) {
		Session sess = getSession( _db );
		Query query = sess.createQuery( _hql );
		query.setFirstResult( _first );
		query.setMaxResults( _limit );
		int i = 0;
		for ( Object o : _param )
			query.setParameter( i++ , o );
		return query.list();
	}

	/**
	 * 执行SQL查询语句, 默认最大返回数： 1000
	 * 
	 * @param _db
	 * @param _sql
	 * @param _first
	 * @param _limit
	 * @param _param
	 * @return java.util.List
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public < T > List< T > selectBySQL( String _db , String _sql , Object[] _param , Class< T > _cls ) {
		Session sess = getSession( _db );
		Query query = sess.createSQLQuery( _sql ).addEntity( _cls );
		int i = 0;
		for ( Object o : _param )
			query.setParameter( i++ , o );
		return query.list();
	}

	/**
	 * 执行HQL更新语句，并返回影响行数。
	 * 
	 * @param _db
	 * @param _hql
	 * @return int
	 */
	@Override
	public int executeByHQL( String _db , String _hql ) {
		return getSession( _db ).createQuery( _hql ).executeUpdate();
	}

	/**
	 * 执行SQL更新语句，并返回影响行数。
	 * 
	 * @param _db
	 * @param _hql
	 * @return int
	 */
	@Override
	public int executeBySQL( String _db , String _sql ) {
		return getSession( _db ).createSQLQuery( _sql ).executeUpdate();
	}

	/**
	 * 查询某个表的记录数
	 * 
	 * @param _db
	 * @param _hql
	 * @return int
	 */
	@Override
	public < T > int selectCount( String _db , Class< T > _cls ) {
		Session sess = getSession( _db );
		String hql = "SELECT COUNT(*) FROM ";

		Query query = sess.createQuery( hql + _cls.getName() );

		return ( int ) query.iterate().next();
	}

	/**
	 * 查询某个表的记录数
	 * 
	 * @param _db
	 * @param _hql
	 * @param _param
	 * @return int
	 */
	@Override
	public int selectCount( String _db , String _hql , Object[] _param ) {
		Session sess = getSession( _db );
		Query query = sess.createQuery( _hql );
		int i = 0;
		for ( Object o : _param )
			query.setParameter( i++ , o );
		return ( int ) query.iterate().next();
	}

	/**
	 * 插入任意对象
	 */
	@Override
	public void insert( Session _sess , Object _entity ) {
		_sess.save( _entity );
		_sess.flush();
	}

	/**
	 * 插入多个映射对象
	 */
	@Override
	public < T > void insertAll( Session _sess , List< T > _list ) {
		insertAll( _sess , _list , DEFAULT_FLUSH );
	}

	/**
	 * 插入到一定数量时，会立即提交并清理缓存
	 * 
	 * @param _db
	 * @param _list
	 * @param _flush
	 */
	@Override
	public < T > void insertAll( Session _sess , List< T > _list , int _flush ) {
		int exnum = 0;
		int monum = _flush - 1;

		for ( T e : _list ) {
			_sess.save( e );
			if ( ++exnum % _flush == monum ) flushSession( _sess );
		}

		flushSession( _sess );
	}

	/**
	 * 删除任意映射对象
	 * 
	 * @param _db
	 * @param _entity
	 */
	@Override
	public void delete( Session _sess , Object _entity ) {
		_sess.delete( _entity );
		_sess.flush();
	}

	/**
	 * 删除多个映射对象
	 */
	@Override
	public < T > void deleteAll( Session _sess , List< T > _list ) {
		deleteAll( _sess , _list , DEFAULT_FLUSH );
	}

	/**
	 * 删除到一定数量时，会立即提交并清理缓存
	 * 
	 * @param _db
	 * @param _list
	 * @param _flush
	 */
	@Override
	public < T > void deleteAll( Session _sess , List< T > _list , int _flush ) {
		int exnum = 0;
		int monum = _flush - 1;

		for ( T e : _list ) {
			_sess.delete( e );
			if ( ++exnum % _flush == monum ) flushSession( _sess );
		}

		flushSession( _sess );
	}

	/**
	 * 更新任意映射对象
	 * 
	 * @param _db
	 * @param _entity
	 */
	@Override
	public void update( Session _sess , Object _entity ) {
		_sess.update( _entity );
		_sess.flush();
	}

	/**
	 * 更新到一定数量时，会立即提交并清理缓存
	 * 
	 * @param _db
	 * @param _list
	 * @param _flush
	 */
	@Override
	public < T > void updateAll( Session _sess , List< T > _list ) {
		updateAll( _sess , _list , DEFAULT_FLUSH );
	}

	/**
	 * 更新到一定数量时，会立即提交并清理缓存
	 * 
	 * @param _db
	 * @param _list
	 * @param _flush
	 */
	@Override
	public < T > void updateAll( Session _sess , List< T > _list , int _flush ) {
		int exnum = 0;
		int monum = _flush - 1;

		for ( T e : _list ) {
			_sess.update( e );
			if ( ++exnum % _flush == monum ) flushSession( _sess );
		}

		flushSession( _sess );
	}

	/**
	 * 查找任意映射对象，适合单对象单次操作。 <br/>
	 * 
	 * 默认最大返回数： 1000
	 * 
	 * @param _db
	 * @param _cls
	 * @return java.util.List
	 */
	@Override
	public < T > List< T > select( Session _sess , Class< T > _cls ) {
		return select( _sess , _cls , DEFAULT_LIMIT );
	}

	/**
	 * 
	 * 查找任意映射对象，设置最大行数，适合单对象单次操作。
	 * 
	 * @param _db
	 * @param _cls
	 * @param _limit
	 * @return java.util.List
	 */
	@Override
	public < T > List< T > select( Session _sess , Class< T > _cls , int _limit ) {
		return select( _sess , _cls , DEFAULT_FIRST , DEFAULT_LIMIT );
	}

	/**
	 * 
	 * 查找任意映射对象，设置起始位置和最大行数，适合单对象单次操作。
	 * 
	 * @param _db
	 * @param _cls
	 * @param _first
	 * @param _limit
	 * @return java.util.List
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public < T > List< T > select( Session _sess , Class< T > _cls , int _first , int _limit ) {
		return _sess.createCriteria( _cls ).setFirstResult( _first ).setMaxResults( _limit ).list();
	}

	/**
	 * 根据查找任意映射对象，适合单对象单次操作。
	 * 
	 * @param _db
	 * @param _cls
	 * @return null | Object
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public < T > T selectById( Session _sess , Class< T > _cls , Serializable _id ) {
		return ( T ) _sess.get( _cls , _id );
	}

	/**
	 * 根据映射类的对象查找结果, 当映射类中设置以下内容时，查询无效：<br/>
	 * 主键 或 关联类 或 为空
	 * 
	 * @param _db
	 * @param _obj
	 * @return java.util.List
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public < T > List< T > selectByExample( Session _sess , Object _obj ) {
		return _sess.createCriteria( _obj.getClass() ).add( Example.create( _obj ) ).list();
	}

	/**
	 * 执行HQL查询语句, 默认最大返回数： 1000
	 * 
	 * @param _db
	 * @param _hql
	 * @param _cls
	 * @param _param
	 * @return java.util.List
	 */
	@Override
	public < T > List< T > selectByHQL( Session _sess , String _hql , Object[] _param , Class< T > _cls ) {
		return selectByHQL( _sess , _hql , DEFAULT_FIRST , DEFAULT_LIMIT , _param , _cls );
	}

	/**
	 * 执行HQL查询语句, 默认最大返回数： 1000
	 * 
	 * @param _db
	 * @param _hql
	 * @param _first
	 * @param _limit
	 * @param _param
	 * @return java.util.List
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public < T > List< T > selectByHQL( Session _sess , String _hql , int _first , int _limit , Object[] _param ,
					Class< T > _cls ) {
		Query query = _sess.createQuery( _hql );
		query.setFirstResult( _first );
		query.setMaxResults( _limit );
		int i = 0;
		for ( Object o : _param )
			query.setParameter( i++ , o );
		return query.list();
	}

	/**
	 * 执行SQL查询语句, 默认最大返回数： 1000
	 * 
	 * @param _db
	 * @param _sql
	 * @param _first
	 * @param _limit
	 * @param _param
	 * @return java.util.List
	 */
	@SuppressWarnings( "unchecked" )
	@Override
	public < T > List< T > selectBySQL( Session _sess , String _sql , Object[] _param , Class< T > _cls ) {
		Query query = _sess.createSQLQuery( _sql ).addEntity( _cls );
		int i = 0;
		for ( Object o : _param )
			query.setParameter( i++ , o );
		return query.list();
	}

	/**
	 * 执行HQL更新语句，并返回影响行数。
	 * 
	 * @param _db
	 * @param _hql
	 * @return int
	 */
	@Override
	public int executeByHQL( Session _sess , String _hql ) {
		return _sess.createQuery( _hql ).executeUpdate();
	}

	/**
	 * 执行SQL更新语句，并返回影响行数。
	 * 
	 * @param _db
	 * @param _hql
	 * @return int
	 */
	@Override
	public int executeBySQL( Session _sess , String _sql ) {
		return _sess.createSQLQuery( _sql ).executeUpdate();
	}

	/**
	 * 查询某个表的记录数
	 * 
	 * @param _db
	 * @param _hql
	 * @return int
	 */
	@Override
	public < T > int selectCount( Session _sess , Class< T > _cls ) {
		String hql = "SELECT COUNT(*) FROM ";

		Query query = _sess.createQuery( hql + _cls.getName() );

		return ( int ) query.iterate().next();
	}

	/**
	 * 查询某个表的记录数
	 * 
	 * @param _db
	 * @param _hql
	 * @param _param
	 * @return int
	 */
	@Override
	public int selectCount( Session _sess , String _hql , Object[] _param ) {
		Query query = _sess.createQuery( _hql );
		int i = 0;
		for ( Object o : _param )
			query.setParameter( i++ , o );
		return ( int ) query.iterate().next();
	}
}
