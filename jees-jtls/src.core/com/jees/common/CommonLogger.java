package com.jees.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Deprecated
public class CommonLogger {
    private static Logger logger = LoggerFactory.getLogger( CommonLogger.class );
    private static Map< Class, Logger > loggerMap = new HashMap<>();
    public static Logger getLogger(){
        return logger;
    }

    public static Logger getLogger( Class _cls ){
        if( loggerMap.containsKey( _cls ) ){
            return loggerMap.getOrDefault( _cls, logger );
        }

        Logger tmp_logger = LoggerFactory.getLogger( _cls );
        loggerMap.put( _cls, tmp_logger );
        return tmp_logger;
    }
}
