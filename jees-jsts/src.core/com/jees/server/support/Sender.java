package com.jees.server.support;

import com.jees.server.abs.AbsMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;

@Log4j2
@Component
@Scope( value = "prototype" )
public class Sender{
    Queue< Object > queue = new LinkedList<>();

    public void insert( Object _obj ){
        if( _obj == null ){
            return;
        }
        Optional< Object > finder = queue.stream().filter( o->o.equals( _obj ) ).findFirst();
        if( finder.isPresent() ){
            return;
        }
        // 针对多次处理的非Message类型消息做合并
        if( _obj instanceof AbsMessage ){
            AbsMessage tmp = ( AbsMessage ) _obj;
            finder = queue.stream().filter( o->{
                if( o instanceof AbsMessage ){
                    return ( ( AbsMessage ) o ).check( tmp );
                }
                return false;
            } ).findFirst();
            if( finder.isPresent() ){
                AbsMessage msg = ( AbsMessage ) finder.get();
                msg.merge( tmp );
            }else{
                queue.offer( _obj );
            }
        }else{
            queue.offer( _obj );
        }
    }

    public void clear(){
        queue.clear();
    }

    public void flush( Consumer _action ){
        if( queue.size() == 0 ){
            return;
        }

        queue.forEach( o->{
            _action.accept( o );
        } );

        queue.clear();
    }
}
