package com.jees.jsts.server.abs;

import com.jees.common.CommonConfig;
import com.jees.core.socket.support.ISupportHandler;
import com.jees.jsts.server.annotation.MessageLabel;
import com.jees.jsts.server.message.Message;
import com.jees.jsts.server.message.MessageDecoder;
import com.jees.jsts.server.support.ProxyInterface;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端连接的请求处理器，可以用作通用的消息发送器。
 */
@Log4j2
public abstract class AbsHandlerService<C extends ChannelHandlerContext, M > implements ISupportHandler< C, M >{

    private static Map< Integer, String > labels = new HashMap<>();
    private static Map< Integer, String > errors = new HashMap<>();
    // Handler 部分
    @Override
    public void send( C _ctx , M _msg ) {
        Message m = (Message)_msg;

        boolean handler = CommonConfig.getBoolean("jees.jsts.message.handler.enable", false );
        boolean error = CommonConfig.getBoolean("jees.jsts.message.error.enable", false );

        if( handler ){
            String label = labels.getOrDefault( m.getId(), "未注解命令" );

            if( label.equals( "" ) && error ){
                label = errors.getOrDefault( m.getId(), "未注解命令" );
                label = "\n  [Handler Error][" + label + "]->";
            }else label = "\n  [Handler][" + label + "]->";

            log.debug( label + m.toString() );
        }

        if( m.getType() == Message.TYPE_WEBSOCKET ){
            TextWebSocketFrame tws = new TextWebSocketFrame( MessageDecoder.serializerToJson( m ) );
            _ctx.writeAndFlush( tws );
        }else if( m.getType() == Message.TYPE_SOCKET ){
            final ByteBuf buf = _ctx.alloc().buffer();
            MessageDecoder.buff( buf , m );
            _ctx.writeAndFlush( buf );
        }else if( m.getType() == Message.TYPE_BYTES ){
            final ByteBuf buf = _ctx.alloc().buffer();

            buf.writeInt( m.getId() );
            buf.writeBytes( m.getBytes( 0 ) );

            _ctx.writeAndFlush( buf );
        }
    }

    public void register(){
        _command_labels();
        _command_errors();
    }
    private void _command_labels(){
        if( labels.size() > 0 ) return;
        boolean handler = CommonConfig.getBoolean("jees.jsts.message.handler.enable", false );
        if( !handler ) return;
        String clazz_str = CommonConfig.getString( "jees.jsts.message.handler.clazz" );

        String[] clses = clazz_str.split( "," );
        for( String cls : clses ) {
            try {
                Class c = Class.forName( cls.trim() );

                Object ir = ProxyInterface.newInstance( new Class[]{ c } );
                Field[] fields = c.getDeclaredFields();
                for ( Field f : fields ) {
                    try {
                        labels.put( f.getInt( ir ), f.getAnnotation( MessageLabel.class ).value() );
                    } catch ( Exception e ) {
                        continue;
                    }
                }
            } catch ( Exception e ) {
                log.error( cls + "包含MessageLabel注解的接口发生错误：", e  );
            }
        }
    }
    private void _command_errors(){
        if( errors.size() > 0 ) return;
        boolean error = CommonConfig.getBoolean("jees.jsts.message.error.enable", false );
        if( !error ) return;
        String cls = CommonConfig.getString( "jees.jsts.message.error.clazz" );
        try {
            Class c = Class.forName( cls );

            Object ir = ProxyInterface.newInstance( new Class[]{c});
            Field[] fields = c.getDeclaredFields();

            for ( Field f : fields ) {
                try {
                    errors.put( f.getInt( ir ) , f.getAnnotation( MessageLabel.class ).value() );
                } catch ( Exception e ) {
                    continue;
                }
            }
        } catch ( Exception e ) {
            log.error( "包含MessageLabel注解的接口发生错误：" + cls );
        }
    }
}
