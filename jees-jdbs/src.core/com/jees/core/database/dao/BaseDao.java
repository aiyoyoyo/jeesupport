package com.jees.core.database.dao;

import com.jees.core.database.support.AbsSupportDao;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 通过缓存操作的形式，先记录要操作的数据（不包含查询），在需要的时候通过commit()提交。
 * @author aiyoyoyo
 */
@Component
public class BaseDao extends AbsSupportDao{
    private Map< String, List<Object> > insertMap = new HashMap<>();
    private Map< String, List<Object> > deleteMap = new HashMap<>();
    private Map< String, List<Object> > updateMap = new HashMap<>();

    private void _push( String _db, Object _obj, Map< String, List<Object> > _map ){
        List<Object> list = _map.getOrDefault( _db, new ArrayList<>() );

        if( _obj instanceof Collection )
            list.addAll( ( Collection< Object > ) _obj );
        else
            list.add( _obj );

        _map.put( _db, list );
    }

    /**
     * 需要通过调用commit()方法，正式提交到数据库
     * @param _db
     * @param _obj
     */
    public void insert( String _db, Object _obj ){
        _push( _db, _obj, insertMap );
    }

    /**
     * 需要通过调用commit()方法，正式提交到数据库
     * @param _db
     * @param _obj
     */
    public void update( String _db, Object _obj ){
        _push( _db, _obj, updateMap );
    }

    /**
     * 需要通过调用commit()方法，正式提交到数据库
     * @param _db
     * @param _obj
     */
    public void delete( String _db, Object _obj ){
        _push( _db, _obj, deleteMap );
    }

    /**
     * 正式提交到数据库，该方法会剔除insert/update数据中已经被声明在delete中的重复对象
     */
    public void commit(){
        deleteMap.keySet().forEach( key -> {
            List<Object> delList = deleteMap.get( key );
            List<Object> updList = updateMap.get( key );
            List<Object> insList = insertMap.get( key );

            Iterator< Object > it = insList.iterator();

            while ( it.hasNext() ) {
                Object x = it.next();
                Optional< Object > insert = insList.stream().filter(o -> o.equals( x ) ).findFirst();
                if ( insert.isPresent() ) {
                    insList.remove(insert.get());
                    break;
                }
            }

            it = updList.iterator();
            while ( it.hasNext() ) {
                Object x = it.next();
                Optional< Object > update = updList.stream().filter(o -> o.equals( x ) ).findFirst();
                if ( update.isPresent() ) {
                    updList.remove( update.get() );
                    break;
                }
            }

            deleteAll( key, delList );
        } );

        insertMap.keySet().forEach( key -> {
            insertAll( key, insertMap.get( key ) );
        } );

        updateMap.keySet().forEach( key -> {
            updateAll( key, updateMap.get( key ) );
        } );

        deleteMap.clear();
        updateMap.clear();
        insertMap.clear();
    }
}
