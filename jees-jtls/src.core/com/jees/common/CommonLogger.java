package com.jees.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommonLogger {
    private static Logger logger = LoggerFactory.getLogger( CommonLogger.class );

    public static Logger getLogger(){
        return logger;
    }
}
