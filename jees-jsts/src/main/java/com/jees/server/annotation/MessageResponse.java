package com.jees.server.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target( {ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MessageResponse {
	int value() default 0;
}
