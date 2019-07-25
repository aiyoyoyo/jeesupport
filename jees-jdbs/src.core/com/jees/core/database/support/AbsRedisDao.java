package com.jees.core.database.support;

import com.alibaba.fastjson.parser.ParserConfig;
import com.jees.common.CommonConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 本类的默认操作方式均为Hash方式
 * @author aiyoyoyo
 * @param <ID>
 * @param <T>
 */
@Log4j2
public abstract class AbsRedisDao<ID,T> implements IRedisDao<ID,T>{
    int index;
    @Autowired
    RedisTemplate<String,T> tpl;

    private ID _get_hk( T _obj ){
        ID hk = null;
        Class cls = _obj.getClass();

        while ( cls != null ) {
            final Field[] declaredFields = cls.getDeclaredFields();
            for (final Field f : declaredFields) {
                Id id = f.getAnnotation( Id.class );
                if ( id != null ) {
                    Method[] mths = cls.getMethods();

                    for ( Method m : mths ) {
                        if ( m.getName().equalsIgnoreCase( "get" + f.getName() ) ) {
                            try {
                                hk = ( ID ) m.invoke( _obj );
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
    public void initialize() {
        ParserConfig.getGlobalInstance().setAutoTypeSupport( true );
        ParserConfig.getGlobalInstance().addAccept( CommonConfig.getString( "spring.redis.package" ) );

        int idx = CommonConfig.getInteger( "spring.redis.database", 0 );
        database( idx );
    }

    @Override
    public void insert ( T _obj ) throws Exception{
        this.insert( index, _obj );
    }

    @Override
    public void insertMap ( Map< ID, T > _map, Class< T > _cls ) {
        this.insertMap( index, _map, _cls );
    }

    @Override
    public void update ( T _obj ) {
        this.update( index, _obj );
    }

    @Override
    public void updateMap ( Map< String, T > _map, Class< T > _cls ) {
        this.updateMap( index, _map, _cls );
    }

    @Override
    public void delete ( T _obj ) throws Exception{
        this.delete( index, _obj );
    }

    @Override
    public void deleteList ( List< T > _list, Class< T > _cls ) {
        this.deleteList( index, _list, _cls );
    }

    @Override
    public void deleteById ( ID _id, Class< T > _cls ) {
        this.deleteById( index, _id, _cls );
    }

    @Override
    public List< T > findAll ( Class< T > _cls ) {
        return this.findAll( index, _cls );
    }

    @Override
    public Map< ID, T > findHashAll( Class< T > _cls ){
        return this.findHashAll( index, _cls );
    }

    @Override
    public Map< ID, T > findHashAll( int _idx, Class< T > _cls ){
        try{
            database( _idx );
            HashOperations< String, ID, T > hash = tpl.opsForHash();
            return hash.entries( _cls.getSimpleName() );
        }catch ( Exception e ){
            log.error( "findHashAll 发生错误：IDX=[" + _idx + "]" + e.toString(), e );
        }
        return new HashMap<>();
    }

    @Override
    public List< T > findByEquals ( String _property, Object _value, Class< T > _cls ) {
        return this.findByEquals( index, _property, _value, _cls );
    }

    @Override
    public List< T > findBetweens ( String _property, Object _begin, Object _end, Class< T > _cls ) {
        return this.findBetweens( index, _property, _begin, _end, _cls );
    }

    @Override
    public T findById ( ID _value, Class< T > _cls ) {
        return this.findById( index, _value, _cls );
    }

    @Override
    public void heartbeat(){
        this.heartbeat( index );
    }

    @Override
    public void heartbeat( int _idx ){
        database( _idx );
        try{
            tpl.opsForValue().get( "heartbeat" );
        }catch ( Exception e ){
            log.error( "heartbeat 发生错误：IDX=[" + _idx + "]" + e.toString(), e );
        }finally {
            if( tpl != null && tpl.getConnectionFactory() != null ){
                RedisConnectionUtils.unbindConnection( tpl.getConnectionFactory() );
            }
        }
    }

    @Override
    public void database( int _idx ){
        if( index == _idx ){
            return;
        }

        index = _idx;

        LettuceConnectionFactory cf = ( LettuceConnectionFactory ) tpl.getConnectionFactory();
        cf.setDatabase( index );
        tpl.setConnectionFactory( cf );
    }

    public void changeDatabase( LettuceConnectionFactory _lcf ){
        RedisConnectionUtils.unbindConnection( tpl.getConnectionFactory() );
        tpl.setConnectionFactory( _lcf );
        index = _lcf.getDatabase();
        heartbeat();
    }

    @Override
    public List< T > findAll( int _idx, Class< T > _cls ) {
        try{
            database( _idx );
            HashOperations< String, ID, T > hash = tpl.opsForHash();
            List< T > list = hash.values( _cls.getSimpleName() );
            return list;
        }catch ( Exception e ){
            log.error( "findAll 发生错误：IDX=[" + _idx + "]" + e.toString(), e );
        }
        return new ArrayList<>();
    }

    @Override
    public List< T > findByEquals ( int _idx, String _property, Object _value, Class< T > _cls ) {
        try{
            database( _idx );
            HashOperations< String, String, T > hash = tpl.opsForHash();
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
            log.error( "findByEquals 发生错误：IDX=[" + _idx + "]" + e.toString(), e );
        }

        return new ArrayList<>();
    }

    @Override
    public List< T > findBetweens ( int _idx, String _property, Object _begin, Object _end, Class< T > _cls ) {
        try{
            database( _idx );
            HashOperations< String, String, T > hash = tpl.opsForHash();
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
            log.error( "findBetweens 发生错误：IDX=[" + _idx + "]" + e.toString(), e );
        }
        return new ArrayList<>();

    }

    @Override
    public T findById ( int _idx, ID _value, Class< T > _cls ) {
        try{
            database( _idx );
            HashOperations< String, ID, T > hash = tpl.opsForHash();
            Object obj = hash.get( _cls.getSimpleName(), _value );
            return ( T ) obj;
        }catch ( Exception e ) {
            log.error( "findById 发生错误：IDX=[" + _idx + "]" + e.toString(), e );
            throw e;
        }
    }
    @Override
    public void insert ( int _idx, T _obj ) throws Exception{
        try{
            database( _idx );
            HashOperations< String, ID, T > hash = tpl.opsForHash();
            ID hk = _get_hk( _obj );
            String sn = _obj.getClass().getSimpleName();
            if( hash.hasKey( sn, hk ) ){
                throw new Exception( "插入失败，包含主键[" + hk + "]的对象[" + sn + "]已存在！" );
            }

            hash.put( _obj.getClass().getSimpleName(), hk, _obj );
        }catch ( Exception e ){
            log.error( "insert 发生错误：IDX=[" + _idx + "]" + e.toString(), e );
            throw e;
        }
    }

    @Override
    public void insertMap ( int _idx, Map< ID, T > _map, Class< T > _cls ) {
        try{
            database( _idx );
            HashOperations< String, ID, T > hash = tpl.opsForHash();
            String sn = _cls.getSimpleName();
            hash.putAll( sn, _map );
        }catch ( Exception e ){
            log.error( "insertMap 发生错误：IDX=[" + _idx + "]" + e.toString(), e );
            throw e;
        }
    }

    @Override
    public void update ( int _idx, T _obj ){
        try{
            database( _idx );
            HashOperations< String, ID, T > hash = tpl.opsForHash();
            ID hk = _get_hk( _obj );
            hash.put( _obj.getClass().getSimpleName(), hk, _obj );
        }catch ( Exception e ){
            log.error( "update 发生错误：IDX=[" + _idx + "]" + e.toString(), e );
            throw e;
        }
    }

    @Override
    public void updateMap ( int _idx, Map< String, T > _map, Class< T > _cls ) {
        try{
            database( _idx );
            HashOperations< String, String, T > hash = tpl.opsForHash();
            String sn = _cls.getClass().getSimpleName();
            hash.putAll( sn, _map );
        }catch ( Exception e ){
            log.error( "update 发生错误：IDX=[" + _idx + "]" + e.toString(), e );
            throw e;
        }
    }

    @Override
    public void delete( int _idx, T _obj ) throws Exception{
        try{
            database( _idx );
            HashOperations< String, ID, T > hash = tpl.opsForHash();
            ID hk = _get_hk( _obj );
            String sn = _obj.getClass().getSimpleName();
            if( hash.hasKey( sn, hk ) ){
                long num = hash.delete( sn, hk );
                log.debug( "--已删除纪录：" + num + "条" );
            }else
                throw new Exception( "删除失败，包含主键[" + hk + "]的对象[" + sn + "]不存在！" );
        }catch ( Exception e ){
            log.error( "update 发生错误：IDX=[" + _idx + "]" + e.toString(), e );
            throw e;
        }
    }

    @Override
    public void deleteList ( int _idx, List< T > _list, Class< T > _cls ) {
        try{
            database( _idx );
            HashOperations< String, ID, T > hash = tpl.opsForHash();
            String sn = _cls.getClass().getSimpleName();
            long num = hash.delete( sn, _list.toArray() );
            log.debug( "--已删除纪录：" + num + "条" );
        }catch ( Exception e ){
            log.error( "update 发生错误：IDX=[" + _idx + "]" + e.toString(), e );
        }
    }

    @Override
    public void deleteById( int _idx, ID _id, Class< T > _cls ){
        try{
            database( _idx );
            HashOperations< String, ID, T > hash = tpl.opsForHash();
            ID hk = _id;
            String sn = _cls.getSimpleName();
            if( hash.hasKey( sn, hk ) ){
                hash.delete( sn, hk );
            }else
                throw new Exception( "删除失败，包含主键[" + hk + "]的对象[" + sn + "]不存在！" );
        }catch ( Exception e ){
            log.error( "update 发生错误：IDX=[" + _idx + "]" + e.toString(), e );
        }
    }
}
