package com.jees.core.database.dao;

import com.jees.core.database.support.AbsSupportDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过缓存操作的形式，先记录要操作的数据（不包含查询），在需要的时候通过commit()提交。
 *
 * @author aiyoyoyo
 */
public class CacheDao extends AbsSupportDao {
    private Map<String, List<Object>> insertMap = new ConcurrentHashMap<>();
    private Map<String, List<Object>> deleteMap = new ConcurrentHashMap<>();
    private Map<String, List<Object>> updateMap = new ConcurrentHashMap<>();

    private void _push(String _db, Object _obj, Map<String, List<Object>> _map) {
        List<Object> list = _map.getOrDefault(_db, new ArrayList<>());

        boolean real_push = false;
        if (_obj instanceof Collection) {
            Collection<?> coll = (Collection<?>) _obj;
            list.forEach(o -> {
                if (coll.contains(o)) {
                    coll.remove(o);
                }
            });
            real_push = coll.size() > 0;
            if (real_push) {
                list.addAll(coll);
            }
        } else {
            if (_obj != null && !list.contains(_obj)) {
                list.add(_obj);
            }
            real_push = list.size() > 0;
        }

        if (real_push) {
            _map.put(_db, list);
        }
    }

    /**
     * 需要通过调用commit()方法，正式提交到数据库
     *
     * @param _db  数据库标识
     * @param _obj 映射类
     */
    @Override
    public void insert(String _db, Object _obj) {
        _push(_db, _obj, insertMap);
    }

    /**
     * 需要通过调用commit()方法，正式提交到数据库
     *
     * @param _db  数据库标识
     * @param _obj 映射类
     */
    @Override
    public void update(String _db, Object _obj) {
        _push(_db, _obj, updateMap);
    }

    /**
     * 需要通过调用commit()方法，正式提交到数据库
     *
     * @param _db  数据库标识
     * @param _obj 映射类
     */
    @Override
    public void delete(String _db, Object _obj) {
        _push(_db, _obj, deleteMap);
    }

    /**
     * 正式提交到数据库，该方法会剔除insert/update数据中已经被声明在delete中的重复对象
     */
    @Override
    @Autowired
    public void commit() {
        try {
            deleteMap.keySet().forEach(key -> {
                List<Object> delList = deleteMap.getOrDefault(key, new ArrayList<>());
                List<Object> updList = updateMap.getOrDefault(key, new ArrayList<>());
                List<Object> insList = insertMap.getOrDefault(key, new ArrayList<>());

                Iterator<Object> it = delList.iterator();

                while (it.hasNext()) {
                    Object x = it.next();
                    Optional<Object> insert = insList.stream().filter(o -> o.equals(x)).findFirst();
                    if (insert.isPresent()) {
                        insList.remove(insert.get());
                        break;
                    }
                    Optional<Object> update = updList.stream().filter(o -> o.equals(x)).findFirst();
                    if (update.isPresent()) {
                        updList.remove(update.get());
                        break;
                    }
                }

                deleteAll(key, delList);
            });

            insertMap.keySet().forEach(key -> {
                List<?> list = insertMap.getOrDefault(key, new ArrayList<>());
                Iterator<?> it = list.iterator();
                while (it.hasNext()) {
                    if (it.next() == null) {
                        it.remove();
                    }
                }
                if (list.size() > 0) {
                    insertAll(key, list);
                }
            });

            updateMap.keySet().forEach(key -> {
                List<?> list = updateMap.getOrDefault(key, new ArrayList<>());
                Iterator<?> it = list.iterator();
                while (it.hasNext()) {
                    if (it.next() == null) {
                        it.remove();
                    }
                }
                if (list.size() > 0) {
                    updateAll(key, list);
                }
            });

        } finally {
            deleteMap.clear();
            updateMap.clear();
            insertMap.clear();
        }
    }
}
