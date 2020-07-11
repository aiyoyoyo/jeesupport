package com.jees.server.interf;

/**
 * Socket服务器启动和关闭服务
 * 
 * @author aiyoyoyo
 *
 */
public interface ISocketServer extends IServerBase{
    String	SCOPE_CREATOR		= "prototype";

    /** 服务加载 **/
    void onload( boolean _websocket );
}
