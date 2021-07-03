package com.jees.test.model;

import com.jees.server.annotation.MessageLabel;
import com.jees.server.annotation.MessageRequest;
import com.jees.server.interf.IChannelHandler;
import com.jees.server.message.Message;
import com.jees.test.proxy.CustomMessage;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;

@MessageRequest
public class JstsModel {
    @MessageLabel( "测试命令" )
    public static final int   CMD_PROXY        = 100;

    @MessageLabel( "测试命令" )
    public static final int   CMD_PROTO        = 101;

    @Autowired
    IChannelHandler handler;
    @SuppressWarnings( "unchecked" )
    @MessageRequest( CMD_PROXY )
    public void CMD_PROXY( ChannelHandlerContext _ctx, Message _msg ){
        System.out.println( "CMD_PROXY-->" + _msg.toString() );

        handler.response( _msg, _ctx );
    }

    @MessageRequest( CMD_PROTO )
    public void CMD_PROTO( ChannelHandlerContext _ctx, CustomMessage _msg ){
        System.out.println( "CMD_PROTO-->" + _msg.toString() );

        handler.response( _msg, _ctx );
    }
}
