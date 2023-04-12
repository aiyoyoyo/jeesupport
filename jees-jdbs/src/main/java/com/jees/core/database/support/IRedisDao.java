package com.jees.core.database.support;

import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.List;
import java.util.Map;

public interface IRedisDao<ID, T> {
    void initialize();

    void heartbeat();

    void heartbeat(int _idx);

    void database(int _idx);

    void changeDatabase(LettuceConnectionFactory _lcf);

    void insert(T _obj) throws Exception;

    void insert(int _idx, T _obj) throws Exception;

    void insertMap(Map<ID, T> _map, Class<T> _cls);

    void insertMap(int _idx, Map<ID, T> _map, Class<T> _cls);

    void update(T _obj);

    void update(int _idx, T _obj);

    void updateMap(Map<String, T> _map, Class<T> _cls);

    void updateMap(int _idx, Map<String, T> _map, Class<T> _cls);

    void delete(T _obj) throws Exception;

    void delete(int _idx, T _obj) throws Exception;

    void deleteList(List<T> _list, Class<T> _cls);

    void deleteList(int _idx, List<T> _list, Class<T> _cls);

    void deleteById(ID _id, Class<T> _cls);

    void deleteById(int _idx, ID _id, Class<T> _cls);

    T findById(ID _value, Class<T> _cls);

    T findById(int _idx, ID _value, Class<T> _cls);

    List<T> findAll(Class<T> _cls);

    List<T> findAll(int _idx, Class<T> _cls);

    Map<ID, T> findHashAll(Class<T> _cls);

    Map<ID, T> findHashAll(int _idx, Class<T> _cls);

    List<T> findByEquals(String _property, Object _value, Class<T> _cls);

    List<T> findByEquals(int _idx, String _property, Object _value, Class<T> _cls);

    List<T> findBetweens(String _property, Object _begin, Object _end, Class<T> _cls);

    List<T> findBetweens(int _idx, String _property, Object _begin, Object _end, Class<T> _cls);
}
