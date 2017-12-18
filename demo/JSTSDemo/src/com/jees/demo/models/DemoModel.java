package com.jees.demo.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jees.demo.entity.TabA;
import com.jees.demo.service.DaoService;

import io.netty.buffer.ByteBuf;

@Component
public class DemoModel {
	
	@Autowired
	DaoService			daoService;
	/**
	 * 假设这里是处理登陆等命令，加入@Transactional保证事务生效
	 * @param _request
	 * @param _response
	 */
	@Transactional
	public void requestLogin( ByteBuf _request, ByteBuf _response ) {
		// 如果需要字符串，建议使用ProtoBuff，否则需要知道截取字符串的长度，参考如下：
		//int len = _request.readInt();
		//byte[] dst = new byte[len];
		//_request.readBytes( dst );
		//String account = new String( dst );
		//下面我仅作示意代码来当作用户登陆
		
		int id = _request.readInt();
		
		TabA a = daoService.selectById( TabA.class , id );
		
		_response.writeInt( a == null ? -1 : a.getId() );
	}
	
	/**
	 * 假设这里处理注册
	 * @param _request
	 * @param _response
	 */
	@Transactional
	public void requestRegist( ByteBuf _request, ByteBuf _response ){
		TabA a = new TabA();
		daoService.insert( a );
		_response.writeInt( a.getId() );
	}
}
