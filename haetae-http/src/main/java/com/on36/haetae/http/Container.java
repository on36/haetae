package com.on36.haetae.http;

import com.on36.haetae.http.request.HttpRequestExt;

import io.netty.handler.codec.http.HttpResponse;

public interface Container {

	void handle(HttpRequestExt request, HttpResponse response);

	RequestHandler findHandler(String resource, String methodName,
			String version);

	boolean removeHandler(String resource, String methodName, String version);

	boolean addHandler(RequestHandler handler, String methodName,
			String resource, String version, String contentType);

	Scheduler getScheduler();
}
