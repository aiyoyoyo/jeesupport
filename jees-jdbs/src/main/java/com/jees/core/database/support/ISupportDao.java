package com.jees.core.database.support;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 定义了标准数据库处理模型的接口
 *
 * @author aiyoyoyo
 */
public interface ISupportDao {
    int DEFAULT_LIMIT = 1000;
    int DEFAULT_FIRST = 0;
    int DEFAULT_FLUSH = 100;

    void putSessionFactory(String _db, SessionFactory _sf);

    SessionFactory getSessionFactory(String _db);

    void insert(String _db, Object _entity);

    <T> void insertAll(String _db, List<T> _list);

    <T> void insertAll(String _db, List<T> _list, int _flush);

    void saveOrUpdate(String _db, Object _entity);

    <T> void saveOrUpdateAll(String _db, List<T> _list);

    <T> void saveOrUpdateAll(String _db, List<T> _list, int _flush);

    void delete(String _db, Object _entity);

    <T> void deleteAll(String _db, List<T> _list);

    <T> void deleteAll(String _db, List<T> _list, int _flush);

    void update(String _db, Object _entity);

    <T> void updateAll(String _db, List<T> _list);

    <T> void updateAll(String _db, List<T> _list, int _flush);

    <T> List<T> select(String _db, Class<T> _cls);

    <T> List<T> select(String _db, Class<T> _cls, int _limit);

    <T> List<T> select(String _db, Class<T> _cls, int _first, int _limit);

    <T> T selectById(String _db, Class<T> _cls, Serializable _id);

    <T> List<T> selectByHQL(String _db, String _hql, String[] _param, Object[] _value, Class<T> _cls);

    <T> List<T> selectByHQL(String _db, String _hql, int _first, int _limit, String[] _param, Object[] _value,
                            Class<T> _cls);

    <T> List<T> selectByHQL(String _db, String _hql, Map _param, Class<T> _cls);

    <T> List<T> selectByHQL(String _db, String _hql, int _first, int _limit, Map _param, Class<T> _cls);

    <T> List<T> selectBySQL(String _db, String _sql, String[] _param, Object[] _value, Class<T> _cls);

    <T> List<T> selectBySQL(String _db, String _sql, int _first, int _limit, String[] _param, Object[] _value,
                            Class<T> _cls);

    <T> List<T> selectBySQL(String _db, String _sql, Map _param, Class<T> _cls);

    <T> List<T> selectBySQL(String _db, String _sql, int _first, int _limit, Map _param,
                            Class<T> _cls);

    <T> long selectCount(String _db, Class<T> _cls);

    long selectCountByHQL(String _db, String _hql, String[] _param, Object[] _value);

    long selectCountBySQL(String _db, String _sql, String[] _param, Object[] _value);

    int executeByHQL(String _db, String _hql, String[] _param, Object[] _value);

    int executeBySQL(String _db, String _sql, String[] _param, Object[] _value);

    int executeBySQL(String _db, String _sql, Map _data);

    void commit();

    String getDefaultDB();

    // 基于Map和默认数据库的操作

    void insert(Object _entity);

    <T> void insertAll(List<T> _list);

    <T> void insertAll(List<T> _list, int _flush);

    void delete(Object _entity);

    <T> void deleteAll(List<T> _list);

    <T> void deleteAll(List<T> _list, int _flush);

    void update(Object _entity);

    <T> void updateAll(List<T> _list);

    <T> void updateAll(List<T> _list, int _flush);

    <T> List<T> select(Class<T> _cls);

    <T> List<T> select(Class<T> _cls, int _limit);

    <T> List<T> select(Class<T> _cls, int _first, int _limit);

    <T> T selectById(Class<T> _cls, Serializable _id);

    <T> List<T> selectByHQL(String _hql, String[] _param, Object[] _value, Class<T> _cls);

    <T> List<T> selectByHQL(String _hql, int _first, int _limit, String[] _param, Object[] _value,
                            Class<T> _cls);

    <T> List<T> selectByHQL(String _hql, Map _param, Class<T> _cls);

    <T> List<T> selectByHQL(String _hql, int _first, int _limit, Map _param, Class<T> _cls);

    <T> List<T> selectBySQL(String _sql, String[] _param, Object[] _value, Class<T> _cls);

    <T> List<T> selectBySQL(String _sql, int _first, int _limit, String[] _param, Object[] _value,
                            Class<T> _cls);

    <T> List<T> selectBySQL(String _sql, Map _param, Class<T> _cls);

    <T> List<T> selectBySQL(String _sql, int _first, int _limit, Map _param,
                            Class<T> _cls);

    <T> long selectCount(Class<T> _cls);

    long selectCountByHQL(String _hql, String[] _param, Object[] _value);

    long selectCountBySQL(String _sql, String[] _param, Object[] _value);

    int executeByHQL(String _hql, String[] _param, Object[] _value);

    int executeBySQL(String _sql, String[] _param, Object[] _value);

    int executeBySQL(String _sql, Map _data);

    // 基于Map参数的查询接口
    // 结尾参数Class<T> _cls为null时，返回List<Map>
    <T> List<T> selectByMap(String _db, String _tbale, Map _param);

    <T> List<T> selectByMap(String _db, String _tbale, Map _param, int _offset, int _limit);

    <T> List<T> selectByMap(String _db, Object _tableOrParam, Class<T> _cls);

    <T> List<T> selectByMap(String _db, Object _tableOrParam, int _offset, int _limit, Class<T> _cls);

    <T> List<T> selectByMap(String _db, String _table, Set<String> _column, Map _param, int _offset, int _limit, Class<T> _cls);
}
