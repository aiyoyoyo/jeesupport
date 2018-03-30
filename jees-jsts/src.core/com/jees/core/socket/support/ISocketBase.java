package com.jees.core.socket.support;

public interface ISocketBase {
    /** 服务加载 **/
    void onload();

    /** 服务关闭 **/
    void unload();

    /** 服务重启 **/
    void reload();

    String Netty_Socket_Port        = "jees.jsts.socket.port";
    String Netty_Socket_LostTime    = "jees.jsts.socket.lostTime";
    String Netty_WebSocket_Port     = "jees.jsts.websocket.port";
    String Netty_WebSocket_LostTime = "jees.jsts.websocket.lostTime";
    String Netty_WebSocket_Url      = "jees.jsts.websocket.url";
}
