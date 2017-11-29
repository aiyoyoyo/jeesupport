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
 * 实现了标准查询内容，根据需要可以继承后扩展数据操作，事务实现通过上层业务类加入注解：org.
 * springframework.transaction.annotation.Transactional 来管理。
 * 
 * @author aiyoyoyo
 * 
 */
public abstract class AbsSupportDao implements ISupportDao {
	/**
	 * 从配置文件中获取数据库链接对象。
	 */

	private static HashMap< String , SessionFactory >	sessionFactoryMap;

	@Resource( name = DEFAULT_SFMAP )
	protected void setSessionFactoryMap( HashMap< String , SessionFactory > sessionFactoryMap ) {
		AbsSupportDao.sessionFactoryMap = sessionFactoryMap;
	}

	protected Session _open_session( String _db ) {
		if ( sessionFactoryMap.containsKey( _db ) ) {
			SessionFactory sessFactory = sessionFactoryMap.get( _db );
			return sessFactory.openSession();
		}

		throw new NullPointerException( "没有找到数据库[" + _db + "]的对应Session容器，请检查配置文件中的[ " + DEFAULT_SFMAP + " ]是否正确。" );
	}

	protected Session _get_session( String _db ) {
		if ( sessionFactoryMap.containsKey( _db ) ) {
			SessionFactory sessFactory = sessionFactoryMap.get( _db );
			if( sessFactory.isClosed()) return sessFactory.openSession();
			return sessFactory.getCurrentSession();
		}

		throw new NullPointerException( "没有找到数据库[" + _db + "]的对应Session容器，请检查配置文件中的[ " + DEFAULT_SFMAP + " ]是否正确。" );
	}

	protected void _flush_session( Session _sess ) {
		if ( _sess == null ) return;
		_sess.flush();
		_sess.clear();
	}

	// insert /////////////////////////////////////////////////////////////
	@Override
	public void insert( String _db , Object _entity ) {
		Session ses = _get_session( _db );
		ses.save( _entity );
	}

	@Override
	public < T > void insertAll( String _db , List< T > _list ) {
		insertAll( _db , _list , DEFAULT_FLUSH );
	}

	@Override
	public < T > void insertAll( String _db , List< T > _list , int _flush ) {
		Session sess = _get_session( _db );

		int exnum = 0;
		int monum = _flush - 1;

		for ( T e : _list ) {
			sess.save( e );
			if ( ++exnum % _flush == monum ) _flush_session( sess );
		}

		_flush_session( sess );
	}

	// delete //////////////////////////////////////////////////////////////
	@Override
	public void delete( String _db , Object _entity ) {
		Session ses = _get_session( _db );
		ses.delete( _entity );
	}

	@Override
	public < T > void deleteAll( String _db , List< T > _list ) {
		deleteAll( _db , _list , DEFAULT_FLUSH );
	}

	@Override
	public < T > void deleteAll( String _db , List< T > _list , int _flush ) {
		Session sess = _get_session( _db );

		int exnum = 0;
		int monum = _flush - 1;

		for ( T e : _list ) {
			sess.delete( e );
			if ( ++exnum % _flush == monum ) _flush_session( sess );
		}

		_flush_session( sess );
	}

	// update ///////////////////////////////////////////////////////////////
	@Override
	public void update( String _db , Object _entity ) {
		Session ses = _get_session( _db );
		ses.update( _entity );
	}

	@Override
	public < T > void updateAll( String _db , List< T > _list ) {
		updateAll( _db , _list , DEFAULT_FLUSH );
	}

	@Override
	public < T > void updateAll( String _db , List< T > _list , int _flush ) {
		Session sess = _get_session( _db );

		int exnum = 0;
		int monum = _flush - 1;

		for ( T e : _list ) {
			sess.update( e );
			if ( ++exnum % _flush == monum ) _flush_session( sess );
		}

		_flush_session( sess );
	}

	// select /////////////////////////////////////////////////////////////////
	@Override
	public < T > List< T > select( String _db , Class< T > _cls ) {
		return select( _db , _cls , DEFAULT_LIMIT );
	}

	@Override
	public < T > List< T > select( String _db , Class< T > _cls , int _limit ) {
		return select( _db , _cls , DEFAULT_FIRST , DEFAULT_LIMIT );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public < T > List< T > select( String _db , Class< T > _cls , int _first , int _limit ) {
		Session sess = _get_session( _db );
		return sess.createCriteria( _cls ).setFirstResult( _first ).setMaxResults( _limit ).list();
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public < T > T selectById( String _db , Class< T > _cls , Serializable _id ) {
		Session sess = _get_session( _db );
		return ( T ) sess.get( _cls , _id );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public < T > List< T > selectByExample( String _db , Object _obj ) {
		Session sess = _get_session( _db );
		return sess.createCriteria( _obj.getClass() ).add( Example.create( _obj ) ).list();
	}

	@Override
	public < T > List< T > selectByHQL( String _db , String _hql , Object[] _param , Class< T > _cls ) {
		return selectByHQL( _db , _hql , DEFAULT_FIRST , DEFAULT_LIMIT , _param , _cls );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public < T > List< T > selectByHQL( String _db , String _hql , int _first , int _limit , Object[] _param ,
					Class< T > _cls ) {
		Session sess = _get_session( _db );
		Query query = sess.createQuery( _hql );
		query.setFirstResult( _first );
		query.setMaxResults( _limit );
		int i = 0;
		if ( _param != null ) for ( Object o : _param )
			query.setParameter( i++ , o );
		return query.list();
	}

	@Override
	public < T > List< T > selectBySQL( String _db , String _sql , Object[] _param , Class< T > _cls ) {
		return selectBySQL( _db , _sql , DEFAULT_FIRST , DEFAULT_LIMIT , _param , _cls );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public < T > List< T > selectBySQL( String _db , String _sql , int _first , int _limit , Object[] _param ,
					Class< T > _cls ) {
		Session sess = _get_session( _db );
		Query query = sess.createSQLQuery( _sql );
		query.setFirstResult( _first );
		query.setMaxResults( _limit );
		int i = 0;
		if ( _param != null ) for ( Object o : _param )
			query.setParameter( i++ , o );
		return query.list();
	}

	@Override
	public < T > long selectCount( String _db , Class< T > _cls ) {
		String hql = "SELECT COUNT(*) FROM ";

		Query query = _get_session( _db ).createQuery( hql + _cls.getName() );

		return ( long ) query.iterate().next();
	}

	@Override
	public long selectCount( String _db , String _hql , Object[] _param ) {
		Query query = _get_session( _db ).createQuery( _hql );
		int i = 0;
		if ( _param != null ) for ( Object o : _param )
			query.setParameter( i++ , o );

		return ( long ) query.iterate().next();
	}

	@Override
	public int executeByHQL( String _db , String _hql , Object[] _param ) {
		Session sess = _get_session( _db );
		try {
			Query query = _get_session( _db ).createQuery( _hql );
			int i = 0;
			if ( _param != null ) for ( Object o : _param )
				query.setParameter( i++ , o );
			return query.executeUpdate();
		} finally {
			_flush_session( sess );
		}
	}

	@Override
	public int executeBySQL( String _db , String _sql , Object[] _param ) {
		Session sess = _get_session( _db );
		try {
			Query query = _get_session( _db ).createSQLQuery( _sql );
			int i = 0;
			if ( _param != null ) for ( Object o : _param )
				query.setParameter( i++ , o );
			return query.executeUpdate();
		} finally {
			_flush_session( sess );
		}
	}
}
