package com.jees.test.server;

import com.jees.common.CommonContextHolder;
import com.jees.server.abs.*;
import com.jees.server.interf.IConnectorService;
import com.jees.server.interf.IServerService;
import com.jees.server.interf.ISocketServer;
import com.jees.server.interf.ISuperUser;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;

@Log4j2
@ComponentScan("com.jees")
public class JstsApplication {

    public static void main(String[] args) {
        SpringApplication.run(JstsApplication.class, args);
        CommonContextHolder.getBean(IServerService.class).onload();
    }

    // 以下为自定义实现 ======
    @Bean
    public IServerService serverService() {
        return new AbsServerService() {
        };
    }

    @Bean
    public IConnectorService connectorService() {
        return new AbsConnectorService() {
        };
    }

    @Bean
    @Scope(value = ISocketServer.SCOPE_CREATOR)
    public AbsConnectorHandler connectroHandler() {
        return new AbsConnectorHandler() {
        };
    }

    @SuppressWarnings("rawtypes")
    @Bean
    @Scope(value = ISocketServer.SCOPE_CREATOR)
    public ISuperUser superuser() {
        return new AbsSuperUser<Long, ChannelHandlerContext>() {
            @Override
            public void switchover(ChannelHandlerContext _net) {

            }
        };
    }

    /**
     * 客户端时间执行器，需要自己实现相应的关联方法
     *
     * @return IChannelHandler
     */
    @SuppressWarnings("rawtypes")
    @Bean
    public AbsChannelHandler requestHandler() {
        return new AbsChannelHandler() {
            @Override
            public void error(ChannelHandlerContext _net, Throwable _thr) {
            }

            @Override
            public boolean before(ChannelHandlerContext _net, int _cmd) {
                return true;
            }

            @Override
            public void after(ChannelHandlerContext _net) {
            }

            @Override
            public void unregist(ChannelHandlerContext _net, Object _msg) {
            }
        };
    }
}
