package com.on36.haetae.api;

import java.util.Set;

import com.on36.haetae.api.http.Session;

public interface Context {

	long getStartHandleTime();

	String getPath();

	String getRequestParameter(String param);

	String getCapturedParameter(String captured);

	String getHeaderValue(String param);

	Set<String> getRequestParameterNames();

	Session getSession();

	String getRequestBodyAsString();

	<T> T getBody(Class<T> clazz);

	String getURI(String resource);

	<T> T getURI(String resource, Class<T> clazz);

}