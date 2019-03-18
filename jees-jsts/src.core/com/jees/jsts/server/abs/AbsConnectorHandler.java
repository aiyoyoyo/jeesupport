package com.jees.jsts.server.abs;

import com.jees.jsts.server.interf.IConnectroHandler;
import com.jees.jsts.server.interf.IRequestHandler;
import com.jees.jsts.server.support.Connector;
import com.jees.jsts.server.support.SessionService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用作服务器通讯客户端的连接器，需要自行处理相关事件
 */
public abstract class AbsConnectorHandler extends ChannelInboundHandlerAdapter implements IConnectroHandler {

    Connector connector;

    @Override
    public void register ( Connector _connector ) {
        this.connector = _connector;
    }

    @Override
    public void channelRegistered( ChannelHandlerContext _ctx ) throws Exception {
        super.channelRegistered( _ctx );
        connector.onConnect( _ctx );
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext _ctx) throws Exception {
        super.channelUnregistered( _ctx );
        connector.onDisconnect();
    }

    @Autowired
    IRequestHandler request;
    @Autowired
    SessionService session;

    @SuppressWarnings( "unchecked" )
    @Override
    public void channelRead(ChannelHandlerContext _ctx , Object _obj ) {
        request.request( _ctx , _obj, session.isWebSocket( _ctx ) );
    }
}
