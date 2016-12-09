package com.on36.haetae.server.core.body;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.SERVER;

import com.on36.haetae.http.Version;

import io.netty.handler.codec.http.HttpResponse;

public abstract class ResponseBody {

	public abstract void sendAndCommit(HttpResponse resp, String contentType);

	public abstract boolean hasContent();

	public abstract String content();

	protected void addStandardHeaders(HttpResponse response,
			String responseContentType) {
		response.headers().set(CONTENT_TYPE, responseContentType);
		response.headers().set(SERVER, "Haetae/" + Version.CURRENT_VERSION);
	}
}
