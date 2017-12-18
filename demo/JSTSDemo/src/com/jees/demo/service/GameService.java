package com.jees.demo.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jees.core.socket.common.CommonContextHolder;
import com.jees.core.socket.common.ICommonConfig;
import com.jees.core.socket.support.ISupportSocket;

public class GameService {
	@SuppressWarnings( "resource" )
	public static void main( String[] args ) {
		new ClassPathXmlApplicationContext( ICommonConfig.CFG_DEFAULT ) ;
		CommonContextHolder.getBean( ISupportSocket.class ).onload();
	}
}
