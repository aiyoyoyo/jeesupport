package com.jees.jsts.server.abs;

import com.jees.jsts.server.interf.IConnectroHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.Setter;

/**
 * 用作服务器通讯客户端的连接器，需要自行处理相关事件
 */
public abstract class AbsConnectorHandler extends ChannelInboundHandlerAdapter implements IConnectroHandler {
    @Getter
    String url = "";

    @Setter
    @Getter
    int status = 0;

    public void initialize( String _host, int _port ){
        this.url = _host + ":" + _port;
        this.status = STATUS_DISCONNECT;
    }

    @Override
    public void channelRegistered( ChannelHandlerContext _ctx ) throws Exception {
        super.channelRegistered( _ctx );
        this.status = STATUS_SUCCESS;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext _ctx) throws Exception {
        super.channelUnregistered( _ctx );
        this.status = STATUS_DISCONNECT;
    }
}
