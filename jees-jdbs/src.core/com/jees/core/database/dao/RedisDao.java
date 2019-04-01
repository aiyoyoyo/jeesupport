package com.jees.core.database.dao;

import com.alibaba.fastjson.parser.ParserConfig;
import com.jees.common.CommonConfig;
import com.jees.core.database.support.IRedisService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
public class RedisDao<ID, T> implements IRedisService< ID, T > {
    @Autowired
    RedisTemplate<String, T> tpl;

    HashOperations< String, String, T >     hash;

    private String _get_hk( T _obj ){
        String hk = null;

        Class cls = _obj.getClass();

        final List<Field> allFields = new ArrayList<Field>();
        while ( cls != null ) {
            final Field[] declaredFields = cls.getDeclaredFields();
            for (final Field f : declaredFields) {
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
                if( hk != null ){
                    break;
                }
            }
            cls = cls.getSuperclass();
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

        hash = tpl.opsForHash();
    }

    public void heartbeat(){
        try{
            tpl.opsForValue().get( "heartbeat" );
        }catch ( Exception e ){
            log.error( "heartbeat 发生错误：" + e.toString(), e );
        }finally {
            RedisConnectionUtils.unbindConnection( tpl.getConnectionFactory() );
        }

    }

    @Override
    public List< T > findAll( Class< T > _cls ) {
        try{
            List< T > list = hash.values( _cls.getSimpleName() );
            return list;
        }catch ( Exception e ){
            log.error( "findAll 发生错误：" + e.toString(), e );
        }finally {
            RedisConnectionUtils.unbindConnection( tpl.getConnectionFactory() );
        }

        return new ArrayList<>();
    }

    @Override
    public List< T > findByEquals ( String _property, Object _value, Class< T > _cls ) {
        try{
            List< T > list = hash.values( _cls.getSimpleName() );

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
        }catch ( Exception e ){
            log.error( "findByEquals 发生错误：" + e.toString(), e );
        }finally {
            RedisConnectionUtils.unbindConnection( tpl.getConnectionFactory() );
        }

        return new ArrayList<>();
    }

    @Override
    public List< T > findBetweens ( String _property, Object _begin, Object _end, Class< T > _cls ) {
        try{
            List< T > list = hash.values( _cls.getSimpleName() );

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
        }catch ( Exception e ){
            log.error( "findBetweens 发生错误：" + e.toString(), e );
        }finally {
            RedisConnectionUtils.unbindConnection( tpl.getConnectionFactory() );
        }
        return new ArrayList<>();

    }

    @Override
    public T findById ( ID _value, Class< T > _cls ) {
        try{
            String id = _value.toString();

            Object obj = tpl.opsForHash().get( _cls.getSimpleName(), id );
            return ( T ) obj;
        }catch ( Exception e ) {
            log.error( "findById 发生错误：" + e.toString(), e );
        }finally {
            RedisConnectionUtils.unbindConnection( tpl.getConnectionFactory() );
        }

        return null;
    }
    @Override
    public void insert ( T _obj ){
        try{
            String hk = _get_hk( _obj );
            String sn = _obj.getClass().getSimpleName();
            if( hash.hasKey( sn, hk ) ){
                throw new Exception( "插入失败，包含主键[" + hk + "]的对象[" + sn + "]已存在！" );
            }

            hash.put( _obj.getClass().getSimpleName(), hk, _obj );
        }catch ( Exception e ){
            log.error( "insert 发生错误：" + e.toString(), e );
        }finally {
            RedisConnectionUtils.unbindConnection( tpl.getConnectionFactory() );
        }
    }

    @Override
    public void insertMap ( Map< String, T > _map, Class< T > _cls ) {
        try{
            String sn = _cls.getClass().getSimpleName();
            hash.putAll( sn, _map );
        }catch ( Exception e ){
            log.error( "insertMap 发生错误：" + e.toString(), e );
            RedisConnectionUtils.unbindConnection( tpl.getConnectionFactory() );
        }
    }

    @Override
    public void update ( T _obj ){
        try{
            String hk = _get_hk( _obj );

            hash.put( _obj.getClass().getSimpleName(), hk, _obj );
        }catch ( Exception e ){
            log.error( "update 发生错误：" + e.toString(), e );
        }finally {
            RedisConnectionUtils.unbindConnection( tpl.getConnectionFactory() );
        }
    }

    @Override
    public void updateMap ( Map< String, T > _map, Class< T > _cls ) {
        try{
            String sn = _cls.getClass().getSimpleName();

            hash.putAll( sn, _map );
        }catch ( Exception e ){
            log.error( "update 发生错误：" + e.toString(), e );
        }finally {
            RedisConnectionUtils.unbindConnection( tpl.getConnectionFactory() );
        }
    }

    @Override
    public void delete( T _obj ){
        try{
            String hk = _get_hk( _obj );
            String sn = _obj.getClass().getSimpleName();
            if( hash.hasKey( sn, hk ) ){
                hash.delete( sn, hk );
            }else
                throw new Exception( "删除失败，包含主键[" + hk + "]的对象[" + sn + "]不存在！" );
        }catch ( Exception e ){
            log.error( "update 发生错误：" + e.toString(), e );
        }finally {
            RedisConnectionUtils.unbindConnection( tpl.getConnectionFactory() );
        }
    }

    @Override
    public void deleteList ( List< T > _list, Class< T > _cls ) {
        try{
            String sn = _cls.getClass().getSimpleName();

            hash.delete( sn, _list.toArray() );
        }catch ( Exception e ){
            log.error( "update 发生错误：" + e.toString(), e );
        }finally {
            RedisConnectionUtils.unbindConnection( tpl.getConnectionFactory() );
        }
    }

    @Override
    public void deleteById( ID _id, Class< T > _cls ){
        try{
            String hk = _id.toString();
            String sn = _cls.getSimpleName();
            if( hash.hasKey( sn, hk ) ){
                hash.delete( sn, hk );
            }else
                throw new Exception( "删除失败，包含主键[" + hk + "]的对象[" + sn + "]不存在！" );
        }catch ( Exception e ){
            log.error( "update 发生错误：" + e.toString(), e );
        }finally {
            RedisConnectionUtils.unbindConnection( tpl.getConnectionFactory() );
        }
    }
}
