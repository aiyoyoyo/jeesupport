package com.jees.server.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target( {ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MessageLabel {
	String value() default "";
}
