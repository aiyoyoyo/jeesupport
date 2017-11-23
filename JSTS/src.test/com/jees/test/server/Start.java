package com.jees.test.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jees.core.socket.common.CommonContextHolder;
import com.jees.core.socket.common.ICommonConfig;
import com.jees.core.socket.support.ISupportSocket;

public class Start {

	@SuppressWarnings( "resource" )
	public static void main( String[] args ) {
		new ClassPathXmlApplicationContext( ICommonConfig.CFG_DEFAULT ) ;
		((ISupportSocket) CommonContextHolder.getBean( ISupportSocket.SOCKET_SUPER ) ).onload();
	}
}
