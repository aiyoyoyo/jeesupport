package com.jees.core.database.dao;

import com.jees.core.database.support.AbsSupportDao;
import org.springframework.stereotype.Component;

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
            list.addAll( ( Collection< ? > ) _obj );
        else
            list.add( _obj );

        _map.put( _db, list );
    }

    /**
     * 需要通过调用commit()方法，正式提交到数据库
     * @param _db 数据库标识
     * @param _obj 映射类
     */
    public void insert( String _db, Object _obj ){
        _push( _db, _obj, insertMap );
    }

    /**
     * 需要通过调用commit()方法，正式提交到数据库
     * @param _db 数据库标识
     * @param _obj 映射类
     */
    public void update( String _db, Object _obj ){
        _push( _db, _obj, updateMap );
    }

    /**
     * 需要通过调用commit()方法，正式提交到数据库
     * @param _db 数据库标识
     * @param _obj 映射类
     */
    public void delete( String _db, Object _obj ){
        _push( _db, _obj, deleteMap );
    }

    /**
     * 正式提交到数据库，该方法会剔除insert/update数据中已经被声明在delete中的重复对象
     */
    public void commit(){
        deleteMap.keySet().forEach( key -> {
            List<Object> delList = deleteMap.getOrDefault( key, new ArrayList<>() );
            List<Object> updList = updateMap.getOrDefault( key, new ArrayList<>() );
            List<Object> insList = insertMap.getOrDefault( key, new ArrayList<>() );

            Iterator< Object > it = delList.iterator();

            while ( it.hasNext() ) {
                Object x = it.next();
                Optional< Object > insert = insList.stream().filter(o -> o.equals( x ) ).findFirst();
                if ( insert.isPresent() ) {
                    insList.remove(insert.get());
                    break;
                }
                Optional< Object > update = updList.stream().filter(o -> o.equals( x ) ).findFirst();
                if ( update.isPresent() ) {
                    updList.remove( update.get() );
                    break;
                }
            }

            deleteAll( key, delList );
        } );

        insertMap.keySet().forEach( key -> insertAll( key, insertMap.getOrDefault( key, new ArrayList<>() ) ));

        updateMap.keySet().forEach( key -> updateAll( key, updateMap.getOrDefault( key, new ArrayList<>() ) ));

        deleteMap.clear();
        updateMap.clear();
        insertMap.clear();
    }
}
