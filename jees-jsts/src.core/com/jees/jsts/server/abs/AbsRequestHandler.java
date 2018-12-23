package com.jees.jsts.server.abs;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.jsts.server.annotation.MessageLabel;
import com.jees.jsts.server.annotation.MessageRequest;
import com.jees.jsts.server.interf.IRequestHandler;
import com.jees.jsts.server.message.Message;
import com.jees.jsts.server.message.MessageDecoder;
import com.jees.jsts.server.message.MessageException;
import com.jees.jsts.server.support.ProxyInterface;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * 客户端的消息处理器
 * @param <C>
 * @param <M>
 */
@Log4j2
public abstract class AbsRequestHandler< C extends ChannelHandlerContext, M > implements IRequestHandler< C, M > {
    private boolean						init;
    private static Map< Integer , Class< ? > > handlerClases  = new HashMap<>();
    private static Map< Integer , Method>		handlerMethod = new HashMap<>();
    private static Map< Integer, String >      labels = new HashMap<>();

    public void register(){
        _command_register();
        _command_labels();
    }
    private void _command_register() {
        log.info( "服务器请求命令配置加载..." );
        Collection< Object > msg_coll = CommonContextHolder.getApplicationContext().getBeansWithAnnotation( MessageRequest.class ).values();
        if( msg_coll.size() == 0 )
            log.debug( "--未找到任何命令配置。" );
        msg_coll.forEach( b -> {
            Method[] mths = b.getClass().getMethods();
            for ( Method m : mths ) {
                MessageRequest mr = AnnotationUtils.findAnnotation( m , MessageRequest.class );
                if( mr != null ){
                    int cmd = mr.value();
                    if ( handlerClases.containsKey( cmd ) ) {
                        log.warn( "已存在相同的命令处理对象：CMD[" + cmd + "], MTH=[" + m.getName() + "]" );
                    } else {
                        handlerClases.put( cmd , b.getClass() );
                        handlerMethod.put( cmd , m );
                        log.debug( "--配置服务器命令: CMD[" + cmd + "], MTH=[" + m.getName() + "]" );
                    }
                }

            }
        } );
    }

    private void _command_labels(){
        if( labels.size() > 0 ) return;
        boolean request = CommonConfig.getBoolean("jees.jsts.message.request.enable", false );
        if( !request ) return;
        String cls = CommonConfig.getString( "jees.jsts.message.request.clazz" );
        try {
            Class c = Class.forName( cls );

            Object ir = ProxyInterface.newInstance( new Class[]{c});
            Field[] fields = c.getDeclaredFields();
            for ( Field f : fields ) {
                try {
                    labels.put( f.getInt( ir ) , f.getAnnotation( MessageLabel.class ).value() );
                } catch ( Exception e ) {
                    continue;
                }
            }
        } catch ( Exception e ) {
            log.error( "包含MessageLabel注解的接口发生错误：" + cls );
        }
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public void handler( C _ctx , Object _obj ) {
        Message msg = MessageDecoder.deserializer( _obj );

        if( msg == null ){
            exit( _ctx );
            return;
        }

        boolean debug = CommonConfig.getBoolean("jees.jsts.message.request.enable", false );

        if( debug ){
            String label = labels.getOrDefault( msg.getId(), "未注解命令" );
            log.debug( "\n  [Request][" + label + "]->" + msg.toString() );
        }

        if( before( _ctx, (M)msg ) ) {
            int cmd = msg.getId();
            if ( handlerClases.containsKey( cmd ) ) {
                Method m = handlerMethod.get( cmd );
                try{
                    ReflectionUtils.invokeMethod( m , CommonContextHolder.getBean( handlerClases.get( cmd ) ), _ctx , msg );
                    after( _ctx );
                } catch ( MessageException me ) {
                    //程序异常，可以通知给客户端
                    log.error( "错误ME: I=[" + cmd + "] M=[" + m.getName() + "]:" + me.getMessage() );

                    me.getMsg().setType( msg.getType() );
                    error( _ctx, me );
                    // 后续两种错误不应该发生。RE为数据库操作错误,EX为程序错误
                } catch ( RuntimeException re ) {
                    log.error( "错误RE: I=[" + cmd + "] M=[" + m.getName() + "]" , re );
                } catch ( Exception ex ) {
                    log.error( "错误EX: I=[" + cmd + "] M=[" + m.getName() + "]" , ex );
                }
            } else {
                log.warn( "命令没有注册：CMD=[" + cmd + "]" );
                unregist( _ctx, (M)msg );
            }
        }
    }

    public boolean validateId( int _id ){
        return handlerClases.containsKey( _id );
    }
}
