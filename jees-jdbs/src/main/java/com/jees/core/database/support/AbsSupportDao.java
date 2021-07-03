package com.jees.core.database.support;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 实现了标准查询内容，根据需要可以继承后扩展数据操作，事务实现通过上层业务类加入注解：org.
 * springframework.transaction.annotation.Transactional 来管理。
 * 
 * @author aiyoyoyo
 * 
 */
public abstract class AbsSupportDao implements ISupportDao {
	/** 从配置文件中获取数据库链接对象。**/
	private static Map< String , SessionFactory > sessionFactoryMap = new HashMap<>();

	private void setSessionFactoryMap( Map< String , SessionFactory > sessionFactoryMap ){
		AbsSupportDao.sessionFactoryMap = sessionFactoryMap;
	}

	private void _set_parameter( Query _query, String[] _param, Object[] _value ){
		if ( _param != null && _value != null ) {
			for( int i = 0; i < _param.length; i ++ ){
				if( _value[i] instanceof Collection )
					_query.setParameterList( _param[i] , ( Collection<?> ) _value[i] );
				else
					_query.setParameter( _param[i] ,_value[i] );
			}
		}
	}

	protected Session _open_session( String _db ) {
		if ( sessionFactoryMap.containsKey( _db ) ) {
			SessionFactory sessionFactory = sessionFactoryMap.get( _db );
			return sessionFactory.openSession();
		}

		throw new NullPointerException( "没有找到数据库[" + _db + "]的对应Session容器，请检查配置文件中是否正确。" );
	}

	protected Session _get_session( String _db ) {
		if ( sessionFactoryMap.containsKey( _db ) ) {
			SessionFactory sessionFactory = sessionFactoryMap.get( _db );
			if( sessionFactory.isClosed()) return sessionFactory.openSession();
			return sessionFactory.getCurrentSession();
		}

		throw new NullPointerException( "没有找到数据库[" + _db + "]的对应Session容器，请检查配置文件中是否正确。" );
	}

	protected void _flush_session( Session _session ) {
		if ( _session == null ) return;
		_session.flush();
		_session.clear();
	}

	public void putSessionFactory( String _db, SessionFactory _sf ){
		sessionFactoryMap.put( _db, _sf );
	}
	// insert /////////////////////////////////////////////////////////////
	@Override
	public void insert( String _db , Object _entity ) {
		Session session = _get_session( _db );
		session.save( _entity );
	}

	@Override
	public < T > void insertAll( String _db , List< T > _list ) {
		insertAll( _db , _list , DEFAULT_FLUSH );
	}

	@Override
	public < T > void insertAll( String _db , List< T > _list , int _flush ) {
		Session session = _get_session( _db );

		int exnum = 0;
		int monum = _flush - 1;

		for ( T e : _list ) {
			session.save( e );
			if ( ++exnum % _flush == monum ) _flush_session( session );
		}

		_flush_session( session );
	}

	// delete //////////////////////////////////////////////////////////////
	@Override
	public void delete( String _db , Object _entity ) {
		Session session = _get_session( _db );
		session.delete( _entity );
	}

	@Override
	public < T > void deleteAll( String _db , List< T > _list ) {
		deleteAll( _db , _list , DEFAULT_FLUSH );
	}

	@Override
	public < T > void deleteAll( String _db , List< T > _list , int _flush ) {
		Session session = _get_session( _db );

		int exnum = 0;
		int monum = _flush - 1;

		for ( T e : _list ) {
			session.delete( e );
			if ( ++exnum % _flush == monum ) _flush_session( session );
		}

		_flush_session( session );
	}

	// update ///////////////////////////////////////////////////////////////
	@Override
	public void update( String _db , Object _entity ) {
		Session ses = _get_session( _db );
		ses.merge( _entity );
	}

	@Override
	public < T > void updateAll( String _db , List< T > _list ) {
		updateAll( _db , _list , DEFAULT_FLUSH );
	}

	@Override
	public < T > void updateAll( String _db , List< T > _list , int _flush ) {
		Session session = _get_session( _db );

		int exnum = 0;
		int monum = _flush - 1;

		for ( T e : _list ) {
			session.merge( e );
			if ( ++exnum % _flush == monum ) _flush_session( session );
		}

		_flush_session( session );
	}

	// select /////////////////////////////////////////////////////////////////
	@Override
	public < T > List< T > select( String _db , Class< T > _cls ) {
		return select( _db , _cls , DEFAULT_LIMIT );
	}

	@Override
	public < T > List< T > select( String _db , Class< T > _cls , int _limit ) {
		return select( _db , _cls , DEFAULT_FIRST , _limit );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public < T > List< T > select( String _db , Class< T > _cls , int _first , int _limit ) {
		Session session = _get_session( _db );
		CriteriaQuery<T> criteria = session.getCriteriaBuilder().createQuery(_cls);
		criteria.from( _cls );
		return session.createQuery( criteria )
				.setFirstResult( _first ).setMaxResults( _limit ).getResultList();
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public < T > T selectById( String _db , Class< T > _cls , Serializable _id ) {
		Session session = _get_session( _db );
		return ( T ) session.get( _cls , _id );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public < T > List< T > selectByHQL( String _db , String _hql , String[] _param , Object[] _value, Class< T > _cls ){
		return selectByHQL( _db, _hql, DEFAULT_FIRST, DEFAULT_LIMIT, _param, _value, _cls );
	}
	@Override
	@SuppressWarnings( "unchecked" )
	public < T > List< T > selectByHQL( String _db , String _hql , int _first , int _limit , String[] _param , Object[] _value,
					Class< T > _cls ) {
		Session session = _get_session( _db );
		Query< T > query = session.createQuery( _hql );
		query.setFirstResult( _first );
		query.setMaxResults( _limit );

		_set_parameter( query, _param, _value );
		return query.getResultList();
	}

	@Override
	public < T > List< T > selectBySQL( String _db , String _sql , String[] _param , Object[] _value, Class< T > _cls ) {
		return selectBySQL( _db , _sql , DEFAULT_FIRST , DEFAULT_LIMIT , _param , _value, _cls );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public < T > List< T > selectBySQL( String _db , String _sql , int _first , int _limit , String[] _param , Object[] _value,
					Class< T > _cls ) {
		Session session = _get_session( _db );
		Query< T > query = session.createNativeQuery( _sql, _cls );
		query.setFirstResult( _first );
		query.setMaxResults( _limit );

		_set_parameter( query, _param, _value );
		return query.getResultList();
	}

	@Override
	public < T > long selectCount( String _db , Class< T > _cls ) {
		String hql = "SELECT COUNT(*) FROM ";

		Query query = _get_session( _db ).createQuery( hql + _cls.getName() );

		return ( long ) query.iterate().next();
	}

	@Override
	public long selectCountBySQL( String _db , String _sql , String[] _param , Object[] _value ) {
		Query query = _get_session( _db ).createNativeQuery( _sql );
		_set_parameter( query, _param, _value );
		return ( long ) query.iterate().next();
	}

	@Override
	public long selectCountByHQL( String _db , String _hql , String[] _param , Object[] _value ) {
		Query query = _get_session( _db ).createQuery( _hql );
		_set_parameter( query, _param, _value );
		return ( long ) query.iterate().next();
	}

	@Override
	public int executeByHQL( String _db , String _hql , String[] _param, Object[] _value ) {
		Session session = _get_session( _db );
		try {
			Query query = _get_session( _db ).createQuery( _hql );
			_set_parameter( query, _param, _value );
			return query.executeUpdate();
		} finally {
			_flush_session( session );
		}
	}
	
	@Override
	public int executeBySQL( String _db , String _sql , String[] _param, Object[] _value ) {
		Session session = _get_session( _db );
		try {
			Query query = _get_session( _db ).createNativeQuery( _sql );
			_set_parameter( query, _param, _value );
			return query.executeUpdate();
		} finally {
			_flush_session( session );
		}
	}
}
