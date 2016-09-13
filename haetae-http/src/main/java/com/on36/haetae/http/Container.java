package com.on36.haetae.http;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;

import java.util.List;

import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.net.udp.Scheduler;

public interface Container {

	void handle(HttpRequestExt request, HttpResponse response);

	RequestHandler findHandler(String resource, String methodName,
			String version);

	boolean removeHandler(String resource, String methodName,
			String version);

	boolean addHandler(RequestHandler handler, HttpMethod method,
			String resource, String version, String contentType);

	List<?> getStatistics();

	Scheduler getScheduler();
}
