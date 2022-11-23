package com.jees.test.proxy;

import com.alibaba.fastjson2.JSON;
import com.jees.server.annotation.MessageProxy;
import lombok.Getter;
import lombok.Setter;

@MessageProxy( 101 )
@Getter
@Setter
public class CustomMessage {
    int id = 101;

    String str;

    @Override
    public String toString () {
        return JSON.toJSONString( this );
    }
}
