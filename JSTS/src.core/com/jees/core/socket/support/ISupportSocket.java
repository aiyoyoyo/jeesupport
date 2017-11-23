package com.jees.core.socket.support;

/**
 * Socket服务器启动和关闭服务
 * 
 * @author aiyoyoyo
 *
 */
public interface ISupportSocket {

	public String	SOCKET_PORT0	= "socket.port0";
	public String	SOCKET_HOST0	= "socket.host0";
	public String	SOCKET_PORT1	= "socket.port1";
	public String	SOCKET_SUPER	= "supportSocket";
	/**
	 * 服务加载
	 */
	void onload();

	/**
	 * 服务关闭
	 */
	void unload();
	
	/**
	 * 服务重启
	 */
	void reload();
}
