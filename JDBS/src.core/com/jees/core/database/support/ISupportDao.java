package com.jees.core.database.support;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

/**
 * 定了最基础的增删改查方法的定义，由子类自行实现
 * @author aiyoyoyo
 *
 */
public interface ISupportDao {
	String	DEFAULT_SFMAP	= "sessionFactoryMap";
	int		DEFAULT_LIMIT	= 1000;
	int		DEFAULT_FIRST	= 0;
	int		DEFAULT_FLUSH	= 100;

	Session getSession( String _db ) throws NullPointerException;
	
	void flushSession( Session _sess );

	void insert( String _db , Object _entity );

	< T > void insertAll( String _db , List< T > _list );

	void delete( String _db , Object _entity );

	< T > void deleteAll( String _db , List< T > _list );

	void update( String _db , Object _entity );

	< T > void updateAll( String _db , List< T > _list );

	int executeByHQL( String _db , String _hql );
	
	int executeBySQL( String _db , String _sql );

	< T > List< T > select( String _db , Class< T > _cls );

	< T > T selectById( String _db , Class< T > _cls , Serializable _id );

	< T > List< T > selectByExample( String _db , Object _entity );

	< T > List< T > selectByHQL( String _db , String _hql , Object[] _param , Class< T > _cls );

	< T > List< T > selectBySQL( String _db , String _sql , Object[] _param , Class< T > _cls );
}
