package com.jees.core.database.support;

import java.util.List;

import org.hibernate.Session;

public interface IDao {
	static int	DEFAULT_LIMIT	= 1000;
	static int	DEFAULT_FIRST	= 0;

	public Session getSession( String _dbName ) throws NullPointerException;

	public void insert( String _db , Object _entity );

	public void delete( String _db , Object _entity );

	public < T > List< T > select( String _db , Class< T > _cls );

	public void update( String _db , Object _entity );
}
