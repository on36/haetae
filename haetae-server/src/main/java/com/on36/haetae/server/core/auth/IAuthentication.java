package com.on36.haetae.server.core.auth;

import com.on36.haetae.http.request.HttpRequestExt;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public interface IAuthentication {

	HttpResponseStatus auth(HttpRequestExt request, HttpResponse response);
}
