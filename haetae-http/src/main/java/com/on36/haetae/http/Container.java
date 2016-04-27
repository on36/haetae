package com.on36.haetae.http;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;

import java.util.List;

import com.on36.haetae.http.request.HttpRequestExt;

public interface Container {

	void handle(HttpRequestExt request, HttpResponse response);

	RequestHandler findHandler(String resource);
	
	boolean removeHandler(String resource);
	
	boolean addHandler(RequestHandler handler, String resource);
	
	boolean addHandler(RequestHandler handler, HttpMethod method, String resource, String version);
	
	List<?> getStatistics();
}
