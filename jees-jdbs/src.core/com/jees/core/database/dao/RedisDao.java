package com.jees.core.database.dao;

import com.alibaba.fastjson.parser.ParserConfig;
import com.jees.common.CommonConfig;
import com.jees.core.database.support.IRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RedisDao implements IRedisService {
    @Autowired
    RedisTemplate<String, Object> tpl;

    private String _get_hk( Object _obj ){
        String hk = null;

        Class cls = _obj.getClass();
        Field[] fields = cls.getDeclaredFields();

        for ( Field f : fields ) {
            Id id = f.getAnnotation( Id.class );
            if ( id != null ) {
                Method[] mths = cls.getMethods();
                for ( Method m : mths ) {
                    if ( m.getName().equalsIgnoreCase( "get" + f.getName() ) ) {
                        try {
                            hk = String.valueOf( m.invoke( _obj ) );
                            break;
                        } catch ( IllegalAccessException e ) {
                        } catch ( InvocationTargetException e ) {
                        }
                    }
                }
                break;
            }
        }

        if( hk == null ){
            throw new NullPointerException( "没有声明有效的ID" );
        }

        return hk;
    }

    @Override
    public void onload () {
        ParserConfig.getGlobalInstance().setAutoTypeSupport( true );
        ParserConfig.getGlobalInstance().addAccept( CommonConfig.getString( "spring.redis.package" ) );
    }

    @Override
    public void unload () {
    }

    @Override
    public void reload () {
    }

    @Override
    public < T > List< T > findAll( Class< T > _cls ) {
        List< T > list = ( List< T > ) tpl.opsForHash().values( _cls.getSimpleName() );

        return list;
    }

    @Override
    public < T > List< T > findByEquals ( String _property, Object _value, Class< T > _cls ) {
        List< T > list = ( List< T > ) tpl.opsForHash().values( _cls.getSimpleName() );

        return list.stream().filter( t -> {
            Method[] mths = t.getClass().getMethods();
            for ( Method m : mths ) {
                if ( m.getName().equalsIgnoreCase( "get" + _property ) ) {
                    try {
                        if( _value instanceof String ){
                            return _value.equals( m.invoke( t ) );
                        }

                        return _value == m.invoke( t );
                    } catch ( IllegalAccessException e ) {
                    } catch ( InvocationTargetException e ) {
                    }
                }
            }
            return false;
        } ).collect( Collectors.toList() );
    }

    @Override
    public < T > T findById ( long _value, Class< T > _cls ) {
        Object obj = tpl.opsForHash().get( _cls.getSimpleName(), String.valueOf( _value ) );
        return ( T ) obj;
    }
    @Override
    public void insert ( Object _obj ) throws Exception {
        String hk = _get_hk( _obj );
        String sn = _obj.getClass().getSimpleName();

        if( tpl.opsForHash().entries( sn ).containsKey( hk ) ){
            throw new Exception( "插入失败，包含主键[" + hk + "]的对象已存在！" );
        }

        tpl.opsForHash().put( _obj.getClass().getSimpleName(), hk, _obj );
    }
    @Override
    public void update ( Object _obj ) throws NullPointerException{
        String hk = _get_hk( _obj );

        tpl.opsForHash().put( _obj.getClass().getSimpleName(), hk, _obj );
    }

    @Override
    public void delete( Object _obj ) throws Exception{
        String hk = _get_hk( _obj );
        String sn = _obj.getClass().getSimpleName();

        if( tpl.opsForHash().entries( sn ).containsKey( hk ) ){
            tpl.opsForHash().entries( sn ).remove( hk );
        }else
            throw new Exception( "删除失败，包含主键[" + hk + "]的对象不存在！" );
    }
}
