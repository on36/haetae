package com.on36.haetae.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.on36.haetae.api.http.MethodType;

/**
 * @author zhanghr
 * @date 2016年3月14日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Api {

	String value();

	MethodType method() default MethodType.GET;

	String version() default "1.0";
}