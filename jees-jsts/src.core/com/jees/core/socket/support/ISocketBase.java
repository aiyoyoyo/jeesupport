package com.jees.core.socket.support;

public interface ISocketBase {
    /** 服务加载 **/
    void onload();

    /** 服务关闭 **/
    void unload();

    /** 服务重启 **/
    void reload();

    String Netty_Socket_Port        = "netty.socket.port";
    String Netty_Socket_LostTime    = "netty.socket.lostTime";
    String Netty_WebSocket_Port     = "netty.websocket.port";
    String Netty_WebSocket_LostTime = "netty.websocket.lostTime";
}
