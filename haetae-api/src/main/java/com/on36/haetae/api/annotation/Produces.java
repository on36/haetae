package com.on36.haetae.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.on36.haetae.api.http.MediaType;

@Deprecated
@Target(ElementType.METHOD)
public @interface Produces {

	MediaType[] value() default MediaType.APPLICATION_JSON;
}