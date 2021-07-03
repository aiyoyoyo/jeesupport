package com.jees.server.abs;

import com.jees.server.interf.IChannelHandler;
import com.jees.server.interf.IConnectorHandler;
import com.jees.server.support.Connector;
import com.jees.server.support.Session;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用作服务器通讯客户端的连接器，需要自行处理相关事件
 */
public abstract class AbsConnectorHandler extends ChannelInboundHandlerAdapter implements IConnectorHandler{
    Connector connector;

    @Override
    public void register ( Connector _connector ) {
        this.connector = _connector;
    }

    @Override
    public void channelRegistered( ChannelHandlerContext _net ) throws Exception {
        super.channelRegistered( _net );
        connector.onConnect( _net );
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext _net) throws Exception {
        super.channelUnregistered( _net );
        connector.onDisconnect();
    }

    @Autowired
    IChannelHandler handler;
    @Autowired
    Session         session;

    @SuppressWarnings( "unchecked" )
    @Override
    public void channelRead(ChannelHandlerContext _net , Object _obj ) {
        handler.request( _net , _obj );
    }
}
