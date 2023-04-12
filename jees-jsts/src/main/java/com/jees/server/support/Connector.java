package com.jees.server.support;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.server.abs.AbsConnectorHandler;
import com.jees.server.abs.AbsNettyDecoder;
import com.jees.server.interf.IChannelHandler;
import com.jees.server.interf.IServerBase;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Component
@Scope(value = "prototype")
public class Connector implements Runnable, IServerBase {
    @Autowired
    IChannelHandler handler;
    private EventLoopGroup work;
    private String host;
    private int port;
    private Channel channel;
    private ChannelHandlerContext ctx;
    private int count;

    public void initialize(String _host, String _port) {
        this.host = _host;
        this.port = Integer.parseInt(_port);
        this.count = 0;
    }

    public void run() {
        this.start();
    }

    @Override
    public void onload() {
        new Thread(this).start();
    }

    @Override
    public void unload() {
        if (work != null) work.shutdownGracefully();
        if (channel != null) this.channel.closeFuture();
        count = 0;
    }

    public void start() {
        log.info("连接指定服务器中，尝试第[" + this.count + "]次...");
        work = new NioEventLoopGroup();
        AbsConnectorHandler server = CommonContextHolder.getBean(AbsConnectorHandler.class);
        server.register(this);
        try {
            Bootstrap b = new Bootstrap();
            b.group(work).channel(NioSocketChannel.class)
                    .handler(clientInitializer(server))
                    .option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = b.connect(host, port);
            this.channel = future.channel();

            future.sync().channel().closeFuture().sync();
            unload();
        } catch (Exception e) {
            log.error("指定服务器[" + host + ":" + port + "] 连接时发生错误:" + e.toString(), e);
            reload();
        }
    }

    @SuppressWarnings("unchecked")
    public void request(Object _msg) {
        handler.response(_msg, ctx);
    }

    @Override
    public void reload() {
        log.info("重新连接指定服务器中，尝试第[" + this.count + "]次...");
        this.unload();

        int retry = CommonConfig.getInteger("jees.jsts.connector.retry.max", 3);
        if (retry == 0 || this.count++ < retry) {
            try {
                Thread.sleep(CommonConfig.getInteger("jees.jsts.connector.retry.rate", 10000));
                this.onload();
            } catch (InterruptedException e) {
            }
        } else {
            try {
                Thread.sleep(CommonConfig.getInteger("jees.jsts.connector.retry.delay", 300000));
                this.onload();
            } catch (InterruptedException e) {
            }
        }
    }

    public ChannelInitializer<SocketChannel> clientInitializer(AbsConnectorHandler _handler) {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel _ch) throws Exception {
                ChannelPipeline pipeline = _ch.pipeline();
                // 自己的逻辑Handler
                pipeline.addLast(CommonContextHolder.getBean(AbsNettyDecoder.class));
                pipeline.addLast(_handler);
            }
        };
    }

    public void onConnect(ChannelHandlerContext _ctx) {
        this.ctx = _ctx;
    }

    public void onDisconnect() {
        this.reload();
    }
}
