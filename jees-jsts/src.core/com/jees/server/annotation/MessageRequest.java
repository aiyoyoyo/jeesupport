package com.jees.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Target( {ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MessageRequest{
	int value() default 0;
}
