package com.on36.haetae.server.core.body;

import java.io.IOException;

import com.on36.haetae.api.http.MediaType;
import com.on36.haetae.config.client.json.util.JSONUtils;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

public class StringResponseBody extends ResponseBody {

	private Object body = null;

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

	private String build(HttpResponse response, String contentType) {
		if (MediaType.TEXT_HTML.value().equals(contentType))
			return content();

		StringBuilder sb = new StringBuilder("{");
		sb.append("\"status\":").append(response.getStatus().code())
				.append(",");
		sb.append("\"message\":\"").append(response.getStatus().reasonPhrase())
				.append("\"");
		if (hasContent())
			translate(sb, body);
		sb.append("}");
		return sb.toString();
	}

	private boolean isJSON(String data) {
		return data.startsWith("{") && data.endsWith("}");
	}

	private void translate(StringBuilder sb, Object entity) {
		sb.append(",").append("\"result\":");
		String data = null;
		if (entity.getClass().isPrimitive())
			sb.append(entity.toString());
		else if (entity instanceof String) {
			data = (String) entity;
			if (isJSON(data))
				sb.append(data);
			else
				sb.append("\"").append(data).append("\"");
		} else {
			data = JSONUtils.toJson(entity);
			sb.append(entity.toString());
		}
	}
}
