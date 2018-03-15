package com.jees.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommonLogger {
    private static Logger logger = LoggerFactory.getLogger( CommonLogger.class );

    private static Map< Class , Logger > loggerMap = new HashMap<>();

    private static Logger get( Class<?> _cls ){
        if( loggerMap.containsKey( _cls ) )
            return loggerMap.getOrDefault( _cls, logger );

        Logger clsLogger = LoggerFactory.getLogger( _cls );
        loggerMap.put( _cls, clsLogger );
        return clsLogger;
    }

    public static void debug( Class<?> _cls, String _s ){
        get( _cls ).debug( _s );
    }

    public static void info( Class<?> _cls, String _s ){
        get( _cls ).info( _s );
    }

    public static void warn( Class<?> _cls, String _s ){
        get( _cls ).warn( _s );
    }

    public static void error( Class<?> _cls, String _s ){
        get( _cls ).error( _s );
    }

    public static void error( Class<?> _cls, String _s, Throwable _thr){
        get( _cls ).error( _s, _thr );
    }

    public static void debug( String _s ){
        logger.debug( _s );
    }

    public static void info( String _s ){
        logger.info( _s );
    }

    public static void warn( String _s ){
        logger.warn( _s );
    }

    public static void error( String _s ){
        logger.error( _s );
    }

    public static void error( String _s, Throwable _thr){
        logger.error( _s, _thr );
    }
}
