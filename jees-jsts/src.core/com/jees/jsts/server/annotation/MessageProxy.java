package com.jees.jsts.server.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target( { ElementType.TYPE })
@Retention( RetentionPolicy.RUNTIME )
@Documented
@Component
public @interface MessageProxy {
    int[] value() default 0;
}
