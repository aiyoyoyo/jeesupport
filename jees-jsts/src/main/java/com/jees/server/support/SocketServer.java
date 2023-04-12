package com.jees.server.support;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.server.abs.AbsSocketServer;
import com.jees.server.interf.ISocketServer;
import com.jees.server.socket.SocketInitializer;
import com.jees.server.websocket.WebSocketInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@Scope(value = ISocketServer.SCOPE_CREATOR)
public class SocketServer extends AbsSocketServer implements Runnable {
    private EventLoopGroup boss;
    private EventLoopGroup work;
    private int port;
    @Autowired
    private WebSocketInitializer websocketInitializer;

    public void run() {
        this.start();
    }

    @Override
    public void onload(boolean _ws) {
        this.webSocket = _ws;
        new Thread(this).start();
    }

    @Override
    public void onload() {
    }

    @Override
    public void unload() {
        log.debug("--WebSocket Server[" + port + "] 停止中...");
        if (boss != null) boss.shutdownGracefully();
        if (work != null) work.shutdownGracefully();
        log.debug("WebSocket Server[" + port + "] 已停止。");
    }

    @Override
    public void reload() {
        log.debug("--WebSocket Server[" + port + "] 重启中...");
        super.reload();
        log.debug("--WebSocket Server[" + port + "] 已重启.");
    }

    public void start() {
        log.debug("--Socket Server准备中...");
        String socket_name = this.webSocket ? "WebSocket" : "Socket";
        boss = new NioEventLoopGroup();
        work = new NioEventLoopGroup();
        port = this.webSocket ?
                CommonConfig.getInteger("jees.jsts.websocket.port") : CommonConfig.getInteger("jees.jsts.socket.port");
        ;
        try {
            log.info(socket_name + " Server[" + port + "] 已启动.");
            ServerBootstrap b = new ServerBootstrap();

            b.group(boss, work);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(this.webSocket ? CommonContextHolder.getBean(WebSocketInitializer.class) : CommonContextHolder.getBean(SocketInitializer.class));
            b.bind(port).sync().channel().closeFuture().sync();
        } catch (Exception e) {
            String err_string = e.toString();
            if (err_string.indexOf("childHandler") != -1) {
                log.error(socket_name + " Server[" + port + "] WebSocketInitializer实例没有找到。");
            } else {
                log.error(socket_name + " Server[" + port + "] 启动时发生错误:" + e.toString(), e);
            }
        } finally {
            log.error(socket_name + " Server[" + port + "] 停止中...");
            unload();
            log.error(socket_name + " Server[" + port + "] 已停止。");
        }
    }
}
