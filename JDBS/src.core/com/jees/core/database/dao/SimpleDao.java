package com.jees.core.database.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jees.core.database.support.AbsDao;

@Service
public class SimpleDao {
	@Autowired
	AbsDao										dao;
	// 一些常用的SQL查询
	
	/**
	 * 根据某个属性，查询某张表的最新分组数据，使用标准的SQL写法，表名、字段名与数据库一致：TableName->table_name, timeProp->time_prop
	 * @param _dbName
	 * @param _cls
	 * @param _time
	 * @param _group
	 * @param _limit
	 * @return
	 */
	public < T > List< T > selectBy_News(String _dbName, Class<T> _cls, String _table, String _time, String _group, int _limit ){
		String sql = "SELECT * FROM ( SELECT * FROM " + _table +" ORDER BY " + _time + " DESC ) AS temp GROUP BY " + _group + " ORDER BY " + _time +" DESC LIMIT " + _limit;
		return dao.selectBySQL( _dbName , sql , new Object[]{} , _cls );
	}
	
	/**
	 * 根据某个字段集合查询某张表
	 * 
	 * @param _dbName
	 * @param _hql
	 * @param _first
	 * @param _limit
	 * @param _param
	 * @return java.util.List
	 */
	@SuppressWarnings ( "unchecked" )
	public < T > List< T > selectBy_In( String _dbName , Class< T > _cls, String _property, Object[] _param ) {
		Session sess = null;
		try {
			sess = dao.getSession( _dbName );
			return sess.createCriteria( _cls ).add( Restrictions.in( _property , _param ) ).list();
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( sess != null ) sess.close();
		}
	}
}
