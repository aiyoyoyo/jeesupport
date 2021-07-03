package com.jees.server.interf;

/**
 * Socket服务器启动和关闭服务
 * 
 * @author aiyoyoyo
 *
 */
public interface ISocketServer extends IServerBase{
    String	SCOPE_CREATOR		= "prototype";

    /**
     * 服务加载
     * @param _websocket 是否是websocket
     */
    void onload( boolean _websocket );
}
