package com.jees.server.abs;

import com.jees.tool.utils.JsonUtil;

public abstract class AbsMessage{
    public abstract int getId();
    public abstract <T> boolean check( T _msg );
    public abstract <T> void merge( T _msg );

    public String toString(){
        return JsonUtil.toString( this );
    }
}
