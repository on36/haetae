package com.on36.haetae.server.core.container;

import io.netty.handler.codec.http.HttpResponseStatus;

import com.on36.haetae.http.HandlerKey;
import com.on36.haetae.http.route.Route;
import com.on36.haetae.server.core.RequestHandlerImpl;

public class ResolvedRequest {

	public Route route;
	public RequestHandlerImpl handler;
	public HandlerKey key;
	public HttpResponseStatus errorStatus;
	public String contentType;
	public String warn;
}
