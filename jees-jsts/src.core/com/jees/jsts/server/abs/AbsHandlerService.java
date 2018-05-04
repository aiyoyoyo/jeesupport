package com.jees.jsts.server.abs;

import com.jees.core.socket.support.ISupportHandler;
import com.jees.jsts.server.message.Message;
import com.jees.jsts.server.message.MessageDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.log4j.Log4j2;

/**
 * 客户端连接的请求处理器，可以用作通用的消息发送器。
 */
@Log4j2
public abstract class AbsHandlerService<C extends ChannelHandlerContext, M > implements ISupportHandler< C, M >{
    // Handler 部分
    @Override
    public void send( C _ctx , M _msg ) {
        Message m = (Message)_msg;
        if( m.getType() == Message.TYPE_WEBSOCKET ){
            TextWebSocketFrame tws = new TextWebSocketFrame( m.dataString() );
            _ctx.writeAndFlush( tws );
        }else{
            final ByteBuf buf = _ctx.alloc().buffer();
            MessageDecoder.buff( buf , m );
            _ctx.writeAndFlush( buf );
        }
    }
}
