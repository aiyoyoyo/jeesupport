package com.jees.test.model;

import com.jees.core.socket.support.ISupportHandler;
import com.jees.jsts.server.annotation.MessageLabel;
import com.jees.jsts.server.annotation.MessageRequest;
import com.jees.jsts.server.annotation.MessageResponse;
import com.jees.jsts.server.message.Message;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;

@MessageRequest
@MessageResponse
public class JstsModel {
    @MessageLabel( "测试命令" )
    public static final int   CMD_TEST        = 100;

    @Autowired
    ISupportHandler         handler;
    @SuppressWarnings( "unchecked" )
    @MessageRequest( CMD_TEST )
    public void request0( ChannelHandlerContext _ctx, Message _msg ){
        System.out.println( "--request0" );

        handler.send( _ctx, _msg );
    }

    @MessageResponse( CMD_TEST )
    public void response0( ChannelHandlerContext _ctx, Message _msg ){
        System.out.println( "--response0" );
    }
}
