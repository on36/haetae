package com.on36.haetae.http;

import com.on36.haetae.http.request.HttpRequestExt;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public interface Container {

	void handle(HttpRequestExt request, HttpResponse response);

	void handle(FullHttpRequest request, ChannelHandlerContext ctx);

	RequestHandler findHandler(String resource, String methodName,
			String version);

	boolean removeHandler(String resource, String methodName, String version);

	boolean addHandler(RequestHandler handler, String methodName,
			String resource, String version, String contentType);

	Scheduler getScheduler();
}
