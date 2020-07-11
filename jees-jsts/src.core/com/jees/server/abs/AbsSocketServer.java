package com.jees.server.abs;

import com.jees.server.interf.ISocketServer;
import org.springframework.stereotype.Repository;

/**
 * Socket服务器实现类，通过配置中实现ISupportSocket的实现类来决定Socket框架类型
 * <br/>
 * 目前支持项：
 * <br/>
 * Netty，实现类：com.jees.netty.support.NettyServer
 * 
 * @author aiyoyoyo
 *
 */
@Repository
public abstract class AbsSocketServer implements ISocketServer{
	protected boolean webSocket;

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.unload();
	}

	public void reload(){
		this.unload();
		this.onload( webSocket );
	}
}
