package com.jees.core.database.support;

import java.util.List;

public interface IRedisService<ID, T> {
    void onload ();

    void insert ( T _obj ) throws Exception;

    void update ( T _obj ) throws NullPointerException;

    void delete ( T _obj ) throws Exception;

    List< T > findAll( Class< T > _cls );

    List< T > findByEquals ( String _property, Object _value, Class< T > _cls );

    List< T > findBetweens ( String _property, Object _begin, Object _end, Class< T > _cls );

    T findById ( ID _value, Class< T > _cls );
}
