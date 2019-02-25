package com.jees.core.database.support;

import java.util.List;

public interface IRedisService<ID> {
    void onload ();

    void unload ();

    void reload ();

    void insert ( Object _obj ) throws Exception;

    void update ( Object _obj ) throws NullPointerException;

    void delete ( Object _obj ) throws Exception;

    < T > List< T > findAll( Class< T > _cls );

    < T > List< T > findByEquals ( String _property, Object _value, Class< T > _cls );

    < T > List< T > findBetweens ( String _property, Object _begin, Object _end, Class< T > _cls );

    < T > T findById ( ID _value, Class< T > _cls );
}
