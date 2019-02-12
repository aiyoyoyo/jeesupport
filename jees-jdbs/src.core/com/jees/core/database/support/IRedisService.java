package com.jees.core.database.support;

import java.util.List;

public interface IRedisService {
    void onload ();

    void unload ();

    void reload ();

    void insert ( Object _obj ) throws Exception;

    void update ( Object _obj );

    < T > List< T > findByEquals ( String _property, Object _value, Class< T > _cls );

    < T > T findById ( long _value, Class< T > _cls );
}
