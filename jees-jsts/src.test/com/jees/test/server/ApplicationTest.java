package com.jees.test.server;

import com.jees.common.CommonContextHolder;
import org.springframework.boot.SpringApplication;
import com.jees.core.socket.support.ISupportSocket;
import com.jees.core.socket.support.ISupportWebSocket;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.jees.*" })
public class ApplicationTest {

	@SuppressWarnings( "resource" )
	public static void main( String[] args ) {
		SpringApplication.run(ApplicationTest.class, args);

		CommonContextHolder.getBean( ISupportSocket.class ).onload();
		CommonContextHolder.getBean( ISupportWebSocket.class ).onload();
	}
}
