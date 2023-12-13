package com.jees.core.database.support;

import com.jees.common.CommonConfig;
import com.jees.tool.utils.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;

import javax.persistence.Table;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

/**
 * 实现了标准查询内容，根据需要可以继承后扩展数据操作，事务实现通过上层业务类加入注解：org.
 * springframework.transaction.annotation.Transactional 来管理。
 *
 * @author aiyoyoyo
 */
@Log4j2
public abstract class AbsSupportDao implements ISupportDao {
    public static final int DEFAULT_LIMIT = 100;
    /**
     * 从配置文件中获取数据库链接对象。
     **/
    private static Map<String, SessionFactory> sessionFactoryMap = new HashMap<>();

    private void setSessionFactoryMap(Map<String, SessionFactory> sessionFactoryMap) {
        AbsSupportDao.sessionFactoryMap = sessionFactoryMap;
    }

    String defaultDB;

    @Override
    public String getDefaultDB() {
        if (defaultDB == null) {
            String[] db_names = CommonConfig.getArray("jees.jdbs.dataSources", String.class);
            if (db_names.length == 0 || db_names[0].isEmpty()) {
                defaultDB = "default";
            } else {
                defaultDB = db_names[0];
            }
        }
        return defaultDB;
    }

    private void _set_parameter(Query _query, String[] _param, Object[] _value) {
        if (_param != null && _value != null) {
            for (int i = 0; i < _param.length; i++) {
                if (_value[i] instanceof Collection) {
                    _query.setParameterList(_param[i], (Collection<?>) _value[i]);
                } else {
                    _query.setParameter(_param[i], _value[i]);
                }
            }
        }
    }

    private void _set_parameter(Query _query, Map _param) {
        if (_param != null) {
            for (Object key : _param.keySet()) {
                Object value = _param.get(key);
                if (value instanceof Collection) {
                    _query.setParameterList(key.toString(), (Collection<?>) value);
                } else {
                    _query.setParameter(key.toString(), value);
                }
            }
        }
    }

    protected Session _open_session(String _db) {
        if (sessionFactoryMap.containsKey(_db)) {
            SessionFactory sessionFactory = sessionFactoryMap.get(_db);
            return sessionFactory.openSession();
        }

        throw new NullPointerException("没有找到数据库[" + _db + "]的对应Session容器，请检查配置文件中是否正确。");
    }

    protected Session _get_session(String _db) {
        if (sessionFactoryMap.containsKey(_db)) {
            SessionFactory sessionFactory = sessionFactoryMap.get(_db);
            try {
                Session sess = sessionFactory.getCurrentSession();
                return sess;
            } catch (RuntimeException e) {
                e.printStackTrace();
                log.error("获取连接对象失败：" + e.getMessage());
            }
        }
        throw new NullPointerException("没有找到数据库[" + _db + "]的对应Session容器，请检查配置文件中是否正确。");
    }

    protected void _flush_session(Session _session) {
        if (_session == null) {
            return;
        }
        _session.flush();
        _session.clear();
    }

    protected void _close_session(Session _session) {
        if (_session == null) {
            return;
        }
        _session.close();
    }

    @Override
    public void putSessionFactory(String _db, SessionFactory _sf) {
        sessionFactoryMap.put(_db, _sf);
    }

    @Override
    public SessionFactory getSessionFactory(String _db) {
        return sessionFactoryMap.get(_db);
    }

    // insert /////////////////////////////////////////////////////////////
    @Override
    public void insert(String _db, Object _entity) {
        Session session = _get_session(_db);
        session.save(_entity);
    }

    @Override
    public <T> void insertAll(String _db, List<T> _list) {
        insertAll(_db, _list, DEFAULT_FLUSH);
    }

    @Override
    public <T> void insertAll(String _db, List<T> _list, int _flush) {
        Session session = _get_session(_db);

        int exnum = 0;
        int monum = _flush - 1;

        for (T e : _list) {
            session.save(e);
            if (++exnum % _flush == monum) {
                _flush_session(session);
            }
        }

        _flush_session(session);
    }

    @Override
    public void saveOrUpdate(String _db, Object _entity){
        Session session = _get_session(_db);
        session.saveOrUpdate(_entity);
    }

    @Override
    public <T> void saveOrUpdateAll(String _db, List<T> _list) {
        saveOrUpdateAll(_db, _list, DEFAULT_FLUSH);
    }

    @Override
    public <T> void saveOrUpdateAll(String _db, List<T> _list, int _flush) {
        Session session = _get_session(_db);

        int exnum = 0;
        int monum = _flush - 1;

        for (T e : _list) {
            session.saveOrUpdate(e);
            if (++exnum % _flush == monum) {
                _flush_session(session);
            }
        }

        _flush_session(session);
    }
    // delete //////////////////////////////////////////////////////////////
    @Override
    public void delete(String _db, Object _entity) {
        Session session = _get_session(_db);
        session.delete(_entity);
    }

    @Override
    public <T> void deleteAll(String _db, List<T> _list) {
        deleteAll(_db, _list, DEFAULT_FLUSH);
    }

    @Override
    public <T> void deleteAll(String _db, List<T> _list, int _flush) {
        Session session = _get_session(_db);

        int exnum = 0;
        int monum = _flush - 1;

        for (T e : _list) {
            session.delete(e);
            if (++exnum % _flush == monum) {
                _flush_session(session);
            }
        }

        _flush_session(session);
    }

    // update ///////////////////////////////////////////////////////////////
    @Override
    public void update(String _db, Object _entity) {
        Session ses = _get_session(_db);
        ses.merge(_entity);
    }

    @Override
    public <T> void updateAll(String _db, List<T> _list) {
        updateAll(_db, _list, DEFAULT_FLUSH);
    }

    @Override
    public <T> void updateAll(String _db, List<T> _list, int _flush) {
        Session session = _get_session(_db);

        int exnum = 0;
        int monum = _flush - 1;

        for (T e : _list) {
            session.merge(e);
            if (++exnum % _flush == monum) {
                _flush_session(session);
            }
        }

        _flush_session(session);
    }

    // select /////////////////////////////////////////////////////////////////

    @Override
    public <T> List<T> select(String _db, Class<T> _cls) {
        return select(_db, _cls, DEFAULT_LIMIT);
    }

    @Override
    public <T> List<T> select(String _db, Class<T> _cls, int _limit) {
        return select(_db, _cls, DEFAULT_FIRST, _limit);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> select(String _db, Class<T> _cls, int _first, int _limit) {
        Session session = _get_session(_db);
        CriteriaQuery<T> criteria = session.getCriteriaBuilder().createQuery(_cls);
        criteria.select(criteria.from(_cls));
        List<T> results = session.createQuery(criteria)
                .setFirstResult(_first).setMaxResults(_limit).getResultList();
        return results;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T selectById(String _db, Class<T> _cls, Serializable _id) {
        Session session = _get_session(_db);
        return session.get(_cls, _id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> selectByHQL(String _db, String _hql, String[] _param, Object[] _value, Class<T> _cls) {
        return selectByHQL(_db, _hql, DEFAULT_FIRST, DEFAULT_LIMIT, _param, _value, _cls);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> selectByHQL(String _db, String _hql, int _first, int _limit, String[] _param, Object[] _value,
                                   Class<T> _cls) {
        Session session = _get_session(_db);
        Query<T> query = session.createQuery(_hql);
        query.setFirstResult(_first);
        query.setMaxResults(_limit);

        _set_parameter(query, _param, _value);
        return query.getResultList();
    }

    @Override
    public <T> List<T> selectByHQL(String _db, String _hql, Map _param, Class<T> _cls) {
        return selectByHQL(_db, _hql, DEFAULT_FIRST, DEFAULT_LIMIT, _param, _cls);
    }

    @Override
    public <T> List<T> selectByHQL(String _db, String _hql, int _first, int _limit, Map _param, Class<T> _cls) {
        Session session = _get_session(_db);
        Query<T> query = session.createQuery(_hql);
        query.setFirstResult(_first);
        query.setMaxResults(_limit);

        _set_parameter(query, _param);
        if (_cls == null) {
            query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        }
        return query.getResultList();
    }

    @Override
    public <T> List<T> selectBySQL(String _db, String _sql, String[] _param, Object[] _value, Class<T> _cls) {
        return selectBySQL(_db, _sql, DEFAULT_FIRST, DEFAULT_LIMIT, _param, _value, _cls);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> selectBySQL(String _db, String _sql, int _first, int _limit, String[] _param, Object[] _value,
                                   Class<T> _cls) {
        Session session = _get_session(_db);

        Query<T> query;
        if (_cls == null) {
            query = session.createNativeQuery(_sql);
            query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        } else {
            query = session.createNativeQuery(_sql, _cls);
        }

        query.setFirstResult(_first);
        query.setMaxResults(_limit);

        _set_parameter(query, _param, _value);

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> selectBySQL(String _db, String _sql, Map _param, Class<T> _cls) {
        return selectBySQL(_db, _sql, DEFAULT_FIRST, DEFAULT_LIMIT, _param, _cls);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> selectBySQL(String _db, String _sql, int _first, int _limit, Map _param,
                                   Class<T> _cls) {
        Session session = _get_session(_db);
        Query<T> query;
        if (_cls == null) {
            query = session.createNativeQuery(_sql);
            query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        } else {
            query = session.createNativeQuery(_sql, _cls);
        }

        query.setFirstResult(_first);
        query.setMaxResults(_limit);

        _set_parameter(query, _param);
        return query.getResultList();
    }

    @Override
    public <T> long selectCount(String _db, Class<T> _cls) {
        String hql = "SELECT COUNT(*) FROM ";
        Query query = _get_session(_db).createQuery(hql + _cls.getName());
        return query.getResultList().size() > 0 ? (long) query.getResultList().get(0) : 0;
    }

    @Override
    public long selectCountBySQL(String _db, String _sql, String[] _param, Object[] _value) {
        Query query = _get_session(_db).createNativeQuery(_sql);
        _set_parameter(query, _param, _value);
        return query.getResultList().size() > 0 ? ((BigInteger) query.getResultList().get(0)).intValue() : 0;
    }

    @Override
    public long selectCountByHQL(String _db, String _hql, String[] _param, Object[] _value) {
        Query query = _get_session(_db).createQuery(_hql);
        _set_parameter(query, _param, _value);
        return query.getResultList().size() > 0 ? (long) query.getResultList().get(0) : 0;
    }

    @Override
    public int executeByHQL(String _db, String _hql, String[] _param, Object[] _value) {
        Session session = _get_session(_db);
        try {
            Query query = _get_session(_db).createQuery(_hql);
            _set_parameter(query, _param, _value);
            return query.executeUpdate();
        } finally {
            _flush_session(session);
        }
    }

    @Override
    public int executeBySQL(String _db, String _sql, String[] _param, Object[] _value) {
        Session session = _get_session(_db);
        try {
            Query query = _get_session(_db).createNativeQuery(_sql);
            _set_parameter(query, _param, _value);
            return query.executeUpdate();
        } finally {
            _flush_session(session);
        }
    }

    @Override
    public int executeBySQL(String _db, String _sql, Map _data) {
        Session session = _get_session(_db);
        try {
            Query query = _get_session(_db).createNativeQuery(_sql);
            _set_parameter(query, _data);
            return query.executeUpdate();
        } finally {
            _flush_session(session);
        }
    }


    @Override
    public void insert(Object _entity) {
        this.insert(getDefaultDB(), _entity);
    }

    @Override
    public <T> void insertAll(List<T> _list) {
        this.insertAll(getDefaultDB(), _list);
    }

    @Override
    public <T> void insertAll(List<T> _list, int _flush) {
        this.insertAll(getDefaultDB(), _list, _flush);
    }

    @Override
    public void delete(Object _entity) {
        this.delete(getDefaultDB(), _entity);
    }

    @Override
    public <T> void deleteAll(List<T> _list) {
        this.deleteAll(getDefaultDB(), _list);
    }

    @Override
    public <T> void deleteAll(List<T> _list, int _flush) {
        this.deleteAll(getDefaultDB(), _list, _flush);
    }

    @Override
    public void update(Object _entity) {
        this.update(getDefaultDB(), _entity);
    }

    @Override
    public <T> void updateAll(List<T> _list) {
        this.updateAll(getDefaultDB(), _list);
    }

    @Override
    public <T> void updateAll(List<T> _list, int _flush) {
        this.updateAll(getDefaultDB(), _list, _flush);
    }

    @Override
    public <T> List<T> select(Class<T> _cls) {
        return this.select(getDefaultDB(), _cls);
    }

    @Override
    public <T> List<T> select(Class<T> _cls, int _limit) {
        return this.select(getDefaultDB(), _cls, _limit);
    }

    @Override
    public <T> List<T> select(Class<T> _cls, int _first, int _limit) {
        return this.select(getDefaultDB(), _cls, _first, _limit);
    }

    @Override
    public <T> T selectById(Class<T> _cls, Serializable _id) {
        return this.selectById(getDefaultDB(), _cls, _id);
    }

    @Override
    public <T> List<T> selectByHQL(String _hql, String[] _param, Object[] _value, Class<T> _cls) {
        return this.selectByHQL(getDefaultDB(), _hql, _param, _value, _cls);
    }

    @Override
    public <T> List<T> selectByHQL(String _hql, int _first, int _limit, String[] _param, Object[] _value, Class<T> _cls) {
        return this.selectByHQL(getDefaultDB(), _hql, _first, _limit, _param, _value, _cls);
    }

    @Override
    public <T> List<T> selectByHQL(String _hql, Map _param, Class<T> _cls) {
        return this.selectByHQL(getDefaultDB(), _hql, _param, _cls);
    }

    @Override
    public <T> List<T> selectByHQL(String _hql, int _first, int _limit, Map _param, Class<T> _cls) {
        return this.selectByHQL(getDefaultDB(), _hql, _first, _limit, _param, _cls);
    }

    @Override
    public <T> List<T> selectBySQL(String _sql, String[] _param, Object[] _value, Class<T> _cls) {
        return this.selectBySQL(getDefaultDB(), _sql, _param, _value, _cls);
    }

    @Override
    public <T> List<T> selectBySQL(String _sql, int _first, int _limit, String[] _param, Object[] _value, Class<T> _cls) {
        return this.selectBySQL(getDefaultDB(), _sql, _first, _limit, _param, _value, _cls);
    }

    @Override
    public <T> List<T> selectBySQL(String _sql, Map _param, Class<T> _cls) {
        return this.selectBySQL(getDefaultDB(), _sql, _param, _cls);
    }

    @Override
    public <T> List<T> selectBySQL(String _sql, int _first, int _limit, Map _param, Class<T> _cls) {
        return this.selectBySQL(getDefaultDB(), _sql, _first, _limit, _param, _cls);
    }

    @Override
    public <T> long selectCount(Class<T> _cls) {
        return this.selectCount(getDefaultDB(), _cls);
    }

    @Override
    public long selectCountByHQL(String _hql, String[] _param, Object[] _value) {
        return this.selectCountByHQL(getDefaultDB(), _hql, _param, _value);
    }

    @Override
    public long selectCountBySQL(String _sql, String[] _param, Object[] _value) {
        return this.selectCountBySQL(getDefaultDB(), _sql, _param, _value);
    }

    @Override
    public int executeByHQL(String _hql, String[] _param, Object[] _value) {
        return this.executeByHQL(getDefaultDB(), _hql, _param, _value);
    }

    @Override
    public int executeBySQL(String _sql, String[] _param, Object[] _value) {
        return this.executeBySQL(getDefaultDB(), _sql, _param, _value);
    }

    @Override
    public int executeBySQL(String _sql, Map _data) {
        return this.executeBySQL(getDefaultDB(), _sql, _data);
    }

    // 基于Map的查询 //////////////////////////////////////////////////////////
    @Override
    public <T> List<T> selectByMap(String _db, String _table, Map _param) {
        return this.selectByMap(_db, _table, _param, DEFAULT_FIRST, DEFAULT_LIMIT);
    }

    @Override
    public <T> List<T> selectByMap(String _db, String _table, Map _param, int _offset, int _limit) {
        return this.selectByMap(_db, _table, null, _param, _offset, _limit, null);
    }

    @Override
    public <T> List<T> selectByMap(String _db, Object _tableOrParam, Class<T> _cls) {
        return this.selectByMap(_db, _tableOrParam, DEFAULT_FIRST, DEFAULT_LIMIT, _cls);
    }

    @Override
    public <T> List<T> selectByMap(String _db, Object _tableOrParam, int _offset, int _limit, Class<T> _cls) {
        if (_tableOrParam == null || _tableOrParam instanceof Map) {
            String table = ((Table) ((Class) _cls).getAnnotation(Table.class)).name();
            Map param = null;
            if (_tableOrParam instanceof Map) {
                param = (Map) _tableOrParam;
            }
            return this.selectByMap(_db, table, null, param, _offset, _limit, _cls);
        } else {
            return this.selectByMap(_db, _tableOrParam.toString(), null, null, _offset, _limit, _cls);
        }
    }

    /**
     * 基于Map的基础查询，此处仅表示基础用法，扩展用法需要自己实现
     *
     * @param _db
     * @param _table
     * @param _column
     * @param _param
     * @param _offset
     * @param _limit
     * @param _cls
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> selectByMap(String _db, String _table, Set<String> _column,
                                   Map _param, int _offset, int _limit, Class<T> _cls) {

        String schema = CommonConfig.get( "jees.jdbs.config." + _db + ".schema", _db );
        // jees.jdbs.config.testa.dbtype = mysql //不同数据库会有不同语法情况
        // jees.jdbs.config.testa.orm = hibernate //可能增加mybatis支持
        String db = schema + ".";
        String type = CommonConfig.get("jees.jdbs.config." + _db + ".dbtype", "mysql").toLowerCase();
        if (type.equalsIgnoreCase("sqlite")) {
            db = "";
        }

        String sql = "SELECT ";
        // 生成column字段
        if (_column != null && _column.size() > 0) {
            for (String col : _column) {
                sql += col + ",";
            }
            sql = sql.substring(0, sql.length() - 1);
        } else {
            sql += "*";
        }
        sql += " FROM " + db + _table + " WHERE 1=1 ";

        // 生成 where
        if (_param != null && _param.size() > 0) {
            Set<String> keys = _param.keySet();
            for (String key : keys) {
                if( key.equalsIgnoreCase("orderBy") ) continue;
                if( key.equalsIgnoreCase("groupBy") ) continue;
                // 生成where的值
                Object value = _param.get(key);
                if (value != null) {
                    // 不判断 列是否存在, 仅排除特殊字段
                    String sql_value = "";
                    // 仅支持简单条件，以防字符串拼接注入
                    if (value instanceof String) {
                        String tmp_val = ((String) value).trim();
                        // 特殊字符开头的处理
                        if (tmp_val.startsWith("%") || tmp_val.endsWith("%") || tmp_val.startsWith("!%")) {
                            if (tmp_val.startsWith("!%")) {
                                tmp_val = " NOT LIKE " + tmp_val;
                            } else {
                                if (tmp_val.startsWith("'") && tmp_val.endsWith("'")) {
                                    tmp_val = " LIKE " + tmp_val;
                                } else {
                                    tmp_val = " LIKE '" + tmp_val + "'";
                                }
                            }
                        } else if (tmp_val.startsWith(">") || tmp_val.startsWith("<") || tmp_val.startsWith("=") || tmp_val.startsWith("!=")
                                || tmp_val.toLowerCase().startsWith("not")) {
                            // TODO 不做处理直接拼接，但是需要判定结尾是否合法 防止sql注入
                            // not like 要重新
                            // > >= < <= <> = 都需要限定是数字或者时间
                        } else {
                            // TODO 处理字符串中的特殊符号 # ' " 等
                            tmp_val = " = '" + tmp_val + "'";
                        }
                        sql_value = tmp_val;
                    } else if (value instanceof List || value instanceof Set || value.getClass().isArray()) {
                        List<Object> list = toList(value);
                        if (list.isEmpty()) {
                        } else {
                            sql_value += " IN (";
                            String tmp_o = "";
                            for (Object o : list) {
                                if (o == null) {
                                    continue;
                                }
                                if (o instanceof String) {
                                    tmp_o += "'" + o + "',";
                                } else if (o instanceof Integer || o instanceof Double
                                        || o instanceof Float || o instanceof Boolean) {
                                    tmp_o += o + ",";
                                } else {
                                    log.warn("--未支持的数据类型：" + o);
                                }
                            }
                            sql_value += tmp_o.substring(0, tmp_o.length() - 1) + ")";
                        }
                    } else {
                        // 仅限整型和布尔型
                        sql_value = " = " + value;
                    }

                    // 这里仅列出  type == mysql;
                    if (!sql_value.trim().isEmpty()) {
                        switch (type) {
                            case "sqlite":
                                sql += " AND " + key + sql_value;
                                break;
                            case "mysql":
                            default:
                                sql += " AND " + "`" + key + "`" + sql_value;
                                break;
                        }
                    }
                } else {
                    // 这里可能有2种情况，一种是查 is null 一种是不查，这里取不查
                }
            }
            String groupBy = (String) _param.get("groupBy");
            if (StringUtil.isNotEmpty(groupBy)) {
                sql += " GROUP BY " + groupBy;
            }
            // 生成 order by
            String order_by = (String) _param.get("orderBy");
            if (StringUtil.isNotEmpty(order_by)) {
                sql += " ORDER BY " + order_by;
            }
        }

        log.debug("生成查询语句：" + sql);
        // TODO sql的替换字符串拼接
        return this.selectBySQL(_db, sql, _offset, _limit, new String[]{}, new String[]{}, _cls);
    }

    public static List<Object> toList(Object val) {
        List<Object> list = new ArrayList();
        if (val instanceof String[]) {
            String[] arr = (String[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof Integer[]) {
            Integer[] arr = (Integer[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof int[]) {
            int[] arr = (int[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof Long[]) {
            Long[] arr = (Long[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof long[]) {
            long[] arr = (long[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof Float[]) {
            Float[] arr = (Float[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof float[]) {
            float[] arr = (float[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof Double[]) {
            Double[] arr = (Double[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof double[]) {
            double[] arr = (double[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof Boolean[]) {
            Boolean[] arr = (Boolean[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof boolean[]) {
            int[] arr = (int[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof Byte[]) {
            Byte[] arr = (Byte[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof byte[]) {
            byte[] arr = (byte[]) val;
            Collections.addAll(list, arr);
        } else if (val instanceof List || val instanceof ArrayList) {
            list = (List<Object>) val;
        } else if (val instanceof Set || val instanceof HashSet) {
            Set tmps = (Set) val;
            list.addAll(tmps);
        } else {
            throw new RuntimeException("can't add to Array");
        }
        return list;
    }
}
