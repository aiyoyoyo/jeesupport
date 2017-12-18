package com.jees.demo.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import com.jees.core.database.support.AbsSupportDao;
@Service
public class DaoService extends AbsSupportDao {
	final static String		DB_DEFAULT	= "default";
	
	// Common 数据库通用方法 ==========================================
	// 调用AbsSupportDao的公共方法，也可以通过其他方式自己实现，例如
	// Session sess = _get_session( DB_DEFAULT );
	
	public void insert( Object _entity ) {
		insert( DB_DEFAULT , _entity );
	}
	
	public < T > T selectById( Class< T > _cls , Serializable _id ) {
		return selectById( DB_DEFAULT , _cls , _id );
	}
}
