package com.on36.haetae.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.on36.haetae.api.http.MediaType;

@Target(ElementType.TYPE)
public @interface Produces {

	MediaType[] value();
}