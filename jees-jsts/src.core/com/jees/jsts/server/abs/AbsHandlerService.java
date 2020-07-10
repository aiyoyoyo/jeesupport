package com.jees.jsts.server.abs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jees.common.CommonConfig;
import com.jees.core.socket.support.ISupportHandler;
import com.jees.jsts.server.annotation.MessageLabel;
import com.jees.jsts.server.interf.IRequestHandler;
import com.jees.jsts.server.message.Message;
import com.jees.jsts.server.message.MessageCrypto;
import com.jees.jsts.server.message.MessageFile;
import com.jees.jsts.server.support.ProxyInterface;
import com.jees.jsts.server.support.SessionService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端连接的请求处理器，可以用作通用的消息发送器。
 */
@Log4j2
public abstract class AbsHandlerService<C extends ChannelHandlerContext > implements ISupportHandler< C >{

    private static Map< Integer, String > labels = new HashMap<>();
    private static Map< Integer, String > errors = new HashMap<>();

    @Autowired
    SessionService  session;
    @Autowired
    IRequestHandler request;

    /**
     * 服务器写回给客户端
     *
     * @param _obj 数据内容
     * @param _ctx 客户端对象
     */
    @Override
    public void send( Object _obj, C _ctx ){
        boolean proxy = CommonConfig.getBoolean( "jees.jsts.message.proxy", true );

        boolean debug = CommonConfig.getBoolean( "jees.jsts.message.handler.enable", false );
        boolean error = CommonConfig.getBoolean( "jees.jsts.message.error.enable", false );

        int cmd;
        Long usr = ( Long ) session.findID( _ctx );
        if( proxy ){
            Message msg = JSON.parseObject( _obj.toString(), Message.class );
            cmd = msg.getId();
            MessageFile.write( cmd, usr, msg, true );
        }else{
            JSONObject obj = JSON.parseObject( _obj.toString() );

            try{
                cmd = obj.getInteger( "id" );
            }catch( Exception e ){
                cmd = 0;
            }
            MessageFile.write( cmd, usr, obj, true );
        }
        if( debug ){
            String label = labels.getOrDefault( cmd, "未注解命令" );
            if( label.equals( "" ) && error ){
                label = errors.getOrDefault( cmd, "未注解命令" );
                label = "\n  [S][ERR][" + cmd + "][" + label + "][" + usr + "]:";
            }else label = "\n  [S][" + cmd + "][" + label + "][" + usr + "]:";
            log.info( label + _obj.toString() );
        }

        final ByteBuf buf = _ctx.alloc().buffer();
        Object        msg = MessageCrypto.serializer( buf, _obj, session.isWebSocket( _ctx ) );
        _ctx.writeAndFlush( msg );
        if( buf.refCnt() == 1){
            log.debug( "--内存检测，判定再次释放：" + System.identityHashCode( buf ) );
            buf.release();
        }
    }

    public void register(){
        _command_labels();
        _command_errors();
    }

    private void _command_labels(){
        if( labels.size() > 0 ) return;
        boolean handler = CommonConfig.getBoolean( "jees.jsts.message.handler.enable", false );
        if( !handler ) return;
        String clazz_str = CommonConfig.getString( "jees.jsts.message.handler.clazz" );

        String[] clses = clazz_str.split( "," );
        for( String cls : clses ){
            try{
                Class c = Class.forName( cls.trim() );

                Object  ir     = ProxyInterface.newInstance( new Class[]{c} );
                Field[] fields = c.getDeclaredFields();
                for( Field f : fields ){
                    try{
                        labels.put( f.getInt( ir ), f.getAnnotation( MessageLabel.class ).value() );
                    }catch( Exception e ){
                        continue;
                    }
                }
            }catch( Exception e ){
                log.error( cls + "包含MessageLabel注解的接口发生错误：", e );
            }
        }
    }

    private void _command_errors(){
        if( errors.size() > 0 ) return;
        boolean error = CommonConfig.getBoolean( "jees.jsts.message.error.enable", false );
        if( !error ) return;
        String cls = CommonConfig.getString( "jees.jsts.message.error.clazz" );
        try{
            Class c = Class.forName( cls );

            Object  ir     = ProxyInterface.newInstance( new Class[]{c} );
            Field[] fields = c.getDeclaredFields();

            for( Field f : fields ){
                try{
                    errors.put( f.getInt( ir ), f.getAnnotation( MessageLabel.class ).value() );
                }catch( Exception e ){
                    continue;
                }
            }
        }catch( Exception e ){
            log.error( "包含MessageLabel注解的接口发生错误：" + cls );
        }
    }

    @Override
    public void receive( ChannelHandlerContext _ctx, Object _obj ){
        request.request( _ctx, _obj, session.isWebSocket( _ctx ) );
    }

    @Override
    public void enter( ChannelHandlerContext _ctx, boolean _ws ){
        session.connect( _ctx, _ws );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public void leave( ChannelHandlerContext _ctx ){
        session.leave( _ctx );
    }

    @Override
    public void standby( ChannelHandlerContext _ctx ){
        session.standby( _ctx );
    }

    @Override
    public void recovery( ChannelHandlerContext _ctx ){
        session.recovery( _ctx );
    }
}
