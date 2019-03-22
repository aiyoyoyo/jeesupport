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
public class RedisDao<ID, T> implements IRedisService< ID, T > {
    @Autowired
    RedisTemplate<String, T> tpl;

    private String _get_hk( T _obj ){
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
    public List< T > findAll( Class< T > _cls ) {
        List< T > list = ( List< T > ) tpl.opsForHash().values( _cls.getSimpleName() );

        return list;
    }

    @Override
    public List< T > findByEquals ( String _property, Object _value, Class< T > _cls ) {
        List< T > list = ( List< T > ) tpl.opsForHash().values( _cls.getSimpleName() );

        return list.stream().filter( t -> {
            Method[] mths = t.getClass().getMethods();
            for ( Method m : mths ) {
                if ( m.getName().equalsIgnoreCase( "get" + _property ) ) {
                    try {
                        Object invoke = m.invoke( t );

                        if( invoke instanceof Integer ){
                            return (int)_value == (int) invoke;
                        }else if( invoke instanceof Long ){
                            return (long)_value == (long) invoke;
                        }else if( invoke instanceof Double ){
                            return (double)_value == (double) invoke;
                        }else if( invoke instanceof Float ){
                            return (float)_value == (float) invoke;
                        }else if( invoke instanceof Boolean ){
                            return (boolean)_value == (boolean) invoke;
                        }else if( _value instanceof String ){
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
    public List< T > findBetweens ( String _property, Object _begin, Object _end, Class< T > _cls ) {
        List< T > list = ( List< T > ) tpl.opsForHash().values( _cls.getSimpleName() );

        return list.stream().filter( t -> {
            Method[] mths = t.getClass().getMethods();
            for ( Method m : mths ) {
                if ( m.getName().equalsIgnoreCase( "get" + _property ) ) {
                    try {
                        Object invoke = m.invoke( t );

                        if( invoke instanceof Integer ){
                            int val = (int)invoke;
                            return ( (int) _begin <= val ) && ( val <= (int) _end );
                        }else if( invoke instanceof Long ){
                            long val = (long)invoke;
                            return ( (long) _begin <= val ) && ( val <= (long) _end );
                        }else if( invoke instanceof Double ){
                            double val = (double)invoke;
                            return ( (double) _begin <= val ) && ( val <= (double) _end );
                        }else if( invoke instanceof Float ){
                            float val = (float)invoke;
                            return ( (float) _begin <= val ) && ( val <= (float) _end );
                        }
                    } catch ( IllegalAccessException e ) {
                    } catch ( InvocationTargetException e ) {
                    }
                }
            }
            return false;
        } ).collect( Collectors.toList() );
    }

    @Override
    public T findById ( ID _value, Class< T > _cls ) {
        Object obj = tpl.opsForHash().get( _cls.getSimpleName(), String.valueOf( _value ) );
        return ( T ) obj;
    }
    @Override
    public void insert ( T _obj ) throws Exception {
        String hk = _get_hk( _obj );
        String sn = _obj.getClass().getSimpleName();

        if( tpl.opsForHash().hasKey( sn, hk ) ){
            throw new Exception( "插入失败，包含主键[" + hk + "]的对象已存在！" );
        }

        tpl.opsForHash().put( _obj.getClass().getSimpleName(), hk, _obj );
    }
    @Override
    public void update ( T _obj ) throws NullPointerException{
        String hk = _get_hk( _obj );

        tpl.opsForHash().put( _obj.getClass().getSimpleName(), hk, _obj );
    }

    @Override
    public void delete( T _obj ) throws Exception{
        String hk = _get_hk( _obj );
        String sn = _obj.getClass().getSimpleName();
        if( this.tpl.opsForHash().hasKey( sn, hk ) ){
            this.tpl.opsForHash().delete( sn, hk );
        }else
            throw new Exception( "删除失败，包含主键[" + hk + "]的对象不存在！" );
    }
}
