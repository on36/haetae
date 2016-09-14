package com.on36.haetae.server.core.body;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

public class StringResponseBody extends ResponseBody {

	protected Object body = null;

	public StringResponseBody(Object body) {
		this.body = body;
	}

	public boolean hasContent() {
		return body != null && body.toString().trim().length() > 0;
	}

	public String content() {
		if (body == null)
			return "";
		return body.toString();
	}

	@Override
	public void sendAndCommit(HttpResponse response, String contentType) {
		try {
			printBody(response, contentType);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void printBody(HttpResponse response, String contentType)
			throws IOException {

		addStandardHeaders(response, contentType);

		String result = build(response, contentType);
		if (response instanceof HttpContent) {
			HttpContent httpContent = (HttpContent) response;
			ByteBuf content = httpContent.content();
			content.writeBytes(result.getBytes(CharsetUtil.UTF_8));
		}
	}

	protected String build(HttpResponse response, String contentType) {
		return content();
	}
}
