package com.jees.server.abs;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.server.interf.IChannelHandler;
import com.jees.server.interf.IConnectorService;
import com.jees.server.interf.IServerService;
import com.jees.server.interf.ISocketServer;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;

/**
 * 可以用作应用的入口。通过加载IServerService的onload方法，激活相关内容的服务。
 */
@Lazy
@Log4j2
public abstract class AbsServerService implements IServerService {
    @Override
    public void onload() {
        log.debug("--服务器组件加载...");
        _check_support_service();
        log.debug("--服务器组件加载完毕");
    }

    @Override
    public void unload() {
    }

    @Override
    public void reload() {
    }

    private void _check_support_service() {
        boolean socket_server_enable = CommonConfig.getBoolean("jees.jsts.socket.enable", false);
        boolean websocket_server_enable = CommonConfig.getBoolean("jees.jsts.websocket.enable", false);
        boolean connector_enable = CommonConfig.getBoolean("jees.jsts.connector.enable", false);

        if (socket_server_enable) {
            CommonContextHolder.getBean(ISocketServer.class).onload(false);
            log.debug("--服务器支持[Socket]已加载.");
        }
        if (websocket_server_enable) {
            CommonContextHolder.getBean(ISocketServer.class).onload(true);
            log.debug("--服务器支持[WebSocket]已加载.");
        }
        if (connector_enable) {
            CommonContextHolder.getBean(IConnectorService.class).onload();
            log.debug("--服务器支持[连接器]已加载.");
        }
        CommonContextHolder.getBean(IChannelHandler.class).onload();
    }
}
