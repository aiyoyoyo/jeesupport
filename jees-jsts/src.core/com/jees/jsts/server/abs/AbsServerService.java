package com.jees.jsts.server.abs;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.core.socket.support.ISupportSocket;
import com.jees.core.socket.support.ISupportWebSocket;
import com.jees.jsts.server.interf.IConnectorService;
import com.jees.jsts.server.interf.IServerService;
import lombok.extern.log4j.Log4j2;

/**
 * 可以用作应用的入口。通过加载IServerService的onload方法，激活相关内容的服务。
 */
@Log4j2
public abstract class AbsServerService implements IServerService {
    @Override
    public void onload() {
        log.debug( "--服务器组件加载..." );

        _check_support_service();

        log.debug( "--服务器组件加载完毕" );
    }

    @Override
    public void unload() {
    }

    @Override
    public void reload() {
    }

    private void _check_support_service(){
        if(CommonConfig.getBoolean( Netty_Socket_Enable, true ) ){
            CommonContextHolder.getBean( ISupportSocket.class ).onload();
            log.debug( "--服务器支持[Socket]已加载." );
        }
        if(CommonConfig.getBoolean( Netty_WebSocket_Enable, true ) ) {
            CommonContextHolder.getBean(ISupportWebSocket.class).onload();
            log.debug("--服务器支持[WebSocket]已加载.");
        }
        if(CommonConfig.getBoolean( Netty_Connector_Enable, true ) ) {
            CommonContextHolder.getBean(IConnectorService.class).onload();
            log.debug("--服务器支持[连接器]已加载.");
        }
    }
}
