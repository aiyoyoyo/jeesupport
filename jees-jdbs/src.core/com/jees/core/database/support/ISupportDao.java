package com.jees.core.database.support;

import org.hibernate.SessionFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * 定义了标准数据库处理模型的接口
 * 
 * @author aiyoyoyo
 *
 */
public interface ISupportDao {
	int		DEFAULT_LIMIT	= 1000;
	int		DEFAULT_FIRST	= 0;
	int		DEFAULT_FLUSH	= 100;

	void putSessionFactory(String _db, SessionFactory _sf );

	void insert( String _db , Object _entity );

	< T > void insertAll( String _db , List< T > _list );

	< T > void insertAll( String _db , List< T > _list , int _flush );

	void delete( String _db , Object _entity );

	< T > void deleteAll( String _db , List< T > _list );

	< T > void deleteAll( String _db , List< T > _list , int _flush );

	void update( String _db , Object _entity );

	< T > void updateAll( String _db , List< T > _list );

	< T > void updateAll( String _db , List< T > _list , int _flush );

	< T > List< T > select( String _db , Class< T > _cls );

	< T > List< T > select( String _db , Class< T > _cls , int _limit );

	< T > List< T > select( String _db , Class< T > _cls , int _first , int _limit );

	< T > T selectById( String _db , Class< T > _cls , Serializable _id );

	< T > List< T > selectByHQL( String _db , String _hql , String[] _param , Object[] _value, Class< T > _cls );

	< T > List< T > selectByHQL( String _db , String _hql , int _first , int _limit , String[] _param , Object[] _value,
			Class< T > _cls );

	< T > List< T > selectBySQL( String _db , String _sql , String[] _param , Object[] _value, Class< T > _cls );

	< T > List< T > selectBySQL( String _db , String _sql , int _first , int _limit , String[] _param , Object[] _value,
			Class< T > _cls );

	< T > long selectCount( String _db , Class< T > _cls );

	long selectCountByHQL( String _db , String _hql , String[] _param , Object[] _value );

	long selectCountBySQL( String _db , String _sql , String[] _param , Object[] _value );

	int executeByHQL( String _db , String _hql , String[] _param, Object[] _value );

	int executeBySQL( String _db , String _sql , String[] _param, Object[] _value );

}
