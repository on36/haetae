package com.on36.haetae.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.on36.haetae.api.http.DataType;
import com.on36.haetae.api.http.ParamType;

/**
 * @author zhanghr
 * @date 2016年3月14日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ApiParam {

	String param();

	ParamType type() default ParamType.REQUEST;

	DataType dataType() default DataType.STRING;

	boolean required() default false;

	String testValue() default "";

	String defaultValue() default "";

	String desc();
}