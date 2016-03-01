package com.on36.haetae.server.core.auth;

import io.netty.handler.codec.http.HttpResponse;

import com.on36.haetae.http.request.HttpRequestExt;

public interface IAuthentication {

	boolean auth(HttpRequestExt request, HttpResponse response);
}
