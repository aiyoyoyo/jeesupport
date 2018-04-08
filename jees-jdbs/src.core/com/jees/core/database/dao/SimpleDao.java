package com.jees.core.database.dao;

import com.jees.core.database.support.AbsSupportDao;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

public class SimpleDao extends AbsSupportDao {
	// 一些常用的SQL查询

	/**
	 * 根据某个属性，查询某张表的最新分组数据，使用标准的SQL写法，表名、字段名与数据库一致：TableName->table_name,
	 * timeProp->time_prop
	 * 
	 * @param _db
	 * @param _cls
	 * @param _time 时间字段名
	 * @param _group 分组字段名
	 * @param _limit
	 * @return
	 */
	public < T > List< T > selectBy_News( String _db , Class< T > _cls , String _table , String _time ,
			String _group , int _limit ) {
		String sql = "SELECT * FROM ( SELECT * FROM " + _table + " ORDER BY " + _time + " DESC ) AS temp GROUP BY "
				+ _group + " ORDER BY " + _time + " DESC LIMIT " + _limit;
		return selectBySQL( _db , sql , null, null , _cls );
	}

	/**
	 * 根据某个字段集合查询某张表
	 * 
	 * @param _db
	 * @param _cls
	 * @param _column
	 * @param _param
	 * @return java.util.List
	 */
	@SuppressWarnings( "unchecked" )
	@Deprecated
	public < T > List< T > selectBy_In( String _db , Class< T > _cls , String _column , Object[] _param ) {
		return _get_session( _db ).createCriteria( _cls ).add( Restrictions.in( _column , _param ) ).list();
	}
}
