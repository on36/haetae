package com.on36.haetae.http;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;

import com.on36.haetae.http.request.HttpRequestExt;

public interface Container {

	void handle(HttpRequestExt request, HttpResponse response);

	HandlerKey addHandler(RequestHandler handler, HttpMethod method, String resource, String contentType);
	
	HandlerKey addHandler(RequestHandler handler, HttpMethod method, String resource);
}
