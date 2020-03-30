package com.jees.jsts.server.abs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.jsts.server.annotation.MessageLabel;
import com.jees.jsts.server.annotation.MessageRequest;
import com.jees.jsts.server.interf.IRequestHandler;
import com.jees.jsts.server.message.Message;
import com.jees.jsts.server.message.MessageCrypto;
import com.jees.jsts.server.message.MessageException;
import com.jees.jsts.server.message.MessageFile;
import com.jees.jsts.server.support.ProxyInterface;
import com.jees.jsts.server.support.SessionService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端的消息处理器
 * @param <C>
 */
@Log4j2
public abstract class AbsRequestHandler< C extends ChannelHandlerContext > implements IRequestHandler< C > {

    private static Map< Integer , Class< ? > > handlerClases  = new HashMap<>();
    private static Map< Integer , Method>	handlerMethod = new HashMap<>();
    private static Map< Integer, String >   labels = new HashMap<>();

    public void register(){
        _command_register();
        _command_labels();

        MessageCrypto.registProxy();
    }
    private void _command_register() {
        log.debug( "@MessageRequest命令注册..." );
        Collection< Object > msg_coll = CommonContextHolder.getApplicationContext().getBeansWithAnnotation( MessageRequest.class ).values();
        if( msg_coll.size() == 0 )
            log.debug( "--未找到任何包含@MessageRequest类。" );
        msg_coll.forEach( b -> {
            Method[] mths = b.getClass().getMethods();
            String cls_name = b.getClass().getName();
            for ( Method m : mths ) {
                MessageRequest mr = AnnotationUtils.findAnnotation( m , MessageRequest.class );
                if( mr != null ){
                    int cmd = mr.value();
                    if ( handlerClases.containsKey( cmd ) ) {
                        String use_mth = handlerClases.get( cmd ).getName()  + "." + handlerMethod.get( cmd ).getName();

                        log.warn( "命令重复：CMD[" + cmd + "], 当前[" + cls_name + "." + m.getName() + "], 已使用[" + use_mth + "]" );
                    } else {
                        handlerClases.put( cmd , b.getClass() );
                        handlerMethod.put( cmd , m );
                        log.debug( "--注册@MessageRequest命令: CMD[" + cmd + "], MTH=[" + m.getName() + "], CLS=[" + cls_name + "]" );
                    }
                }
            }
        } );
        log.debug( "@MessageRequest命令注册成功：SIZE[" + handlerMethod.size() + "]" );
    }

    private void _command_labels(){
        if( labels.size() > 0 ) return;
        boolean request = CommonConfig.getBoolean("jees.jsts.message.request.enable", false );
        if( !request ) return;
        String clazz_str = CommonConfig.getString( "jees.jsts.message.request.clazz" );

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

    @Autowired
    SessionService session;

    @Override
    public void request( C _ctx , Object _obj, boolean _ws ){
        long time = System.currentTimeMillis();
        if( _obj != null ){
            this.handler( _ctx , _obj );
        }else{
            session.leave( _ctx );
        }
        if( CommonConfig.getBoolean( "jees.jsts.message.monitor", false ) ){
            time = System.currentTimeMillis() - time;
            log.debug( "--命令用时: TIME=[" + time + "]"  );
        }
        _ctx.flush();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public void handler( C _ctx , Object _obj ) {
        Object msg = MessageCrypto.deserializer( _obj, session.isWebSocket( _ctx ) );

        boolean debug = CommonConfig.getBoolean( "jees.jsts.message.request.enable", false );
        boolean exception = CommonConfig.getBoolean( "jees.jsts.message.request.exception", false );

        Integer cmd = null;
        Long usr = ( Long ) session.findID( _ctx );
        if( msg instanceof Message ){
            Message m = ( Message ) msg;
            cmd = ( m ).getId();
            m.setUserId( usr );
            MessageFile.write( cmd, usr,  m, false );
        }else if( msg instanceof JSONObject ){
            JSONObject job = ( JSONObject ) msg;
            cmd = ( job ).getInteger( "id" );
            MessageFile.write( cmd, usr,  job, false );
        }else{
            // 代理类
            String json = msg.toString();
            cmd = JSON.parseObject( json ).getInteger( "id" );
            MessageFile.write( cmd, usr,  json, false );
        }

        if ( debug ) {
            String label = labels.getOrDefault( cmd, "未注解命令" );
            log.info( "\n  [C][" + usr + "][" + cmd + "][" + label + "]:" + msg.toString() );
        }

        if( before( _ctx, cmd ) ) {
            if ( handlerClases.containsKey( cmd ) ) {
                Method m = handlerMethod.get( cmd );
                try{
                    ReflectionUtils.invokeMethod( m , CommonContextHolder.getBean( handlerClases.get( cmd ) ), _ctx , msg );
                } catch ( MessageException me ) {
                    //程序异常，可以通知给客户端
                    if( exception ){
                        log.error( "错误ME: U=[" + usr + "] I=[" + cmd + "] M=[" + m.getName() + "]:" + me.getMessage(), me );
                    }else{
                        log.error( "错误ME: U=[" + usr + "] I=[" + cmd + "] M=[" + m.getName() + "]:" + me.getMessage() );
                    }

                    me.getMsg().setRequest( cmd );
                    error( _ctx, me );
                    // 后续两种错误不应该发生。RE为数据库操作错误,EX为程序错误
                } catch ( RuntimeException re ) {
                    log.error( "错误RE: U=[" + usr + "] I=[" + cmd + "] M=[" + m.getName() + "]" , re );
                } catch ( Exception ex ) {
                    log.error( "错误EX: U=[" + usr + "] I=[" + cmd + "] M=[" + m.getName() + "]" , ex );
                } finally {
                    after( _ctx );
                }
            } else {
                log.warn( "命令没有注册：U=[" + usr + "] CMD=[" + cmd + "]" );
                unregist( _ctx, msg );
            }
        }
    }
}
