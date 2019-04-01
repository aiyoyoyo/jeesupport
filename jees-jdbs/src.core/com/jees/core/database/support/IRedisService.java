package com.jees.core.database.support;

import java.util.List;
import java.util.Map;

public interface IRedisService<ID, T> {
    void onload ();

    void insert ( T _obj );

    void insertMap( Map<String, T> _map, Class< T > _cls );

    void update ( T _obj );

    void updateMap( Map<String, T> _map, Class< T > _cls );

    void delete ( T _obj );

    void deleteList( List< T > _list, Class< T > _cls );

    void deleteById ( ID _id, Class< T > _cls );

    List< T > findAll( Class< T > _cls );

    List< T > findByEquals ( String _property, Object _value, Class< T > _cls );

    List< T > findBetweens ( String _property, Object _begin, Object _end, Class< T > _cls );

    T findById ( ID _value, Class< T > _cls );
}
